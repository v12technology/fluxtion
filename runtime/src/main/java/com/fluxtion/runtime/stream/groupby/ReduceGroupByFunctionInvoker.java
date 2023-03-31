package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;
import lombok.Value;

@Value
public class ReduceGroupByFunctionInvoker {

    AggregateFunction aggregateFunction;

    public <R> R reduceValues(GroupByStreamed inputMap) {
        aggregateFunction.reset();
        inputMap.toMap().values().forEach(aggregateFunction::aggregate);
        return (R) aggregateFunction.get();
    }

    public Object reduceValues(Object inputMap) {
        return reduceValues((GroupByStreamed) inputMap);
    }
}
