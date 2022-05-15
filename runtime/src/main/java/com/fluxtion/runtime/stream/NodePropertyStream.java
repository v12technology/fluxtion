package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection;

public class NodePropertyStream <T> extends EventLogNode implements TriggeredEventStream<T> {

    private transient final Object source;
    private final LambdaReflection.SerializableSupplier<T> methodSupplier;
    private transient T value;

    public NodePropertyStream(LambdaReflection.SerializableSupplier<T> methodSupplier) {
        this.methodSupplier = methodSupplier;
        this.source = methodSupplier.captured()[0];
    }

    @OnTrigger
    public boolean triggered(){
        value = methodSupplier.get();
        return true;
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
