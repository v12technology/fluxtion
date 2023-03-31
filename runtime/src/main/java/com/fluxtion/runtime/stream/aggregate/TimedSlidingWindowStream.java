package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.DoubleAggregateFunction;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.IntAggregateFunction;
import com.fluxtion.runtime.stream.LongAggregateFunction;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction.BucketedSlidingWindowedDoubleFunction;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction.BucketedSlidingWindowedIntFunction;
import com.fluxtion.runtime.stream.aggregate.BucketedSlidingWindowedFunction.BucketedSlidingWindowedLongFunction;
import com.fluxtion.runtime.stream.impl.AbstractEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class TimedSlidingWindowStream
        <T, R, S extends EventStream<T>, F extends AggregateFunction<T, R, F>>
        extends AbstractEventStream<T, R, S>
        implements TriggeredEventStream<R> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    private final int buckets;
    protected transient final BucketedSlidingWindowedFunction<T, R, F> windowFunction;
    public FixedRateTrigger rollTrigger;
    private R value;

    public TimedSlidingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int windowSizeMillis, int buckets) {
        this(inputEventStream, windowFunctionSupplier, buckets);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TimedSlidingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int buckets) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.buckets = buckets;
        this.windowFunction = new BucketedSlidingWindowedFunction<>(windowFunctionSupplier, buckets);
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
            <F extends IntAggregateFunction<F>>
            extends TimedSlidingWindowStream<Integer, Integer, IntEventStream, F>
            implements IntEventStream {

        private int value;
        private transient final BucketedSlidingWindowedIntFunction<F> intSlidingFunction;

        public TimedSlidingWindowIntStream(
                IntEventStream inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedIntFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowIntStream(
                IntEventStream inputEventStream,
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

        protected void aggregateInputValue(IntEventStream inputEventStream) {
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
            <F extends DoubleAggregateFunction<F>>
            extends TimedSlidingWindowStream<Double, Double, DoubleEventStream, F>
            implements DoubleEventStream {

        private double value;
        private transient final BucketedSlidingWindowedDoubleFunction<F> intSlidingFunction;

        public TimedSlidingWindowDoubleStream(
                DoubleEventStream inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedDoubleFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowDoubleStream(
                DoubleEventStream inputEventStream,
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

        protected void aggregateInputValue(DoubleEventStream inputEventStream) {
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
            <F extends LongAggregateFunction<F>>
            extends TimedSlidingWindowStream<Long, Long, LongEventStream, F>
            implements LongEventStream {

        private long value;
        private transient final BucketedSlidingWindowedLongFunction<F> intSlidingFunction;

        public TimedSlidingWindowLongStream(
                LongEventStream inputEventStream,
                SerializableSupplier<F> windowFunctionSupplier,
                int windowSizeMillis,
                int buckets) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis, buckets);
            intSlidingFunction = new BucketedSlidingWindowedLongFunction<>(windowFunctionSupplier, buckets);
        }

        public TimedSlidingWindowLongStream(
                LongEventStream inputEventStream,
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

        protected void aggregateInputValue(LongEventStream inputEventStream) {
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
