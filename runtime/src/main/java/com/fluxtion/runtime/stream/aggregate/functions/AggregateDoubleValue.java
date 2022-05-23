package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleValue extends BaseDoubleSlidingFunction<AggregateDoubleValue> {

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
    public boolean deductSupported(){
        return false;
    }

}