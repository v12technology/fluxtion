package com.fluxtion.runtime.stream.aggregate.functions;

public class AggregateIntSum extends BaseIntSlidingWindowFunction<AggregateIntSum> {

    @Override
    public int resetInt() {
        value = 0;
        return getAsInt();
    }

    @Override
    public int aggregateInt(int input) {
        value += input;
        return getAsInt();
    }

    @Override
    public void combine(AggregateIntSum combine) {
        value += combine.value;
    }

    @Override
    public void deduct(AggregateIntSum deduct) {
        value -= deduct.value;
    }

    @Override
    public String toString() {
        return "AggregateIntSum{" +
                "value=" + value +
                '}';
    }
}
