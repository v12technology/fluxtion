package com.fluxtion.runtime.input;

import com.fluxtion.runtime.StaticEventProcessor;

public interface EventFeed {

    void registerSubscriber(StaticEventProcessor subscriber);

    void subscribe(StaticEventProcessor subscriber, Object subscriptionId);

    void unSubscribe(StaticEventProcessor subscriber, Object subscriptionId);

    void removeAllSubscriptions(StaticEventProcessor subscriber);
}
