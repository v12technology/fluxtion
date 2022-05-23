package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongMax extends BaseLongSlidingFunction<AggregateLongMax> {

    @Override
    public long aggregateLong(long input) {
        value = Math.max(value, input);
        return getAsLong();
    }

    @Override
    public void combine(AggregateLongMax add) {
        aggregateLong(add.getAsLong());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
