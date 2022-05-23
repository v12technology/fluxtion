package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.aggregate.AggregateFunction;

public class AggregateIdentity<T> implements AggregateFunction<T, T, AggregateIdentity<T>> {
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
