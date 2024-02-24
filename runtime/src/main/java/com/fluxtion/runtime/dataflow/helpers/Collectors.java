package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction.AggregateToListFactory;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToSetFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.List;
import java.util.Set;

public interface Collectors {

    static <T> SerializableFunction<T, Set<T>> toSet() {
        return new AggregateToSetFlowFunction<T>()::aggregate;
    }

    static <T> SerializableFunction<T, List<T>> toList() {
        return new AggregateToListFlowFunction<T>()::aggregate;
    }

    static <T> SerializableFunction<T, List<T>> toList(int maxElements) {
        return new AggregateToListFlowFunction<T>(maxElements)::aggregate;
    }

    static <T> SerializableSupplier<AggregateToListFlowFunction<T>> listFactory(int maximumElementCount) {
        return new AggregateToListFactory(maximumElementCount)::newList;
    }

    static <T> SerializableSupplier<AggregateToListFlowFunction<T>> listFactory() {
        return listFactory(-1);
    }

    static <T> SerializableSupplier<AggregateToSetFlowFunction<T>> setFactory() {
        return AggregateToSetFlowFunction::new;
    }

    static <T, K> SerializableSupplier<GroupByFlowFunctionWrapper<T, K, T, List<T>, AggregateToListFlowFunction<T>>>
    groupingByCollectToList(SerializableFunction<T, K> keyFunction) {
        GroupingFactory<T, K, ?, ?> factory = new GroupingFactory<>(keyFunction);
        return factory::groupByToList;
    }

    static <T, K> SerializableSupplier<GroupByFlowFunctionWrapper<T, K, T, T, AggregateIdentityFlowFunction<T>>>
    groupingBy(SerializableFunction<T, K> keyFunction) {
        GroupingFactory<T, K, ?, ?> factory = new GroupingFactory<>(keyFunction);
        return factory::groupBy;
    }

    static <T, K, O, F extends AggregateFlowFunction<T, O, F>> SerializableSupplier<GroupByFlowFunctionWrapper<T, K, T, O, F>>
    groupingBy(SerializableFunction<T, K> keyFunction, SerializableSupplier<F> supplier) {
        GroupingFactory<T, K, O, F> factory = new GroupingFactory<>(keyFunction, supplier);
        return factory::groupingByXXX;
    }
}
