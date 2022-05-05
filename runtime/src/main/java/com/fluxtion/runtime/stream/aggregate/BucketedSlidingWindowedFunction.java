package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T> Input type
 * @param <R> return type
 * @param <F> BaseSlidingWindowFunction
 */
public class BucketedSlidingWindowedFunction<T, R, F extends BaseSlidingWindowFunction<T, R, F>> {

    private final SerializableSupplier<F> windowFunctionSupplier;
    private final F aggregatedFunction;
    private final F currentFunction;
    private final List<F> buckets;
    private int writePointer;
    private boolean allBucketsFilled = false;

    public BucketedSlidingWindowedFunction(SerializableSupplier<F> windowFunctionSupplier, int numberOfBuckets) {
        this.windowFunctionSupplier = windowFunctionSupplier;
        aggregatedFunction = windowFunctionSupplier.get();
        currentFunction = windowFunctionSupplier.get();
        buckets = new ArrayList<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets.add(windowFunctionSupplier.get());
        }
    }

    public void aggregate(T input) {
        currentFunction.aggregate(input);
    }

    public void roll(){
        roll(1);
    }

    public void roll(int windowsToRoll) {
//        System.out.println("roll count:" + windowsToRoll);
        for (int i = 0; i < windowsToRoll; i++) {
            //add the current function to aggregate
            //get the next, deduct from aggregate, reset function and bump write pointer
//            System.out.println("head of list pointer:" + writePointer);
            F oldFunction = buckets.get(writePointer);
            aggregatedFunction.combine(currentFunction);
            aggregatedFunction.deduct(oldFunction);
            oldFunction.reset();
            oldFunction.combine(currentFunction);
            currentFunction.reset();
            writePointer++;
            allBucketsFilled = allBucketsFilled | writePointer == buckets.size();
            writePointer = writePointer % buckets.size();
//            System.out.println("new head of list:" + writePointer);
        }
    }

    public boolean isAllBucketsFilled() {
        return allBucketsFilled;
    }

    public R get() {
        return aggregatedFunction.get();
    }

}
