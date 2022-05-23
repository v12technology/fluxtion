package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateIntMax extends BaseIntSlidingFunction<AggregateIntMax> {

    @Override
    public int aggregateInt(int input) {
        value = Math.max(value, input);
        return getAsInt();
    }

    @Override
    public void combine(AggregateIntMax add) {
        aggregateInt(add.getAsInt());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
