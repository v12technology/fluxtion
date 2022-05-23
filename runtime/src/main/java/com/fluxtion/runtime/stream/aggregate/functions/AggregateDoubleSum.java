package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleSum extends BaseDoubleSlidingFunction<AggregateDoubleSum> {

    @Override
    public double aggregateDouble(double input) {
        value += input;
        return getAsDouble();
    }

    @Override
    public void combine(AggregateDoubleSum combine) {
        value += combine.value;
    }

    @Override
    public void deduct(AggregateDoubleSum deduct) {
        value -= deduct.value;
    }

}
