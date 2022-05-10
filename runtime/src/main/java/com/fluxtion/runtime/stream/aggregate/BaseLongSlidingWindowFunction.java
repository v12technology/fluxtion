package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.stream.EventStream.LongEventStream;

public abstract class BaseLongSlidingWindowFunction<T extends BaseLongSlidingWindowFunction<T>>
        extends BaseSlidingWindowFunction<Long, Long, T> implements LongEventStream {

    protected long value;

    public abstract long resetLong();

    public abstract long aggregateLong(long input);

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
