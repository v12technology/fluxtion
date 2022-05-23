package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongValue extends BaseLongSlidingWindowFunction<AggregateLongValue> {

    @Override
    public long resetLong() {
        value = 0;
        return getAsLong();
    }

    @Override
    public long aggregateLong(long input) {
        value = input;
        return getAsLong();
    }

    @Override
    public void combine(AggregateLongValue combine) {
        value = combine.value;
    }

    @Override
    public void deduct(AggregateLongValue deduct) {
//        value -= deduct.value;
    }

}