package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
import lombok.Value;

@Value
public class ReduceGroupByFunctionInvoker {

    AggregateFunction aggregateFunction;

    public <R> R reduceValues(GroupByStreamed inputMap) {
        aggregateFunction.reset();
        inputMap.map().values().forEach(aggregateFunction::aggregate);
        return (R) aggregateFunction.get();
    }

    public Object reduceValues(Object inputMap) {
        return reduceValues((GroupByStreamed) inputMap);
    }
}
