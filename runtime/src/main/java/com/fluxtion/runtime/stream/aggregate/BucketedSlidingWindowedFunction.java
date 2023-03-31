package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.DoubleAggregateFunction;
import com.fluxtion.runtime.stream.IntAggregateFunction;
import com.fluxtion.runtime.stream.LongAggregateFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @param <T> Input type
 * @param <R> return type
 * @param <F> BaseSlidingWindowFunction
 */
public class BucketedSlidingWindowedFunction<T, R, F extends AggregateFunction<T, R, F>> {

    private final Supplier<F> windowFunctionSupplier;
    protected final F aggregatedFunction;
    protected final F currentFunction;
    private final List<F> buckets;
    private int writePointer;
    private boolean allBucketsFilled = false;
    private final boolean deductSupported;

    public BucketedSlidingWindowedFunction(Supplier<F> windowFunctionSupplier, int numberOfBuckets) {
        this.windowFunctionSupplier = windowFunctionSupplier;
        aggregatedFunction = windowFunctionSupplier.get();
        currentFunction = windowFunctionSupplier.get();
        deductSupported = currentFunction.deductSupported();
        buckets = new ArrayList<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets.add(windowFunctionSupplier.get());
        }
    }

    public void init() {
        aggregatedFunction.reset();
        currentFunction.reset();
        buckets.forEach(AggregateFunction::reset);
    }

    public final void aggregate(T input) {
        currentFunction.aggregate(input);
    }

    public void roll() {
        roll(1);
    }

    public void roll(int windowsToRoll) {
        if (deductSupported) {
            for (int i = 0; i < windowsToRoll; i++) {
                F oldFunction = buckets.get(writePointer);
                aggregatedFunction.combine(currentFunction);
                aggregatedFunction.deduct(oldFunction);
                oldFunction.reset();
                oldFunction.combine(currentFunction);
                currentFunction.reset();
                writePointer++;
                allBucketsFilled = allBucketsFilled | writePointer == buckets.size();
                writePointer = writePointer % buckets.size();
            }
        } else {
            aggregatedFunction.reset();
            //clear and then combine
            for (int i = 0; i < windowsToRoll; i++) {
                F oldFunction = buckets.get(writePointer);
                oldFunction.reset();
                oldFunction.combine(currentFunction);
                currentFunction.reset();
                writePointer++;
                allBucketsFilled = allBucketsFilled | writePointer == buckets.size();
                writePointer = writePointer % buckets.size();
            }
            for (int i = 0; i < buckets.size(); i++) {
                aggregatedFunction.combine(buckets.get(i));
            }
        }
    }

    public boolean isAllBucketsFilled() {
        return allBucketsFilled;
    }

    public R get() {
        return aggregatedFunction.get();
    }

    public static class BucketedSlidingWindowedIntFunction<F extends IntAggregateFunction<F>>
            extends BucketedSlidingWindowedFunction<Integer, Integer, F> {

        public BucketedSlidingWindowedIntFunction(SerializableSupplier<F> windowFunctionSupplier, int numberOfBuckets) {
            super(windowFunctionSupplier, numberOfBuckets);
        }

        public void aggregateInt(int input) {
            currentFunction.aggregateInt(input);
        }

        public int getAsInt() {
            return aggregatedFunction.getAsInt();
        }
    }

    public static class BucketedSlidingWindowedDoubleFunction<F extends DoubleAggregateFunction<F>>
            extends BucketedSlidingWindowedFunction<Double, Double, F> {

        public BucketedSlidingWindowedDoubleFunction(SerializableSupplier<F> windowFunctionSupplier, int numberOfBuckets) {
            super(windowFunctionSupplier, numberOfBuckets);
        }

        public void aggregateDouble(double input) {
            currentFunction.aggregateDouble(input);
        }

        public double getAsDouble() {
            return aggregatedFunction.getAsDouble();
        }
    }

    public static class BucketedSlidingWindowedLongFunction<F extends LongAggregateFunction<F>>
            extends BucketedSlidingWindowedFunction<Long, Long, F> {

        public BucketedSlidingWindowedLongFunction(SerializableSupplier<F> windowFunctionSupplier, int numberOfBuckets) {
            super(windowFunctionSupplier, numberOfBuckets);
        }

        public void aggregateLong(long input) {
            currentFunction.aggregateLong(input);
        }

        public long getAsLong() {
            return aggregatedFunction.getAsLong();
        }
    }

}
