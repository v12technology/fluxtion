package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AbstractEventStream;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class TumblingWindowStream<T, R, S extends EventStream<T>, F extends AggregateFunction<T, R, F>>
        extends AbstractEventStream<T, R, S> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    protected final transient F windowFunction;
    public FixedRateTrigger rollTrigger;
    private R value;


    public TumblingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier, int windowSizeMillis) {
        this(inputEventStream, windowFunctionSupplier);
        rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
    }

    public TumblingWindowStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.windowFunction = windowFunctionSupplier.get();
    }

    @Override
    public R get() {
        return value;
    }

    protected void cacheWindowValue() {
        value = windowFunction.get();
    }

    protected void aggregateInputValue(S inputEventStream) {
        windowFunction.aggregate(inputEventStream.get());
    }

    @OnParentUpdate
    public void timeTriggerFired(FixedRateTrigger rollTrigger) {
        cacheWindowValue();
        inputStreamTriggered_1 = true;
        inputStreamTriggered = true;
        windowFunction.reset();
    }

    @OnParentUpdate
    public void inputUpdated(S inputEventStream) {
        aggregateInputValue(inputEventStream);
        inputStreamTriggered_1 = false;
        inputStreamTriggered = false;
    }

    @OnParentUpdate("updateTriggerNode")
    public void updateTriggerNodeUpdated(Object triggerNode) {
        super.updateTriggerNodeUpdated(triggerNode);
        cacheWindowValue();
    }

    @OnTrigger
    public boolean triggered() {
        return fireEventUpdateNotification();
    }

    @Override
    protected void resetOperation() {
        windowFunction.reset();
        rollTrigger.init();
        value = null;
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    public static class TumblingIntWindowStream<F extends IntAggregateFunction<F>>
            extends TumblingWindowStream<Integer, Integer, IntEventStream, F>
            implements IntEventStream {

        private int value;

        public TumblingIntWindowStream(IntEventStream inputEventStream,
                                       SerializableSupplier<F> windowFunctionSupplier,
                                       int windowSizeMillis) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis);

        }

        public TumblingIntWindowStream(IntEventStream inputEventStream,
                                       SerializableSupplier<F> windowFunctionSupplier) {
            super(inputEventStream, windowFunctionSupplier);
        }

        @Override
        public int getAsInt() {
            return value;
        }

        @Override
        public Integer get() {
            return value;
        }

        protected void cacheWindowValue() {
            value = windowFunction.getAsInt();
        }

        protected void aggregateInputValue(IntEventStream inputEventStream) {
            windowFunction.aggregateInt(inputEventStream.getAsInt());
        }
    }


    public static class TumblingDoubleWindowStream<F extends DoubleAggregateFunction<F>>
            extends TumblingWindowStream<Double, Double, DoubleEventStream, F>
            implements DoubleEventStream {

        private double value;

        public TumblingDoubleWindowStream(DoubleEventStream inputEventStream,
                                          SerializableSupplier<F> windowFunctionSupplier,
                                          int windowSizeMillis) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis);
        }

        public TumblingDoubleWindowStream(DoubleEventStream inputEventStream,
                                          SerializableSupplier<F> windowFunctionSupplier) {
            super(inputEventStream, windowFunctionSupplier);
        }

        @Override
        public double getAsDouble() {
            return value;
        }

        @Override
        public Double get() {
            return value;
        }

        protected void cacheWindowValue() {
            value = windowFunction.getAsDouble();
        }

        protected void aggregateInputValue(DoubleEventStream inputEventStream) {
            windowFunction.aggregateDouble(inputEventStream.getAsDouble());
        }
    }


    public static class TumblingLongWindowStream<F extends LongAggregateFunction<F>>
            extends TumblingWindowStream<Long, Long, LongEventStream, F>
            implements LongEventStream {

        private long value;

        public TumblingLongWindowStream(LongEventStream inputEventStream,
                                        SerializableSupplier<F> windowFunctionSupplier,
                                        int windowSizeMillis) {
            super(inputEventStream, windowFunctionSupplier, windowSizeMillis);
        }

        public TumblingLongWindowStream(LongEventStream inputEventStream,
                                        SerializableSupplier<F> windowFunctionSupplier) {
            super(inputEventStream, windowFunctionSupplier);
        }

        @Override
        public long getAsLong() {
            return value;
        }

        @Override
        public Long get() {
            return value;
        }

        protected void cacheWindowValue() {
            value = windowFunction.getAsLong();
        }

        protected void aggregateInputValue(LongEventStream inputEventStream) {
            windowFunction.aggregateLong(inputEventStream.getAsLong());
        }

    }
}
