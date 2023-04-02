package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class DoubleIdentityFlowFunction extends AbstractDoubleFlowFunction<DoubleIdentityFlowFunction> {

    @Override
    public double aggregateDouble(double input) {
        value = input;
        return getAsDouble();
    }

    @Override
    public void combine(DoubleIdentityFlowFunction combine) {
        value = combine.value;
    }

    @Override
    public boolean deductSupported() {
        return false;
    }

}