package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntMinFlowFunction extends AbstractIntFlowFunction<IntMinFlowFunction> {

    @Override
    public int aggregateInt(int input) {
        value = reset ? input : Math.min(value, input);
        reset = false;
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
