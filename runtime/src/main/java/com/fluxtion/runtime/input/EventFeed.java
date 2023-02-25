package com.fluxtion.runtime.input;

import com.fluxtion.runtime.StaticEventProcessor;

public interface EventFeed {

    void registerFeedTarget(StaticEventProcessor staticEventProcessor);

    void subscribe(StaticEventProcessor target, Object subscriptionId);

    void unSubscribe(StaticEventProcessor target, Object subscriptionId);

    void removeAllSubscriptions(StaticEventProcessor eventProcessor);
}