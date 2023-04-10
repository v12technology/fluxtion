package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

public class AggregateIdentityFlowFunction<T> implements AggregateFlowFunction<T, T, AggregateIdentityFlowFunction<T>> {
    T value;

    @Override
    public T reset() {
        value = null;
        return null;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public T aggregate(T input) {
        value = input;
        return value;
    }
}
