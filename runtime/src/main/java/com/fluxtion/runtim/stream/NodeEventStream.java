package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.Named;
import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.NoEventReference;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.EventLogNode;
import com.fluxtion.runtim.audit.NodeNameLookup;

public class NodeEventStream<T> extends EventLogNode  implements TriggeredEventStream<T> {

    private final T source;
    private String instanceName;
    private String name;
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
        name = "stream_" + instanceName;
    }

    @Override
    public T get() {
        return source;
    }

    @Override
    public void setUpdateTriggerOverride(Object updateTriggerOverride) {
        //do nothing
    }
}
