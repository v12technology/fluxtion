package com.fluxtion.runtime.stream.aggregate;

import java.util.function.IntSupplier;

public interface IntAggregateFunction <T extends IntAggregateFunction<T>>
        extends AggregateFunction<Integer, Integer, T>, IntSupplier {
    int resetInt();

    int aggregateInt(int input);
}
