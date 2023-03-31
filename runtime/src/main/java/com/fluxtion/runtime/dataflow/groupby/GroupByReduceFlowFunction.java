package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import lombok.Value;

@Value
public class GroupByReduceFlowFunction {

    AggregateFlowFunction aggregateFunction;

    public <R> R reduceValues(GroupBy inputMap) {
        aggregateFunction.reset();
        inputMap.toMap().values().forEach(aggregateFunction::aggregate);
        return (R) aggregateFunction.get();
    }

    public Object reduceValues(Object inputMap) {
        return reduceValues((GroupBy) inputMap);
    }
}
