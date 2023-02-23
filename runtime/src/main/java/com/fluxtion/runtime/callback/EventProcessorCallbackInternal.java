package com.fluxtion.runtime.callback;

public interface EventProcessorCallbackInternal extends CallbackDispatcher, DirtyStateMonitor {

    void dispatchQueuedCallbacks();

    void setEventProcessor(InternalEventProcessor eventProcessor);
}
