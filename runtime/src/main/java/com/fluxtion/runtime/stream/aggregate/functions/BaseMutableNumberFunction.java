package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.MutableNumber;

public abstract class BaseMutableNumberFunction
        implements AggregateFunction<Number, Number, BaseMutableNumberFunction> {
    MutableNumber mutableNumber = new MutableNumber();

    @Override
    public Number reset() {
        return mutableNumber.reset();
    }

    @Override
    public void combine(BaseMutableNumberFunction add) {

    }

    @Override
    public void deduct(BaseMutableNumberFunction add) {

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
