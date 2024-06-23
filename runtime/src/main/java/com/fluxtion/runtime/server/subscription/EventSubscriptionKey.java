package com.fluxtion.runtime.server.subscription;

import lombok.Value;

@Value
public class EventSubscriptionKey<T> {
    EventSourceKey<T> eventSourceKey;
    CallBackType callBackType;
    Object subscriptionQualifier;
}
