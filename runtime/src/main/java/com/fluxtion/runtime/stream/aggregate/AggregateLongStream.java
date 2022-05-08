package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class AggregateLongStream<F extends BaseLongSlidingWindowFunction<F>>
        extends MapEventStream<Long, Long, LongEventStream> implements LongEventStream {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    private long result;

    public AggregateLongStream(LongEventStream inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.mapFunction = windowFunctionSupplier.get();
        auditInfo = mapFunction.getClass().getSimpleName() + "->aggregateInt";
    }

    protected void initialise() {
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @Override
    protected void resetOperation() {
        result = mapFunction.resetLong();
    }

    @Override
    protected void mapOperation() {
        result = mapFunction.aggregateLong(getInputEventStream().getAsLong());
    }

    @Override
    public long getAsLong() {
        return result;
    }

    @Override
    public Long get() {
        return getAsLong();
    }

    public static class TumblingLongWindowStream<F extends BaseLongSlidingWindowFunction<F>>
            extends EventLogNode
            implements LongEventStream {

        @NoTriggerReference
        private final LongEventStream inputEventStream;
        private final SerializableSupplier<F> windowFunctionSupplier;
        private transient final F windowFunction;
        public FixedRateTrigger rollTrigger;

        private long value;

        public TumblingLongWindowStream(LongEventStream inputEventStream,
                                        SerializableSupplier<F> windowFunctionSupplier,
                                        int windowSizeMillis) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
            rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
        }

        public TumblingLongWindowStream(LongEventStream inputEventStream,
                                        SerializableSupplier<F> windowFunctionSupplier) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
        }

        @Override
        public long getAsLong() {
            return value;
        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            value = windowFunction.getAsLong();
            windowFunction.reset();
        }

        @OnParentUpdate
        public void updateData(LongEventStream inputEventStream) {
            windowFunction.aggregateLong(inputEventStream.getAsLong());
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
