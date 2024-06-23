package com.fluxtion.runtime.server.subscription;

import lombok.Value;

@Value
public class EventSourceKey<T> {
    String sourceName;
}
