package com.fluxtion.runtime.dataflow.aggregate;

import java.util.function.LongSupplier;

public interface AggregateLongFlowFunction<T extends AggregateLongFlowFunction<T>>
        extends AggregateFlowFunction<Long, Long, T>, LongSupplier {
    long resetLong();

    long aggregateLong(long input);
}
