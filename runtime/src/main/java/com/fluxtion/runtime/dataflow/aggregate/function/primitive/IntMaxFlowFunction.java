package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntMaxFlowFunction extends AbstractIntFlowFunction<IntMaxFlowFunction> {

    @Override
    public int aggregateInt(int input) {
        value = reset ? input : Math.max(value, input);
        reset = false;
        return getAsInt();
    }

    @Override
    public void combine(IntMaxFlowFunction add) {
        aggregateInt(add.getAsInt());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
