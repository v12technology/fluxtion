package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.OnParentUpdate;
import com.fluxtion.runtim.audit.EventLogNode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @param <R> Type of input stream
 * @param <T> Output type of stream
 */
@Data
public abstract class AbstractEventStream<R, T, S extends EventStream<R>> extends EventLogNode
        implements TriggeredEventStream<T> {

    private final S inputEventStream;
    @Setter(AccessLevel.NONE)
    private transient boolean overrideUpdateTrigger;
    @Setter(AccessLevel.NONE)
    private transient boolean overrideTriggered;
    @Setter(AccessLevel.NONE)
    private transient boolean publishTriggered;

    private Object updateTriggerNode;
    private Object publishTriggerNode;
    private Object resetTriggerNode;

    public AbstractEventStream(S inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

    protected final boolean fireEventUpdateNotification(){
        boolean fireNotification = !overrideUpdateTrigger | overrideTriggered | publishTriggered;
        overrideTriggered = false;
        publishTriggered = false;
        auditLog.info("fireNotification", fireNotification);
        return fireNotification;
    }

    protected final boolean executeUpdate(){
        return !overrideUpdateTrigger | overrideTriggered;
    }

    @OnParentUpdate("updateTriggerNode")
    public final void updateTriggerNodeUpdated(Object triggerNode){
        overrideTriggered = true;
    }

    @OnParentUpdate("publishTriggerNode")
    public final void publishTriggerNodeUpdated(Object triggerNode){
        publishTriggered = true;
    }

    @Initialise
    public final void initialiseEventStream(){
        overrideUpdateTrigger = updateTriggerNode!=null;
        initialise();
    }

    protected void initialise(){
        //NO-OP
    }

}
