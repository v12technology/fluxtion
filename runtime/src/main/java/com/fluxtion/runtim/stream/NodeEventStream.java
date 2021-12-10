package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.NoEventReference;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.EventLogNode;
import com.fluxtion.runtim.audit.NodeNameLookup;

public class NodeEventStream<T> extends EventLogNode  implements TriggeredEventStream<T> {

    private final T source;
    private String instanceName;
    @Inject
    @NoEventReference
    public NodeNameLookup nodeNameLookup;

    public NodeEventStream(T source) {
        this.source = source;
    }

    @OnEvent
    public void sourceUpdated(){
        auditLog.info("sourceInstance", instanceName);
    }

    @Initialise
    public void init(){
        instanceName = nodeNameLookup.lookup(source);
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
}
