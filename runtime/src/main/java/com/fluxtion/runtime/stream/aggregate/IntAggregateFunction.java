package com.fluxtion.runtime.stream.aggregate;

import java.util.function.IntSupplier;

public interface IntAggregateFunction <T extends IntAggregateFunction<T>>
        extends AggregateWindowFunction<Integer, Integer, T>, IntSupplier {
    int resetInt();

    int aggregateInt(int input);
}
