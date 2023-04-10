package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntMinFlowFunction extends AbstractIntFlowFunction<IntMinFlowFunction> {

    @Override
    public int aggregateInt(int input) {
        value = Math.min(value, input);
        return getAsInt();
    }

    @Override
    public void combine(IntMinFlowFunction add) {
        aggregateInt(add.getAsInt());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
