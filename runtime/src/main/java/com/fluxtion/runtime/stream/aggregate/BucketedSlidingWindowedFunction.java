package com.fluxtion.runtime.stream.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @param <T> Input type
 * @param <R> return type
 * @param <F> BaseSlidingWindowFunction
 */
public class BucketedSlidingWindowedFunction<T, R, F extends BaseSlidingWindowFunction<T, R, F>> {

    private final Supplier<F> windowFunctionSupplier;
    private final F aggregatedFunction;
    private final List<F> buckets;
    private int writePointer;

    public BucketedSlidingWindowedFunction(Supplier<F> windowFunctionSupplier, int numberOfBuckets) {
        this.windowFunctionSupplier = windowFunctionSupplier;
        aggregatedFunction = windowFunctionSupplier.get();
        buckets = new ArrayList<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets.add(windowFunctionSupplier.get());
        }
    }

    public void aggregate(T input) {
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
            F currentFunction = buckets.get(writePointer);
            aggregatedFunction.deduct(currentFunction);
            currentFunction.reset();
        }
    }

    public R get() {
        return aggregatedFunction.get();
    }

}
