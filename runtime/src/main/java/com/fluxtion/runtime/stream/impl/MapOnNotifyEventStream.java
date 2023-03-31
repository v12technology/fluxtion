package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.node.NodeNameLookup;
import com.fluxtion.runtime.stream.EventStream;
import lombok.ToString;

@ToString
public class MapOnNotifyEventStream<R, T, S extends EventStream<R>> extends AbstractEventStream<R, T, S> {

    @PushReference
    private final T target;
    private final transient String auditInfo;
    private String instanceNameToNotify;
    @Inject
    @NoTriggerReference
    public NodeNameLookup nodeNameLookup;

    public MapOnNotifyEventStream(S inputEventStream, T target) {
        super(inputEventStream, null);
        this.target = target;
        auditInfo = target.getClass().getSimpleName();
    }

    protected void initialise() {
        instanceNameToNotify = nodeNameLookup.lookupInstanceName(target);
    }

    @OnTrigger
    public boolean notifyChild() {
        auditLog.info("notifyClass", auditInfo);
        auditLog.info("notifyInstance", instanceNameToNotify);
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return target;
    }
}
