package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.dataflow.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.List;

public class GroupingFactory<T, K> {
    private final SerializableFunction<T, K> keyFunction;

    public GroupingFactory(SerializableFunction<T, K> keyFunction) {
        this.keyFunction = keyFunction;
    }

    public SerializableFunction<T, K> getKeyFunction() {
        return keyFunction;
    }

    public GroupByFlowFunctionWrapper<T, K, T, List<T>, AggregateToListFlowFunction<T>> groupByToList() {
        return new GroupByFlowFunctionWrapper<>(keyFunction, Mappers::identity, Collectors.toList());
    }

    public GroupByFlowFunctionWrapper<T, K, T, T, AggregateIdentityFlowFunction<T>> groupBy() {
        return new GroupByFlowFunctionWrapper<>(keyFunction, Mappers::identity, Aggregates.identityFactory());
    }


}
