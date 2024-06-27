package com.fluxtion.runtime.server;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.dutycycle.ComposingEventProcessorAgent;
import com.fluxtion.runtime.server.service.DeadWheelScheduler;
import com.fluxtion.runtime.server.subscription.*;
import com.fluxtion.runtime.service.Service;
import lombok.Value;
import lombok.extern.java.Log;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.AtomicCounter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Experimental
@Log
public class FluxtionServer {

    private final EventFlowManager flowManager = new EventFlowManager();
    private final ConcurrentHashMap<String, ComposingAgentRunner> composingAgents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Service<?>> registeredServices = new ConcurrentHashMap<>();

    public void registerEventMapperFactory(Supplier<EventToInvokeStrategy> eventMapper, CallBackType type) {
        log.info("registerEventMapperFactory:" + eventMapper);
        flowManager.registerEventMapperFactory(eventMapper, type);
    }

    public <T> void registerEventSource(String sourceName, EventSource<T> eventSource) {
        log.info("registerEventSource name:" + sourceName + " eventSource:" + eventSource);
        flowManager.registerEventSource(sourceName, eventSource);
    }

    public void registerService(Service<?>... services) {
        for (Service<?> service : services) {
            String serviceName = service.serviceName();
            log.info("registerService:" + service);
            if (registeredServices.containsKey(serviceName)) {
                throw new IllegalArgumentException("cannot register service name is already assigned:" + serviceName);
            }
            registeredServices.put(serviceName, service);
            Object instance = service.instance();
            if (instance instanceof EventFlowService) {
                ((EventFlowService) instance).setEventFlowManager(flowManager, serviceName);
            }
        }
    }

    public void init() {
        log.info("init");
        registeredServices.values().forEach(svc -> {
            if (!(svc.instance() instanceof LifeCycleEventSource)) {
                svc.init();
            }
        });
        flowManager.init();
    }

    public void start() {
        log.info("start");
        registeredServices.values().forEach(svc -> {
            if (!(svc.instance() instanceof LifeCycleEventSource)) {
                svc.start();
            }
        });
        flowManager.start();
        composingAgents.forEach((k, v) -> {
            log.info("starting composing agent " + k);
            AgentRunner.startOnThread(v.getGroupRunner());
        });
    }

    public void addEventProcessor(String groupName, Supplier<StaticEventProcessor> feedConsumer) {
        ComposingAgentRunner composingAgentRunner = composingAgents.computeIfAbsent(
                groupName,
                ket -> {
                    //build a subscriber group
                    ComposingEventProcessorAgent group = new ComposingEventProcessorAgent(groupName, flowManager, new DeadWheelScheduler(), registeredServices);
                    //threading to be configured by file
                    IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(100);
                    ErrorHandler errorHandler = m -> log.severe(m.getMessage());
                    AtomicCounter errorCounter = new AtomicCounter(new UnsafeBuffer(new byte[4096]), 0);
                    //run subscriber group
                    AgentRunner groupRunner = new AgentRunner(
                            idleStrategy,
                            errorHandler,
                            errorCounter,
                            group);
                    return new ComposingAgentRunner(group, groupRunner);
                });

        composingAgentRunner.getGroup().addEventFeedConsumer(feedConsumer);
    }

    @Value
    private static class ComposingAgentRunner {
        ComposingEventProcessorAgent group;
        AgentRunner groupRunner;
    }
}
