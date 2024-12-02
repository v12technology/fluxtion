package com.fluxtion.runtime.input;

import com.fluxtion.runtime.node.EventSubscription;

public interface SubscriptionManager {
    String DEFAULT_NODE_NAME = "subscriptionManager";

    void subscribe(Object subscriptionId);

    void unSubscribe(Object subscriptionId);

    void subscribeToNamedFeed(EventSubscription<?> subscription);

    void subscribeToNamedFeed(String feedName);

    void unSubscribeToNamedFeed(EventSubscription<?> subscription);

    void unSubscribeToNamedFeed(String feedName);
}
