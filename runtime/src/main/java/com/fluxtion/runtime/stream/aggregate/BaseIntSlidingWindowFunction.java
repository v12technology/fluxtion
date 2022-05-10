package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.EventStream.IntEventStream;

public abstract class BaseIntSlidingWindowFunction<T extends BaseIntSlidingWindowFunction<T>>
        extends BaseSlidingWindowFunction<Integer, Integer, T> implements IntEventStream {

    protected int value;
    public abstract int resetInt();

    public abstract int aggregateInt(int input);

    @Override
    public Integer reset() {
        return resetInt();
    }

    @Override
    public Integer aggregate(Integer input) {
        return aggregateInt(input);
    }

    public Integer get() {
        return getAsInt();
    }

    @Override
    public int getAsInt() {
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
