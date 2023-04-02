package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntMaxFlowFunction extends AbstractIntFlowFunction<IntMaxFlowFunction> {

    @Override
    public int aggregateInt(int input) {
        value = Math.max(value, input);
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
