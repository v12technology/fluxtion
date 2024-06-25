package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.input.EventFeed;
import com.fluxtion.runtime.server.subscription.EventFlowManager;
import com.fluxtion.runtime.server.subscription.EventSubscriptionKey;
import lombok.extern.java.Log;
import org.agrona.concurrent.DynamicCompositeAgent;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 */
@Experimental
@Log
public class ComposingEventProcessorAgent extends DynamicCompositeAgent implements EventFeed<EventSubscriptionKey<?>> {

    private final EventFlowManager eventFlowManager;
    private final Map<EventSubscriptionKey<?>, EventQueueToEventProcessor> queueProcessorMap = new HashMap<>();
    private final OneToOneConcurrentArrayQueue<Consumer<EventFeed<?>>> toStartList = new OneToOneConcurrentArrayQueue<>(128);

    public ComposingEventProcessorAgent(String roleName, EventFlowManager eventFlowManager) {
        super(roleName);
        this.eventFlowManager = eventFlowManager;
    }

    public void addEventFeedConsumer(Consumer<EventFeed<?>> initFunction) {
        toStartList.add(initFunction);
    }

    @Override
    public void onStart() {
        log.info("onStart");
        super.onStart();
    }

    @Override
    public int doWork() throws Exception {
        toStartList.drain(init -> init.accept(this));
        return super.doWork();
    }

    @Override
    public void onClose() {
        log.info("onClose");
        super.onClose();
    }

    @Override
    public void registerSubscriber(StaticEventProcessor subscriber) {
        log.info("registerSubscriber:" + subscriber);
    }

    @Override
    public void subscribe(StaticEventProcessor subscriber, EventSubscriptionKey<?> subscriptionKey) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        Objects.requireNonNull(subscriptionKey, "subscriptionKey is null");
        log.info("subscribe subscriptionKey:" + subscriptionKey + " subscriber:" + subscriber);
        EventQueueToEventProcessor eventQueueToEventProcessor = queueProcessorMap.get(subscriptionKey);
        if (eventQueueToEventProcessor == null) {
            eventQueueToEventProcessor = eventFlowManager.getMappingAgent(subscriptionKey, this);
            queueProcessorMap.put(subscriptionKey, eventQueueToEventProcessor);
            tryAdd(eventQueueToEventProcessor);
        }
        eventQueueToEventProcessor.registerProcessor(subscriber);
        eventFlowManager.subscribe(subscriptionKey);
    }

    @Override
    public void unSubscribe(StaticEventProcessor subscriber, EventSubscriptionKey<?> subscriptionKey) {
        if (queueProcessorMap.containsKey(subscriptionKey)) {
            EventQueueToEventProcessor eventQueueToEventProcessor = queueProcessorMap.get(subscriptionKey);
            if (eventQueueToEventProcessor.deregisterProcessor(subscriber) == 0) {
                log.info("EventQueueToEventProcessor listener count = 0, removing subscription:" + subscriptionKey);
                queueProcessorMap.remove(subscriptionKey);
                eventFlowManager.unSubscribe(subscriptionKey);
            }
        }
    }

    @Override
    public void removeAllSubscriptions(StaticEventProcessor subscriber) {

    }
}
