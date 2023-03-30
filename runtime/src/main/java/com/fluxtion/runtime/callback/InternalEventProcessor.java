package com.fluxtion.runtime.callback;

public interface InternalEventProcessor {

    void onEvent(Object event);

    void onEventInternal(Object event);

    default void triggerCalculation() {
    }

    ;

    void bufferEvent(Object event);

    boolean isDirty(Object node);

    void setDirty(Object node, boolean dirtyFlag);

    <T> T getNodeById(String id) throws NoSuchFieldException;
}
