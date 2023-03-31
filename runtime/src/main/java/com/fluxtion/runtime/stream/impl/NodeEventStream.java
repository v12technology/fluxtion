package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.node.NodeNameLookup;
import com.fluxtion.runtime.stream.TriggeredEventStream;

public class NodeEventStream<T> extends EventLogNode implements TriggeredEventStream<T> {

    private final T source;
    private String instanceName;
    @Inject
    @NoTriggerReference
    public NodeNameLookup nodeNameLookup;

    public NodeEventStream(T source) {
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
