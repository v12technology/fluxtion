package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateIntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow.BucketedSlidingWindowedDoubleFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow.BucketedSlidingWindowedIntFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.BucketedSlidingWindow.BucketedSlidingWindowedLongFunction;
import com.fluxtion.runtime.dataflow.function.AbstractFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class TimedSlidingWindow
        <T, R, S extends FlowFunction<T>, F extends AggregateFlowFunction<T, R, F>>
        extends AbstractFlowFunction<T, R, S>
        implements TriggeredFlowFunction<R> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    private final int buckets;
    protected transient final BucketedSlidingWindow<T, R, F> windowFunction;
    public FixedRateTrigger rollTrigger;
    private R value;

    public TimedSlidingWindow(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int windowSizeMillis, int buckets) {
        this(inputEventStream, windowFunctionSupplier, buckets);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TimedSlidingWindow(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int buckets) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.buckets = buckets;
        this.windowFunction = new BucketedSlidingWindow<>(windowFunctionSupplier, buckets);
    }

    @Override
    public R get() {
        return value;//windowFunction.get();
    }

    protected void cacheWindowValue() {
        value = windowFunction.get();
    }

    protected void aggregateInputValue(S inputEventStream) {
        windowFunction.aggregate(inputEventStream.get());
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        windowFunction.roll(rollTrigger.getTriggerCount());
        if (windowFunction.isAllBucketsFilled()) {
            cacheWindowValue();
            inputStreamTriggered_1 = true;
            inputStreamTriggered = true;
        }
    }

    @OnParentUpdate
    public void inputUpdated(S inputEventStream) {
        aggregateInputValue(inputEventStream);
        inputStreamTriggered_1 = false;
        inputStreamTriggered = false;
    }

    @OnTrigger
    public boolean triggered() {
        return fireEventUpdateNotification();
    }

    @Override
    protected void resetOperation() {
        windowFunction.init();
        rollTrigger.init();
        value = null;
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }


    public static class TimedSlidingWindowIntStream
            <F extends AggregateIntFlowFunction<F>>
            extends TimedSlidingWindow<Integer, Integer, IntFlowFunction, F>
            implements IntFlowFunction {

        private int value;
        private transient final BucketedSlidingWindowedIntFunction<F> intSlidingFunction;

        public TimedSlidingWindowIntStream(
                IntFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedIntFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowIntStream(
                IntFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            intSlidingFunction = new BucketedSlidingWindowedIntFunction<>(windowFunctionSupplier, buckets);
        }

//        @OnParentUpdate
//        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
//            intSlidingFunction.roll(rollTrigger.getTriggerCount());
//        }

//        @OnParentUpdate
//        public void updateData(IntEventStream inputEventStream) {
//            intSlidingFunction.aggregateInt(inputEventStream.getAsInt());
//        }
//
//        @OnTrigger
//        public boolean triggered() {
//            boolean publish = intSlidingFunction.isAllBucketsFilled();
//            if (publish) value = intSlidingFunction.getAsInt();
//            return publish;
//        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            intSlidingFunction.roll(rollTrigger.getTriggerCount());
            if (intSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public int getAsInt() {
            return value;
        }

        protected void cacheWindowValue() {
            value = intSlidingFunction.getAsInt();
        }

        protected void aggregateInputValue(IntFlowFunction inputEventStream) {
            intSlidingFunction.aggregateInt(inputEventStream.getAsInt());
        }

        @Override
        protected void resetOperation() {
            intSlidingFunction.init();
            rollTrigger.init();
            value = 0;
        }
    }

    public static class TimedSlidingWindowDoubleStream
            <F extends AggregateDoubleFlowFunction<F>>
            extends TimedSlidingWindow<Double, Double, DoubleFlowFunction, F>
            implements DoubleFlowFunction {

        private double value;
        private transient final BucketedSlidingWindowedDoubleFunction<F> intSlidingFunction;

        public TimedSlidingWindowDoubleStream(
                DoubleFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedDoubleFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowDoubleStream(
                DoubleFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            intSlidingFunction = new BucketedSlidingWindowedDoubleFunction<>(windowFunctionSupplier, buckets);
        }

//        @OnParentUpdate
//        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
//            intSlidingFunction.roll(rollTrigger.getTriggerCount());
//        }
//
//        @OnParentUpdate
//        public void updateData(DoubleEventStream inputEventStream) {
//            intSlidingFunction.aggregateDouble(inputEventStream.getAsDouble());
//        }
//
//        @OnTrigger
//        public boolean triggered() {
//            boolean publish = intSlidingFunction.isAllBucketsFilled();
//            if (publish) value = intSlidingFunction.getAsDouble();
//            return publish;
//        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            intSlidingFunction.roll(rollTrigger.getTriggerCount());
            if (intSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        public Double get() {
            return value;
        }

        @Override
        public double getAsDouble() {
            return value;
        }

        protected void cacheWindowValue() {
            value = intSlidingFunction.getAsDouble();
        }

        protected void aggregateInputValue(DoubleFlowFunction inputEventStream) {
            intSlidingFunction.aggregateDouble(inputEventStream.getAsDouble());
        }

        @Override
        protected void resetOperation() {
            intSlidingFunction.init();
            rollTrigger.init();
            value = 0;
        }
    }

    public static class TimedSlidingWindowLongStream
            <F extends AggregateLongFlowFunction<F>>
            extends TimedSlidingWindow<Long, Long, LongFlowFunction, F>
            implements LongFlowFunction {

        private long value;
        private transient final BucketedSlidingWindowedLongFunction<F> intSlidingFunction;

        public TimedSlidingWindowLongStream(
                LongFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedLongFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowLongStream(
                LongFlowFunction inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, buckets);
            intSlidingFunction = new BucketedSlidingWindowedLongFunction<>(windowFunctionSupplier, buckets);
        }

//        @OnParentUpdate
//        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
//            intSlidingFunction.roll(rollTrigger.getTriggerCount());
//        }
//
//        @OnParentUpdate
//        public void updateData(LongEventStream inputEventStream) {
//            intSlidingFunction.aggregateLong(inputEventStream.getAsLong());
//        }
//
//        @OnTrigger
//        public boolean triggered() {
//            boolean publish = intSlidingFunction.isAllBucketsFilled();
//            if (publish) value = intSlidingFunction.getAsLong();
//            return publish;
//        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            intSlidingFunction.roll(rollTrigger.getTriggerCount());
            if (intSlidingFunction.isAllBucketsFilled()) {
                cacheWindowValue();
                inputStreamTriggered_1 = true;
                inputStreamTriggered = true;
            }
        }

        @Override
        public Long get() {
            return value;
        }

        @Override
        public long getAsLong() {
            return value;
        }

        protected void cacheWindowValue() {
            value = intSlidingFunction.getAsLong();
        }

        protected void aggregateInputValue(LongFlowFunction inputEventStream) {
            intSlidingFunction.aggregateLong(inputEventStream.getAsLong());
        }

        @Override
        protected void resetOperation() {
            intSlidingFunction.init();
            rollTrigger.init();
            value = 0;
        }
    }
}
