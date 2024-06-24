package com.fluxtion.runtime.server;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.server.dutycycle.ComposingEventProcessorAgent;
import com.fluxtion.runtime.server.subscription.CallBackType;
import com.fluxtion.runtime.server.subscription.EventFlowManager;
import com.fluxtion.runtime.server.subscription.EventSource;
import com.fluxtion.runtime.server.subscription.EventToInvokeStrategy;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.AtomicCounter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Experimental
@Log4j2
public class FluxtionServer {

    private final EventFlowManager flowManager = new EventFlowManager();
    private final ConcurrentHashMap<String, ComposingAgentRunner> composingAgents = new ConcurrentHashMap<>();

    public void registerEventMapperFactory(Supplier<EventToInvokeStrategy> eventMapper, CallBackType type) {
        log.info("registerEventMapperFactory:{}", eventMapper);
        flowManager.registerEventMapperFactory(eventMapper, type);
    }

    public <T> void registerEventSource(String sourceName, EventSource<T> eventSource) {
        log.info("registerEventSource name:{} eventSource:{}", sourceName, eventSource);
        flowManager.registerEventSource(sourceName, eventSource);
    }

    public void init() {
        log.info("init");
        flowManager.init();
    }

    public void start() {
        log.info("start");
        flowManager.start();
        composingAgents.forEach((k, v) -> {
            log.info("starting composing agent {}", k);
            AgentRunner.startOnThread(v.getGroupRunner());
        });
    }

    public void addEventProcessor(String groupName, Consumer<EventFeed<?>> feedConsumer) {
        ComposingAgentRunner composingAgentRunner = composingAgents.computeIfAbsent(
                groupName,
                ket -> {
                    //build a subscriber group
                    ComposingEventProcessorAgent group = new ComposingEventProcessorAgent(groupName, flowManager);
                    //threading to be configured by file
                    IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(100);
                    ErrorHandler errorHandler = log::error;
                    AtomicCounter errorCounter = new AtomicCounter(new UnsafeBuffer(new byte[4096]), 0);
                    //run subscriber group
                    AgentRunner groupRunner = new AgentRunner(
                            idleStrategy,
                            errorHandler,
                            errorCounter,
                            group);
                    return new ComposingAgentRunner(group, groupRunner);
                });

        composingAgentRunner.getGroup().addEventConsumer(feedConsumer);
    }

    @Value
    private static class ComposingAgentRunner {
        ComposingEventProcessorAgent group;
        AgentRunner groupRunner;
    }
}
