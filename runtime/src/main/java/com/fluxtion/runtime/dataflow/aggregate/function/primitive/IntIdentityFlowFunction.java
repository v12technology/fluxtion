package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class IntIdentityFlowFunction extends AbstractIntFlowFunction<IntIdentityFlowFunction> {

    @Override
    public int aggregateInt(int input) {
        value = input;
        return getAsInt();
    }

    @Override
    public void combine(IntIdentityFlowFunction combine) {
        value = combine.value;
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}