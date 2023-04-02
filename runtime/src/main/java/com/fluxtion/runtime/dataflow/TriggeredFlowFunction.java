package com.fluxtion.runtime.dataflow;

public interface TriggeredFlowFunction<T> extends FlowFunction<T> {
    void setUpdateTriggerNode(Object updateTriggerNode);

    void setPublishTriggerNode(Object publishTriggerNode);

    void setResetTriggerNode(Object resetTriggerNode);

    void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode);
}
