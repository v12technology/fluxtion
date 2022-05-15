package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;

public class AggregateCounting<T> extends BaseSlidingWindowFunction<T, Integer, AggregateCounting<T>> implements IntEventStream {
    private int count;

    @Override
    public Integer reset() {
        count = 0;
        return get();
    }

    @Override
    public int getAsInt() {
        return count;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

    @Override
    public Integer aggregate(T input) {
        return ++count;
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
