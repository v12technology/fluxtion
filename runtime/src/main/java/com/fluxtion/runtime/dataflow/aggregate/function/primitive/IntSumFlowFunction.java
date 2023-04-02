package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntSumFlowFunction extends AbstractIntFlowFunction<IntSumFlowFunction> {

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
    public void combine(IntSumFlowFunction combine) {
        value += combine.value;
    }

    @Override
    public void deduct(IntSumFlowFunction deduct) {
        value -= deduct.value;
    }

    @Override
    public String toString() {
        return "AggregateIntSum{" +
                "value=" + value +
                '}';
    }
}
