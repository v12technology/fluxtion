package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateDoubleFlowFunction;

public abstract class AbstractDoubleFlowFunction<T extends AbstractDoubleFlowFunction<T>>
        implements DoubleFlowFunction, AggregateDoubleFlowFunction<T> {

    protected double value;

    @Override
    public double resetDouble() {
        value = 0;
        return getAsDouble();
    }

    @Override
    public Double reset() {
        return resetDouble();
    }

    @Override
    public Double aggregate(Double input) {
        return aggregateDouble(input);
    }

    public Double get() {
        return getAsDouble();
    }

    @Override
    public double getAsDouble() {
        return value;
    }

}
