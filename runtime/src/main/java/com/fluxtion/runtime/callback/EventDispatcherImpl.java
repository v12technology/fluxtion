package com.fluxtion.runtime.callback;

public class EventDispatcherImpl implements EventDispatcher, CallbackDispatcherListener {
    private CallbackDispatcher callBackDispatcher;

    @Override
    public void registerCallbackDispatcher(CallbackDispatcher callBackDispatcher) {
        this.callBackDispatcher = callBackDispatcher;
    }

    @Override
    public void processReentrantEvent(Object event) {
        callBackDispatcher.processReentrantEvent(event);
    }

    @Override
    public void processReentrantEvents(Iterable<Object> iterable) {
        callBackDispatcher.processReentrantEvents(iterable);

    }
}
