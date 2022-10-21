package com.fluxtion.runtime.stream.aggregate.functions;

import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;

public class AggregateCounting<T> implements AggregateFunction<T, Integer, AggregateCounting<T>>, IntEventStream {
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

    public int increment(int input) {
        count++;
        return getAsInt();
    }

    @Override
    public void combine(AggregateCounting<T> add) {
        count += add.count;
    }

    @Override
    public void deduct(AggregateCounting<T> add) {
        count -= add.count;
    }

    public int increment(double input) {
        return increment(1);
    }

    public int increment(long input) {
        return increment(1);
    }

//    @Override
//    public void setUpdateTriggerNode(Object updateTriggerNode) {
//
//    }
//
//    @Override
//    public void setPublishTriggerNode(Object publishTriggerNode) {
//
//    }
//
//    @Override
//    public void setResetTriggerNode(Object resetTriggerNode) {
//
//    }
//
//    @Override
//    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {
//
//    }
}
