package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.lifecycle.Lifecycle;

public interface LifeCycleEventSource<T> extends EventSource<T>, Lifecycle {

    @Override
    default void subscribe(EventSubscriptionKey<T> eventSourceKey) {

    }

    @Override
    default void unSubscribe(EventSubscriptionKey<T> eventSourceKey) {

    }

    @Override
    default void setEventToQueuePublisher(EventToQueuePublisher<T> targetQueue) {

    }
}
