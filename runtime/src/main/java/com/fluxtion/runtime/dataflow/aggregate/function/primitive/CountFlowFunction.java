package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

public class CountFlowFunction<T> implements AggregateFlowFunction<T, Integer, CountFlowFunction<T>>, IntFlowFunction {
    private int count;

    @Override
    public Integer reset() {
        count = 0;
        return get();
    }

    @Override
    public int getAsInt() {
        return count;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

    @Override
    public Integer aggregate(T input) {
        return ++count;
    }

    public int increment(int input) {
        count++;
        return getAsInt();
    }

    @Override
    public void combine(CountFlowFunction<T> add) {
        count += add.count;
    }

    @Override
    public void deduct(CountFlowFunction<T> add) {
        count -= add.count;
    }

    public int increment(double input) {
        return increment(1);
    }

    public int increment(long input) {
        return increment(1);
    }

}
