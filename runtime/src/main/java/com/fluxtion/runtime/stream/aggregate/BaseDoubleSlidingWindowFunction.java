package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;

public abstract class BaseDoubleSlidingWindowFunction<T extends BaseDoubleSlidingWindowFunction<T>>
        extends BaseSlidingWindowFunction<Double, Double, T> implements DoubleEventStream {

    protected double value;
    public abstract double resetDouble();

    public abstract double aggregateDouble(double input);

    @Override
    public Double reset() {
        return resetDouble();
    }

    @Override
    public Double aggregate(Double input) {
        return aggregateDouble(input);
    }

    public Double get() {
        return getAsDouble();
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {
    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {
    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {
    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
    }

}
