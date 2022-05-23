package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.aggregate.DoubleAggregateWindowFunction;

public abstract class BaseDoubleSlidingWindowFunction<T extends BaseDoubleSlidingWindowFunction<T>>
        implements DoubleEventStream, DoubleAggregateWindowFunction<T> {

    protected double value;

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
