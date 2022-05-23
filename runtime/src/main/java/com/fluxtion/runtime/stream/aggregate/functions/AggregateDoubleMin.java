package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleMin extends BaseDoubleSlidingFunction<AggregateDoubleMin> {

    @Override
    public double aggregateDouble(double input) {
        value = Math.min(value, input);
        return getAsDouble();
    }

    @Override
    public void combine(AggregateDoubleMin add) {
        aggregateDouble(add.getAsDouble());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
