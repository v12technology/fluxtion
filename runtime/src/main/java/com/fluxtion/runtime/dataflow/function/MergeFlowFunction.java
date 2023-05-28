package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;

public class MergeFlowFunction<T, S extends FlowFunction<T>, R extends FlowFunction<? extends T>> extends EventLogNode
        implements TriggeredFlowFunction<T> {

    private final S inputEventStream1;
    private final R inputEventStream2;
    private T update;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;

    public MergeFlowFunction(
            @AssignToField("inputEventStream1") S inputEventStream1,
            @AssignToField("inputEventStream2") R inputEventStream2) {
        this.inputEventStream1 = inputEventStream1;
        this.inputEventStream2 = inputEventStream2;
    }

    @OnParentUpdate("inputEventStream1")
    public void inputStream1Updated(S inputEventStream1) {
        update = inputEventStream1.get();
    }

    @OnParentUpdate("inputEventStream2")
    public void inputStream2Updated(R inputEventStream2) {
        update = inputEventStream2.get();
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @OnTrigger
    public boolean publishMerge() {
        return update != null;
    }

    @Override
    public boolean hasChanged() {
        return dirtyStateMonitor.isDirty(this);
    }

    @Override
    public T get() {
        return update;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {

    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {

    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {

    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

    }
}
