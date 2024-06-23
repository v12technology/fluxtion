package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.server.dutycycle.EventQueueToEventProcessor;
import com.fluxtion.runtime.server.dutycycle.EventQueueToEventProcessorAgent;
import lombok.Value;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Manages mapping between:
 * <ul>
 *     <li>{@link EventSource} - pushed events into a queue</li>
 *     <li>{@link EventQueueToEventProcessor} -  reads from a queue and handles multiplexing to registered {@link com.fluxtion.runtime.StaticEventProcessor}</li>
 *     <li>{@link EventToInvokeStrategy} - processed an event and map events to callbacks on the {@link com.fluxtion.runtime.StaticEventProcessor}</li>
 * </ul>
 */
public class EventFlowManager {

    private final ConcurrentHashMap<EventSourceKey<?>, EventSource_QueuePublisher<?>> eventSourceToQueueMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<CallBackType, Supplier<EventToInvokeStrategy>> eventToInvokerFactoryMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<EventSourceKey_Subscriber<?>, OneToOneConcurrentArrayQueue<?>> subscriberKeyToQueueMap = new ConcurrentHashMap<>();

    public EventFlowManager() {
        eventToInvokerFactoryMap.put(CallBackType.StandardCallbacks.ON_EVENT, EventToOnEventInvokeStrategy::new);
    }

    @SuppressWarnings("unchecked")
    public void subscribe(EventSubscriptionKey<?> subscriptionKey) {
        Objects.requireNonNull(subscriptionKey, "subscriptionKey must be non-null");

        EventSource_QueuePublisher<?> eventSourceQueuePublisher = eventSourceToQueueMap.get(subscriptionKey.getEventSourceKey());
        Objects.requireNonNull(eventSourceQueuePublisher, "no EventSource registered for EventSourceKey:" + subscriptionKey);
        eventSourceQueuePublisher.getEventSource().subscribe((EventSubscriptionKey) subscriptionKey);
    }

    @SuppressWarnings("unchecked")
    public void unSubscribe(EventSubscriptionKey<?> subscriptionKey) {
        Objects.requireNonNull(subscriptionKey, "subscriptionKey must be non-null");

        EventSource_QueuePublisher<?> eventSourceQueuePublisher = eventSourceToQueueMap.get(subscriptionKey.getEventSourceKey());
        Objects.requireNonNull(eventSourceQueuePublisher, "no EventSource registered for EventSourceKey:" + subscriptionKey);
        eventSourceQueuePublisher.getEventSource().unSubscribe((EventSubscriptionKey) subscriptionKey);
    }

    @SuppressWarnings("unchecked")
    public <T> void registerEventSource(String sourceName, EventSource<T> eventSource) {
        Objects.requireNonNull(eventSource, "eventSource must be non-null");

        EventSource_QueuePublisher<?> eventSourceQueuePublisher = eventSourceToQueueMap.computeIfAbsent(
                new EventSourceKey<>(sourceName),
                eventSourceKey -> new EventSource_QueuePublisher<>(new EventToQueuePublisher<>(sourceName), eventSource));

        eventSource.setEventToQueuePublisher((EventToQueuePublisher<T>) eventSourceQueuePublisher.getQueuePublisher());
    }

    public void registerEventMapperFactory(Supplier<EventToInvokeStrategy> eventMapper, CallBackType type) {
        Objects.requireNonNull(eventMapper, "eventMapper must be non-null");
        Objects.requireNonNull(type, "type must be non-null");

        eventToInvokerFactoryMap.put(type, eventMapper);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> EventQueueToEventProcessor getMappingAgent(EventSourceKey<T> eventSourceKey, CallBackType type, Agent subscriber) {
        Objects.requireNonNull(eventSourceKey, "eventSourceKey must be non-null");
        Objects.requireNonNull(type, "type must be non-null");
        Objects.requireNonNull(subscriber, "subscriber must be non-null");

        Supplier<EventToInvokeStrategy> eventMapperSupplier = eventToInvokerFactoryMap.get(type);
        Objects.requireNonNull(eventMapperSupplier, "no EventMapper registered for type:" + type);


        EventSource_QueuePublisher<T> eventSourceQueuePublisher = (EventSource_QueuePublisher<T>) eventSourceToQueueMap.get(eventSourceKey);
        Objects.requireNonNull(eventSourceQueuePublisher, "no EventSource registered for EventSourceKey:" + eventSourceKey);

        //create or re-use a target queue
        EventSourceKey_Subscriber<T> keySubscriber = new EventSourceKey_Subscriber<>(eventSourceKey, subscriber);
        OneToOneConcurrentArrayQueue eventQueue = subscriberKeyToQueueMap.computeIfAbsent(
                keySubscriber,
                key -> new OneToOneConcurrentArrayQueue<>(500));

        //add as a target to the source
        String name = subscriber.roleName() + "." + eventSourceKey.getSourceName() + "." + type.name();
        eventSourceQueuePublisher.getQueuePublisher().addTargetQueue(eventQueue, name);

        return new EventQueueToEventProcessorAgent(eventQueue, eventMapperSupplier.get(), name);
    }

    public <T> EventQueueToEventProcessor getMappingAgent(EventSubscriptionKey<T> subscriptionKey, Agent subscriber) {
        return getMappingAgent(subscriptionKey.getEventSourceKey(), subscriptionKey.getCallBackType(), subscriber);
    }


    @Value
    private static class EventSource_QueuePublisher<T> {
        EventToQueuePublisher<T> queuePublisher;
        EventSource<T> eventSource;
    }

    @Value
    private static class EventSourceKey_Subscriber<T> {
        EventSourceKey<T> eventSourceKey;
        Object subscriber;
    }
}
