package com.fluxtion.runtime.stream;

public interface TriggeredEventStream<T> extends EventStream <T> {
    void setUpdateTriggerNode(Object updateTriggerNode);

    void setPublishTriggerNode(Object publishTriggerNode);

    void setResetTriggerNode(Object resetTriggerNode);

    void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode);
}
