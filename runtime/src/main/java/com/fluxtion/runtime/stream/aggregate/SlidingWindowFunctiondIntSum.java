package com.fluxtion.runtime.stream.aggregate;

public class SlidingWindowFunctiondIntSum extends BaseSlidingWindowFunction<Integer, Integer, SlidingWindowFunctiondIntSum> {

    private int sum;

    @Override
    public void combine(SlidingWindowFunctiondIntSum combine) {
        sum += combine.sum;
    }

    @Override
    public void deduct(SlidingWindowFunctiondIntSum deduct) {
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
