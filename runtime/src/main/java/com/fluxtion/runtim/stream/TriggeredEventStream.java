package com.fluxtion.runtim.stream;

public interface TriggeredEventStream<T> extends EventStream <T> {
    void setUpdateTriggerOverride(Object updateTriggerOverride);
}
