package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.aggregate.LongAggregateFunction;

public abstract class BaseLongSlidingFunction<T extends BaseLongSlidingFunction<T>>
        implements LongEventStream, LongAggregateFunction<T> {

    protected long value;

    @Override
    public long resetLong() {
        value = 0;
        return getAsLong();
    }


    @Override
    public Long reset() {
        return resetLong();
    }

    @Override
    public Long aggregate(Long input) {
        return aggregateLong(input);
    }

    public Long get() {
        return getAsLong();
    }

    @Override
    public long getAsLong() {
        return value;
    }

//    @Override
//    public void setUpdateTriggerNode(Object updateTriggerNode) {
//    }
//
//    @Override
//    public void setPublishTriggerNode(Object publishTriggerNode) {
//    }
//
//    @Override
//    public void setResetTriggerNode(Object resetTriggerNode) {
//    }
//
//    @Override
//    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
//    }

}
