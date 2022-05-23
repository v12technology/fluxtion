package com.fluxtion.runtime.stream.aggregate;

import java.util.function.LongSupplier;

public interface LongAggregateFunction <T extends LongAggregateFunction<T>>
        extends AggregateWindowFunction<Long, Long, T>, LongSupplier {
    long resetLong();

    long aggregateLong(long input);
}
