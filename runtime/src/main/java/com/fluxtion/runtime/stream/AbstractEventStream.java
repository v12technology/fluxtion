package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import lombok.ToString;

/**
 * @param <T> Type of input stream
 * @param <R> Output type of this stream
 * @param <S> The type of incoming {@link EventStream} that wraps
 */
@ToString
public abstract class AbstractEventStream<T, R, S extends EventStream<T>> extends EventLogNode
        implements TriggeredEventStream<R> {

    private final S inputEventStream;
    @NoTriggerReference
    private final transient MethodReferenceReflection streamFunction;
    private transient final boolean statefulFunction;
    protected transient boolean overrideUpdateTrigger;
    protected transient boolean overridePublishTrigger;
    protected transient boolean inputStreamTriggered;
    protected transient boolean inputStreamTriggered_1;
    protected transient boolean overrideTriggered;
    protected transient boolean publishTriggered;
    protected transient boolean publishOverrideTriggered;
    protected transient boolean resetTriggered;
    @NoTriggerReference
    protected transient Stateful<R> resetFunction;
    private Object updateTriggerNode;
    private Object publishTriggerNode;
    private Object publishTriggerOverrideNode;
    private Object resetTriggerNode;

    public AbstractEventStream(S inputEventStream, MethodReferenceReflection methodReferenceReflection) {
        this.inputEventStream = inputEventStream;
        streamFunction = methodReferenceReflection;
        if (methodReferenceReflection != null && methodReferenceReflection.captured().length > 0 && !methodReferenceReflection.isDefaultConstructor()) {
            Object streamFunctionInstance = EventProcessorBuilderService.service().addOrReuse(methodReferenceReflection.captured()[0]);
            statefulFunction = Stateful.class.isAssignableFrom(streamFunctionInstance.getClass());
            if (statefulFunction) {
                resetFunction = (Stateful<R>) methodReferenceReflection.captured()[0];
            }
        } else {
//            streamFunctionInstance = null;
            statefulFunction = false;
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
     *
     * @return flag indicating fire a notification to child nodes for any upstream change
     */
    protected boolean fireEventUpdateNotification() {
        boolean fireNotification = (!overridePublishTrigger && !overrideUpdateTrigger && inputStreamTriggered)
                | (!overridePublishTrigger && overrideTriggered)
                | publishOverrideTriggered
                | publishTriggered
                | resetTriggered;
        overrideTriggered = false;
        publishTriggered = false;
        publishOverrideTriggered = false;
        resetTriggered = false;
        inputStreamTriggered = false;
        auditLog.info("fireNotification", fireNotification);
        return fireNotification && get() != null;
    }

    /**
     * Checks whether an event notification should be fired to downstream nodes due to:
     * <ul>
     *     <li>upstream nodes firing a notification</li>
     *     <li>update override firing</li>
     * </ul>
     * Requests from publish trigger are not included in this flag
     *
     * @return flag indicating fire a notification to child nodes for an upstream update change, not publish trigger
     */
    protected boolean executeUpdate() {
        return (!overrideUpdateTrigger && inputStreamTriggered) | overrideTriggered;
    }

    protected final boolean reset() {
        return resetTriggered && statefulFunction;
    }

    @OnParentUpdate("inputEventStream")
    public void inputUpdated(S inputEventStream) {
        inputStreamTriggered_1 = !resetTriggered;
        inputStreamTriggered = !resetTriggered;
    }

    @OnParentUpdate("updateTriggerNode")
    public void updateTriggerNodeUpdated(Object triggerNode) {
        overrideTriggered = true;
    }

    @OnParentUpdate("publishTriggerNode")
    public final void publishTriggerNodeUpdated(Object triggerNode) {
        publishTriggered = true;
    }

    @OnParentUpdate("publishTriggerOverrideNode")
    public final void publishTriggerOverrideNodeUpdated(Object triggerNode) {
        publishOverrideTriggered = true;
    }

    @OnParentUpdate("resetTriggerNode")
    public final void resetTriggerNodeUpdated(Object triggerNode) {
        resetTriggered = true;
        inputStreamTriggered = false;
        inputStreamTriggered_1 = false;
        if (isStatefulFunction()) resetOperation();
    }

    @Initialise
    public final void initialiseEventStream() {
        overrideUpdateTrigger = updateTriggerNode != null;
        overridePublishTrigger = publishTriggerOverrideNode != null;
        initialise();
    }

    protected void initialise() {
        //NO-OP
    }

    protected void resetOperation() {
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

    public Object getPublishTriggerOverrideNode() {
        return publishTriggerOverrideNode;
    }

    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
        this.publishTriggerOverrideNode = publishTriggerOverrideNode;
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

    public boolean isOverrideTriggered() {
        return overrideTriggered;
    }

    public boolean isPublishTriggered() {
        return publishTriggered;
    }

    public MethodReferenceReflection getStreamFunction() {
        return streamFunction;
    }

    public boolean isStatefulFunction() {
        return statefulFunction;
    }

    /**
     * @param <R> Type of input stream for first argument
     * @param <Q> Type of input stream for second argument
     * @param <T> Output type of this stream
     * @param <S> The type of {@link EventStream} that wraps R
     * @param <U> The type of {@link EventStream} that wraps Q
     */
    public static abstract class AbstractBinaryEventStream<R, Q, T, S extends EventStream<R>, U extends EventStream<Q>>
            extends AbstractEventStream<R, T, S> {

        private final U inputEventStream_2;
        protected boolean inputStreamTriggered_2;

        public AbstractBinaryEventStream(S inputEventStream, U inputEventStream_2, MethodReferenceReflection methodReferenceReflection) {
            super(inputEventStream, methodReferenceReflection);
            this.inputEventStream_2 = inputEventStream_2;
        }

        protected boolean executeUpdate() {
            return (!overrideUpdateTrigger && inputStreamTriggered_1 && inputStreamTriggered_2) | overrideTriggered;
        }

        protected boolean fireEventUpdateNotification() {
            boolean fireNotification = (!overridePublishTrigger && !overrideUpdateTrigger
                    && inputStreamTriggered_1 && inputStreamTriggered_2)
                    | (!overridePublishTrigger && overrideTriggered)
                    | publishOverrideTriggered
                    | publishTriggered
                    | resetTriggered;
            overrideTriggered = false;
            publishTriggered = false;
            publishOverrideTriggered = false;
            resetTriggered = false;
            inputStreamTriggered = false;
            auditLog.info("fireNotification", fireNotification);
            return fireNotification && get() != null;
        }

        public S getInputEventStream_1() {
            return getInputEventStream();
        }

        public U getInputEventStream_2() {
            return inputEventStream_2;
        }

        @OnParentUpdate("inputEventStream_2")
        public final void input2Updated(Object inputEventStream) {
            inputStreamTriggered_2 = !resetTriggered;
            inputStreamTriggered = !resetTriggered;
        }

        @Override
        protected void initialise() {
            inputStreamTriggered_1 = getInputEventStream_1().hasDefaultValue();
            inputStreamTriggered_2 = getInputEventStream_2().hasDefaultValue();
        }
    }
}
