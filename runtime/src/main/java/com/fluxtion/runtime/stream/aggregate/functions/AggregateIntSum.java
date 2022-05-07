package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;

public class AggregateIntSum extends BaseSlidingWindowFunction<Integer, Integer, AggregateIntSum> {

    private int sum;

    @Override
    public void combine(AggregateIntSum combine) {
        sum += combine.sum;
    }

    @Override
    public void deduct(AggregateIntSum deduct) {
        sum -= deduct.sum;
    }

    @Override
    public Integer reset() {
        sum = 0;
        return 0;
    }

    @Override
    public Integer aggregate(Integer input) {
        sum += input;
        return sum;
    }

    public Integer get(){
        return sum;
    }
}
