package com.fluxtion.runtime.stream.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @param <I> Input type
 * @param <R> return type
 * @param <T> BaseSlidingWindowFunction
 */
public class SlidingWindowedValueStream<I, R, T extends BaseSlidingWindowFunction<I, R, T>> {

    private final Supplier<T> windowFunctionSupplier;
    private final T aggregatedFunction;
    private final List<T> buckets;
    private int writePointer;

    public SlidingWindowedValueStream(Supplier<T> windowFunctionSupplier, int numberOfBuckets) {
        this.windowFunctionSupplier = windowFunctionSupplier;
        aggregatedFunction = windowFunctionSupplier.get();
        buckets = new ArrayList<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets.add(windowFunctionSupplier.get());
        }
    }

    public void aggregate(I input) {
        buckets.get(writePointer).aggregate(input);
    }

    public void roll(){
        roll(1);
    }

    public void roll(int windowsToRoll) {
        for (int i = 0; i < windowsToRoll; i++) {
            //add the current function to aggregate
            //get the next, deduct from aggregate, reset function and bump write pointer
            aggregatedFunction.combine(buckets.get(writePointer));
            writePointer = ++writePointer % buckets.size();
            T currentFunction = buckets.get(writePointer);
            aggregatedFunction.deduct(currentFunction);
            currentFunction.reset();
        }
    }

    public R get() {
        return aggregatedFunction.get();
    }

}
