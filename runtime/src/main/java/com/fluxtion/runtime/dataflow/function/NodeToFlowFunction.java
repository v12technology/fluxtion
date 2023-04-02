package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.node.NodeNameLookup;

public class NodeToFlowFunction<T> extends EventLogNode implements TriggeredFlowFunction<T> {

    private final T source;
    private String instanceName;
    @Inject
    @NoTriggerReference
    public NodeNameLookup nodeNameLookup;

    public NodeToFlowFunction(T source) {
        this.source = source;
    }

    @OnTrigger
    public void sourceUpdated() {
        auditLog.info("sourceInstance", instanceName);
    }

    @Initialise
    public void init() {
        instanceName = nodeNameLookup.lookupInstanceName(source);
    }

    @Override
    public T get() {
        return source;
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
