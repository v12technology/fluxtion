package com.fluxtion.runtime.stream.aggregate;

public class SlidingWindowedIntSum extends BaseSlidingWindowFunction<Integer, Integer, SlidingWindowedIntSum> {

    private int sum;

    @Override
    public void combine(SlidingWindowedIntSum combine) {
        sum += combine.sum;
    }

    @Override
    public void deduct(SlidingWindowedIntSum deduct) {
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
