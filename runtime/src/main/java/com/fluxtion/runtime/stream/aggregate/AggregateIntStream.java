package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class AggregateIntStream<F extends BaseIntSlidingWindowFunction<F>>
        extends MapEventStream<Integer, Integer, IntEventStream>
        implements IntEventStream {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F aggregateFunction;

    private int result;

    public AggregateIntStream(IntEventStream inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.aggregateFunction = windowFunctionSupplier.get();
        auditInfo = aggregateFunction.getClass().getSimpleName() + "->aggregateInt";
    }

    protected void initialise() {
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @Override
    protected void resetOperation() {
        result = aggregateFunction.resetInt();
    }

    @Override
    protected void mapOperation() {
        result = aggregateFunction.aggregateInt(getInputEventStream().getAsInt());
    }

    @Override
    public int getAsInt() {
        return result;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

    public static class TumblingIntWindowStream <F extends BaseIntSlidingWindowFunction<F>>
            extends EventLogNode
            implements IntEventStream {

        @NoTriggerReference
        private final IntEventStream inputEventStream;
        private final SerializableSupplier<F> windowFunctionSupplier;
        private transient final F windowFunction;
        public FixedRateTrigger rollTrigger;

        private int value;

        public TumblingIntWindowStream(IntEventStream inputEventStream,
                                       SerializableSupplier<F> windowFunctionSupplier,
                                       int windowSizeMillis) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
            rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
        }

        public TumblingIntWindowStream(IntEventStream inputEventStream,
                                       SerializableSupplier<F> windowFunctionSupplier) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
        }

        @Override
        public int getAsInt() {
            return value;
        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            value = windowFunction.getAsInt();
            windowFunction.reset();
        }

        @OnParentUpdate
        public void updateData(IntEventStream inputEventStream) {
            windowFunction.aggregateInt(inputEventStream.getAsInt());
        }

        @OnTrigger
        public boolean triggered() {
            return true;
        }

        @Override
        public void setUpdateTriggerNode(Object updateTriggerNode) {

        }

        @Override
        public void setPublishTriggerNode(Object publishTriggerNode) {

        }

        @Override
        public void setResetTriggerNode(Object resetTriggerNode) {

        }

        @Override
        public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

        }
    }
}
