package com.fluxtion.runtime.server.subscription;

/**
 * Publishes events to {@link EventToQueuePublisher}. Register an {@link EventSource} instance with {@link EventFlowManager}
 * to receive the target queue via the setEventToQueuePublisher callback method.
 *
 * @param <T>
 */
public interface EventSource<T> {

    void subscribe(EventSubscriptionKey<T> eventSourceKey);

    void unSubscribe(EventSubscriptionKey<T> eventSourceKey);

    void setEventToQueuePublisher(EventToQueuePublisher<T> targetQueue);
}
