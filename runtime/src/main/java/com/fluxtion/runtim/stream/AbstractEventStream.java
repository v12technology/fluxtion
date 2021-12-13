package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.Anchor;
import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.OnParentUpdate;
import com.fluxtion.runtim.audit.EventLogNode;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.MethodReferenceReflection;
import lombok.ToString;

import java.lang.reflect.Method;

/**
 * @param <R> Type of input stream
 * @param <T> Output type of this stream
 * @param <S> The type of {@link EventStream} that wraps R
 */
@ToString
public abstract class AbstractEventStream<R, T, S extends EventStream<R>> extends EventLogNode
        implements TriggeredEventStream<T> {

    private final S inputEventStream;
    private final transient MethodReferenceReflection streamFunction;
    private transient boolean overrideUpdateTrigger;
    private transient boolean overrideTriggered;
    private transient boolean publishTriggered;

    private Object updateTriggerNode;
    private Object publishTriggerNode;
    private Object resetTriggerNode;

    public AbstractEventStream(S inputEventStream, MethodReferenceReflection methodReferenceReflection) {
        this.inputEventStream = inputEventStream;
        streamFunction = methodReferenceReflection;
        if (methodReferenceReflection != null && methodReferenceReflection.captured().length > 0 && !methodReferenceReflection.isDefaultConstructor()) {
            Anchor.anchor(this, methodReferenceReflection.captured()[0]);
        }
    }

    /**
     * Checks whether an event notification should be fired to downstream nodes. This can be due to any upstream trigger
     * firing including:
     * <ul>
     *     <li>upstream nodes firing a notification</li>
     *     <li>publish trigger firing</li>
     *     <li>update override firing</li>
     * </ul>
     * @return flag indicating fire a notification to child nodes for any upstream change
     */
    protected final boolean fireEventUpdateNotification() {
        boolean fireNotification = !overrideUpdateTrigger | overrideTriggered | publishTriggered;
        overrideTriggered = false;
        publishTriggered = false;
        auditLog.info("fireNotification", fireNotification);
        return fireNotification;
    }

    /**
     * Checks whether an event notification should be fired to downstream nodes due to:
     * <ul>
     *     <li>upstream nodes firing a notification</li>
     *     <li>update override firing</li>
     * </ul>
     * Requests from publish trigger are not included in this flag
     * @return flag indicating fire a notification to child nodes for an upstream update change, not publish trigger
     *
     *
     */
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

    public MethodReferenceReflection getStreamFunction() {
        return streamFunction;
    }

    /**
     * @param <R> Type of input stream for first argument
     * @param <Q> Type of input stream for second argument
     * @param <T> Output type of this stream
     * @param <S> The type of {@link EventStream} that wraps R
     * @param <U> The type of {@link EventStream} that wraps Q
     */
    public static abstract class AbstractBinaryEventStream<R, Q, T, S extends EventStream<R>, U extends EventStream<Q>>
        extends AbstractEventStream<R, T, S>{

        private final U inputEventStream_2;

        public AbstractBinaryEventStream(S inputEventStream_1, U inputEventStream_2, MethodReferenceReflection methodReferenceReflection) {
            super(inputEventStream_1, methodReferenceReflection);
            this.inputEventStream_2 = inputEventStream_2;
        }

        public S getInputEventStream_1() {
            return getInputEventStream();
        }

        public U getInputEventStream_2() {
            return inputEventStream_2;
        }
    }
}
