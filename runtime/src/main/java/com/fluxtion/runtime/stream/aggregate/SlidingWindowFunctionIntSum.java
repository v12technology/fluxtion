package com.fluxtion.runtime.stream.aggregate;

public class SlidingWindowFunctionIntSum extends BaseSlidingWindowFunction<Integer, Integer, SlidingWindowFunctionIntSum> {

    private int sum;

    @Override
    public void combine(SlidingWindowFunctionIntSum combine) {
        sum += combine.sum;
//        System.out.println("after combine window sum:" + sum + " to combine:" + combine.sum);
    }

    @Override
    public void deduct(SlidingWindowFunctionIntSum deduct) {
        sum -= deduct.sum;
//        System.out.println("after deduct window sum:" + sum + " to remove:" + deduct.sum);
    }

    @Override
    public Integer reset() {
        sum = 0;
        return 0;
    }

    @Override
    public Integer aggregate(Integer input) {
        sum += input;
//        System.out.println("add:" + input + " this bucket sum:" + sum);
        return sum;
    }

    public Integer get(){
        return sum;
    }
}
