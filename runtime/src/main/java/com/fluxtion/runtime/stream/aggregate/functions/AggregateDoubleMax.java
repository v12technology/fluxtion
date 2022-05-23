package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateDoubleMax extends BaseDoubleSlidingFunction<AggregateDoubleMax> {

    @Override
    public double aggregateDouble(double input) {
        value = Math.max(value, input);
        return getAsDouble();
    }

    @Override
    public void combine(AggregateDoubleMax add) {
        aggregateDouble(add.getAsDouble());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
