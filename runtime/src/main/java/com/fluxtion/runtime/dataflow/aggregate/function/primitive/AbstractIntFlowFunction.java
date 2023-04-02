package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateIntFlowFunction;

public abstract class AbstractIntFlowFunction<T extends AbstractIntFlowFunction<T>>
        implements IntFlowFunction, AggregateIntFlowFunction<T> {

    protected int value;

    @Override
    public Integer reset() {
        return resetInt();
    }

    @Override
    public int resetInt() {
        value = 0;
        return getAsInt();
    }

    @Override
    public Integer aggregate(Integer input) {
        return aggregateInt(input);
    }

    public Integer get() {
        return getAsInt();
    }

    @Override
    public int getAsInt() {
        return value;
    }

}
