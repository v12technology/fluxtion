package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.EventDispatcher;

public class InternalEventDispatcher {

    @Inject
    private final EventDispatcher eventDispatcher;

    public InternalEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public InternalEventDispatcher() {
        this.eventDispatcher = null;
    }

    public void dispatchToGraph(Object event) {
        eventDispatcher.processReentrantEvent(event);
    }
}
