package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateIntValue extends BaseIntSlidingFunction<AggregateIntValue> {

    @Override
    public int resetInt() {
        value = 0;
        return getAsInt();
    }

    @Override
    public int aggregateInt(int input) {
        value = input;
        return getAsInt();
    }

    @Override
    public void combine(AggregateIntValue combine) {
        value = combine.value;
    }

    @Override
    public void deduct(AggregateIntValue deduct) {
//        value -= deduct.value;
    }


}