package com.fluxtion.runtim.stream;

public interface TriggeredEventStream<T> extends EventStream <T> {
    void setUpdateTriggerNode(Object updateTriggerNode);

    void setPublishTriggerNode(Object publishTriggerNode);

    void setResetTriggerNode(Object resetTriggerNode);
}
