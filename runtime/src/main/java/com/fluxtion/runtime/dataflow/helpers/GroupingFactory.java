package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.List;

public class GroupingFactory<T, K, O, F extends AggregateFlowFunction<T, O, F>> {
    private final SerializableFunction<T, K> keyFunction;
    private SerializableSupplier<F> aggregateFunctionSupplier;

    public GroupingFactory(SerializableFunction<T, K> keyFunction) {
        this.keyFunction = keyFunction;
    }

    public GroupingFactory(SerializableFunction<T, K> keyFunction, SerializableSupplier<F> aggregateFunctionSupplier) {
        this.keyFunction = keyFunction;
        this.aggregateFunctionSupplier = aggregateFunctionSupplier;
    }

    public SerializableFunction<T, K> getKeyFunction() {
        return keyFunction;
    }

    public GroupByFlowFunctionWrapper<T, K, T, List<T>, AggregateToListFlowFunction<T>> groupByToList() {
        SerializableSupplier<AggregateToListFlowFunction<T>> list = Collectors.toList();
        return new GroupByFlowFunctionWrapper<>(keyFunction, Mappers::identity, list);
    }

    public GroupByFlowFunctionWrapper<T, K, T, T, AggregateIdentityFlowFunction<T>> groupBy() {
        SerializableSupplier<AggregateIdentityFlowFunction<T>> aggregateIdentityFlowFunctionSerializableSupplier = Aggregates.identityFactory();
        return new GroupByFlowFunctionWrapper<>(keyFunction, Mappers::identity, aggregateIdentityFlowFunctionSerializableSupplier);
    }

    public GroupByFlowFunctionWrapper<T, K, T, O, F> groupingByXXX() {
        return new GroupByFlowFunctionWrapper<>(keyFunction, Mappers::identity, aggregateFunctionSupplier);
    }

    public SerializableSupplier getAggregateFunctionSupplier() {
        return aggregateFunctionSupplier;
    }

    public void setAggregateFunctionSupplier(SerializableSupplier aggregateFunctionSupplier) {
        this.aggregateFunctionSupplier = aggregateFunctionSupplier;
    }
}
