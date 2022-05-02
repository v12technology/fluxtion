package com.fluxtion.runtime.callback;

public class EventDispatcherImpl implements EventDispatcher, CallbackDispatcherListener {
    private CallbackDispatcher callBackDispatcher;

    @Override
    public void registerCallbackDispatcher(CallbackDispatcher callBackDispatcher) {
        this.callBackDispatcher = callBackDispatcher;
    }

    @Override
    public void processEvent(Object event) {
        callBackDispatcher.processEvent(event);
    }

    @Override
    public void processEvents(Iterable<Object> iterable) {
        callBackDispatcher.processEvents(iterable);

    }
}
