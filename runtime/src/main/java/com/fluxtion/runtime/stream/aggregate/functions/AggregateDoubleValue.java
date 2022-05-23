package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleValue extends BaseDoubleSlidingWindowFunction<AggregateDoubleValue> {

    @Override
    public double resetDouble() {
        value = 0;
        return getAsDouble();
    }

    @Override
    public double aggregateDouble(double input) {
        value = input;
        return getAsDouble();
    }

    @Override
    public void combine(AggregateDoubleValue combine) {
        value = combine.value;
    }

    @Override
    public void deduct(AggregateDoubleValue deduct) {
//        value -= deduct.value;
    }

}