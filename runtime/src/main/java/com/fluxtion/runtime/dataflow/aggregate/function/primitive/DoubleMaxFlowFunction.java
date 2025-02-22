package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

public class DoubleMaxFlowFunction extends AbstractDoubleFlowFunction<DoubleMaxFlowFunction> {

    @Override
    public double aggregateDouble(double input) {
        if (!Double.isNaN(input)) {
            value = Double.isNaN(value) ? input : Math.max(value, input);
        }
        return getAsDouble();
    }

    @Override
    public void combine(DoubleMaxFlowFunction add) {
        aggregateDouble(add.getAsDouble());
    }

    @Override
    public boolean deductSupported() {
        return false;
    }
}
