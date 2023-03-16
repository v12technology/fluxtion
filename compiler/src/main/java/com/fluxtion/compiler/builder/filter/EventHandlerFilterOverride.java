package com.fluxtion.compiler.builder.filter;

import lombok.Value;

@Value
public class EventHandlerFilterOverride {
    Object eventHandlerInstance;
    Class<?> eventType;
    int newFilterId;
}
