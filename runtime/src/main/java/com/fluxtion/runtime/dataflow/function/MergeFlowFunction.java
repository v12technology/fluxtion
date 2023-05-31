package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;

import java.util.ArrayList;
import java.util.List;

public class MergeFlowFunction<T, S extends FlowFunction<T>, R extends FlowFunction<? extends T>> extends EventLogNode
        implements TriggeredFlowFunction<T> {

    private final List<FlowFunction<? extends T>> mergeList;

    private T update;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;

    public MergeFlowFunction(
            S inputEventStream1,
            R inputEventStream2) {
        mergeList = new ArrayList<>();
        mergeList.add(inputEventStream1);
        mergeList.add(inputEventStream2);
    }

    public MergeFlowFunction(List<FlowFunction<? extends T>> mergeList) {
        this.mergeList = mergeList;
    }

    @OnParentUpdate("mergeList")
    public void inputStreamUpdated(FlowFunction<? extends T> inputEventStream1) {
        update = (T) inputEventStream1.get();
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
