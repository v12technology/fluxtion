package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.OnParentUpdate;
import com.fluxtion.runtim.audit.EventLogNode;
import lombok.ToString;

/**
 * @param <R> Type of input stream
 * @param <T> Output type of stream
 */
@ToString
public abstract class AbstractEventStream<R, T, S extends EventStream<R>> extends EventLogNode
        implements TriggeredEventStream<T> {

    private final S inputEventStream;
    private transient boolean overrideUpdateTrigger;
    private transient boolean overrideTriggered;
    private transient boolean publishTriggered;

    private Object updateTriggerNode;
    private Object publishTriggerNode;
    private Object resetTriggerNode;

    public AbstractEventStream(S inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

    protected final boolean fireEventUpdateNotification() {
        boolean fireNotification = !overrideUpdateTrigger | overrideTriggered | publishTriggered;
        overrideTriggered = false;
        publishTriggered = false;
        auditLog.info("fireNotification", fireNotification);
        return fireNotification;
    }

    protected final boolean executeUpdate() {
        return !overrideUpdateTrigger | overrideTriggered;
    }

    @OnParentUpdate("updateTriggerNode")
    public final void updateTriggerNodeUpdated(Object triggerNode) {
        overrideTriggered = true;
    }

    @OnParentUpdate("publishTriggerNode")
    public final void publishTriggerNodeUpdated(Object triggerNode) {
        publishTriggered = true;
    }

    @Initialise
    public final void initialiseEventStream() {
        overrideUpdateTrigger = updateTriggerNode != null;
        initialise();
    }

    protected void initialise() {
        //NO-OP
    }

    public Object getUpdateTriggerNode() {
        return updateTriggerNode;
    }

    public void setUpdateTriggerNode(Object updateTriggerNode) {
        this.updateTriggerNode = updateTriggerNode;
    }

    public Object getPublishTriggerNode() {
        return publishTriggerNode;
    }

    public void setPublishTriggerNode(Object publishTriggerNode) {
        this.publishTriggerNode = publishTriggerNode;
    }

    public Object getResetTriggerNode() {
        return resetTriggerNode;
    }

    public void setResetTriggerNode(Object resetTriggerNode) {
        this.resetTriggerNode = resetTriggerNode;
    }

    public S getInputEventStream() {
        return inputEventStream;
    }

    public boolean isOverrideUpdateTrigger() {
        return overrideUpdateTrigger;
    }

    public boolean isOverrideTriggered() {
        return overrideTriggered;
    }

    public boolean isPublishTriggered() {
        return publishTriggered;
    }
}
