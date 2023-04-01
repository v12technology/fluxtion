package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.dataflow.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToListFlowFunction.AggregateToListFactory;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateToSetFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.List;

public interface Collectors {

    static <T> SerializableSupplier<AggregateToListFlowFunction<T>> toList(int maximumElementCount) {
        return new AggregateToListFactory(maximumElementCount)::newList;
    }

    static <T> SerializableSupplier<AggregateToListFlowFunction<T>> toList() {
        return toList(-1);
    }

    static <T> SerializableSupplier<AggregateToSetFlowFunction<T>> toSet() {
        return AggregateToSetFlowFunction::new;
    }

    static <T, K> SerializableSupplier<GroupByFlowFunctionWrapper<T, K, T, List<T>, AggregateToListFlowFunction<T>>>
    groupingByCollectToList(SerializableFunction<T, K> keyFunction) {
        GroupingFactory<T, K> factory = new GroupingFactory<>(keyFunction);
        return factory::groupByToList;
    }

    static <T, K> SerializableSupplier<GroupByFlowFunctionWrapper<T, K, T, T, AggregateIdentityFlowFunction<T>>>
    groupingBy(SerializableFunction<T, K> keyFunction) {
        GroupingFactory<T, K> factory = new GroupingFactory<>(keyFunction);
        return factory::groupBy;
    }
}
