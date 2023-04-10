package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;

public class NodePropertyToFlowFunction<T> extends EventLogNode implements TriggeredFlowFunction<T> {

    private transient final Object source;
    private final LambdaReflection.SerializableSupplier<T> methodSupplier;
    private transient T value;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;

    public NodePropertyToFlowFunction(LambdaReflection.SerializableSupplier<T> methodSupplier) {
        this.methodSupplier = methodSupplier;
        this.source = methodSupplier.captured()[0];
    }

    @OnTrigger
    public boolean triggered() {
        value = methodSupplier.get();
        return true;
    }

    @Override
    public boolean hasChanged() {
        return dirtyStateMonitor.isDirty(this);
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {
        //do nothing
    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {
        //do nothing
    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
    }
}
