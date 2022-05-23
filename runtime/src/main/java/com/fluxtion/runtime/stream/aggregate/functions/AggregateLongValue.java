package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateLongValue extends BaseLongSlidingFunction<AggregateLongValue> {

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
    public boolean deductSupported(){
        return false;
    }

}