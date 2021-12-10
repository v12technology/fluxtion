package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.NoEventReference;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.NodeNameLookup;

public class NotifyEventStream<T> extends AbstractEventStream<T, T> {

    @PushReference
    private final Object target;
    private final String auditInfo;
    private String instanceName;
    @Inject
    @NoEventReference
    public NodeNameLookup nodeNameLookup;

    public NotifyEventStream(EventStream<T> inputEventStream, Object target) {
        super(inputEventStream);
        this.target = target;
        auditInfo = target.getClass().getSimpleName() ;
    }

    protected void initialise(){
       instanceName = nodeNameLookup.lookup(target);
    }

    @OnEvent
    public boolean notifyChild(){
        auditLog.info("notifyClass", auditInfo);
        auditLog.info("notifyInstance", instanceName);
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

}
