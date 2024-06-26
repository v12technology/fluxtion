package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.subscription.EventToInvokeStrategy;
import lombok.extern.java.Log;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.logging.Logger;


@Experimental
@Log
public class EventQueueToEventProcessorAgent implements EventQueueToEventProcessor {

    private final OneToOneConcurrentArrayQueue<?> inputQueue;
    private final EventToInvokeStrategy eventToInvokeStrategy;
    private final String name;
    private final Logger logger;


    public EventQueueToEventProcessorAgent(
            OneToOneConcurrentArrayQueue<?> inputQueue,
            EventToInvokeStrategy eventToInvokeStrategy,
            String name) {
        this.inputQueue = inputQueue;
        this.eventToInvokeStrategy = eventToInvokeStrategy;
        this.name = name;

        logger = Logger.getLogger("EventQueueToEventProcessorAgent." + name);
    }

    @Override
    public void onStart() {
        logger.info("start");
    }

    @Override
    public int doWork() throws Exception {
        Object event = inputQueue.poll();
        if (event != null) {
            eventToInvokeStrategy.processEvent(event);
            return 1;
        }
        return 0;
    }

    @Override
    public void onClose() {
        logger.info("onClose");
    }

    @Override
    public String roleName() {
        return name;
    }

    @Override
    public int registerProcessor(StaticEventProcessor eventProcessor) {
        logger.info("registerProcessor" + eventProcessor);
        eventToInvokeStrategy.registerProcessor(eventProcessor);
        return listenerCount();
    }

    @Override
    public int deregisterProcessor(StaticEventProcessor eventProcessor) {
        logger.info("deregisterProcessor" + eventProcessor);
        eventToInvokeStrategy.deregisterProcessor(eventProcessor);
        return listenerCount();
    }

    @Override
    public int listenerCount() {
        return eventToInvokeStrategy.listenerCount();
    }
}
