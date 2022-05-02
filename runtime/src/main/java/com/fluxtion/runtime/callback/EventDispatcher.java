package com.fluxtion.runtime.callback;

public interface EventDispatcher {
    
    void processEvent(Object event);

    void processEvents(Iterable<Object> iterator);
}
