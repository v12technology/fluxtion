package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.MutableNumber;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;

public abstract class AbstractMutableNumberFlowFunction
        implements AggregateFlowFunction<Number, Number, AbstractMutableNumberFlowFunction> {
    MutableNumber mutableNumber = new MutableNumber();

    @Override
    public Number reset() {
        return mutableNumber.reset();
    }

    @Override
    public void combine(AbstractMutableNumberFlowFunction add) {

    }

    @Override
    public void deduct(AbstractMutableNumberFlowFunction add) {

    }

    @Override
    public Number get() {
        return mutableNumber;
    }

    @Override
    public Number aggregate(Number input) {
        return null;
    }
}
