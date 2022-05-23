package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongMin extends BaseLongSlidingFunction<AggregateLongMin> {

    @Override
    public long aggregateLong(long input) {
        value = Math.min(value, input);
        return getAsLong();
    }

    @Override
    public void combine(AggregateLongMin add) {
        aggregateLong(add.getAsLong());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
