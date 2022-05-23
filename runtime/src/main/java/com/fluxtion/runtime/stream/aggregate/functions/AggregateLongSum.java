package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongSum extends BaseLongSlidingFunction<AggregateLongSum> {

    @Override
    public long aggregateLong(long input) {
        value += input;
        return getAsLong();
    }

    @Override
    public void combine(AggregateLongSum combine) {
        value += combine.value;
    }

    @Override
    public void deduct(AggregateLongSum deduct) {
        value -= deduct.value;
    }

}
