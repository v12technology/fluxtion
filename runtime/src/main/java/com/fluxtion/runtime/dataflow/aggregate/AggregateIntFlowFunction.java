package com.fluxtion.runtime.dataflow.aggregate;

import java.util.function.IntSupplier;

public interface AggregateIntFlowFunction<T extends AggregateIntFlowFunction<T>>
        extends AggregateFlowFunction<Integer, Integer, T>, IntSupplier {
    int resetInt();

    int aggregateInt(int input);
}
