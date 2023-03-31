package com.fluxtion.runtime.stream;

import java.util.function.LongSupplier;

public interface LongAggregateFunction<T extends LongAggregateFunction<T>>
        extends AggregateFunction<Long, Long, T>, LongSupplier {
    long resetLong();

    long aggregateLong(long input);
}
