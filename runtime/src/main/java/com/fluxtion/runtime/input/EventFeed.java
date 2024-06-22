package com.fluxtion.runtime.input;

import com.fluxtion.runtime.StaticEventProcessor;

public interface EventFeed<T> {

    void registerSubscriber(StaticEventProcessor subscriber);

    void subscribe(StaticEventProcessor subscriber, T subscriptionId);

    void unSubscribe(StaticEventProcessor subscriber, T subscriptionId);

    void removeAllSubscriptions(StaticEventProcessor subscriber);
}
