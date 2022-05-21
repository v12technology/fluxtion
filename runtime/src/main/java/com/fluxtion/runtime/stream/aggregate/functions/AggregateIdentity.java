package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;

public class AggregateIdentity<T> extends BaseSlidingWindowFunction<T, T, AggregateIdentity<T>> {
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
