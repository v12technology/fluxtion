package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateIntMin extends BaseIntSlidingFunction<AggregateIntMin> {

    @Override
    public int aggregateInt(int input) {
        value = Math.min(value, input);
        return getAsInt();
    }

    @Override
    public void combine(AggregateIntMin add) {
        aggregateInt(add.getAsInt());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
