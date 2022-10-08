package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;

public interface GroupByFunction {

    static <K, V, O, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, O>> mapValues(
            SerializableFunction<V, O> mappingFunction) {
        return new MapGroupByFunctionInvoker(mappingFunction)::mapValues;
    }

    static <K, V, G extends GroupBy<K, V>> SerializableFunction<G, GroupBy<K, V>> filterValues(
            SerializableFunction<V, Boolean> mappingFunction) {
        return new FilterGroupByFunctionInvoker(mappingFunction)::filterValues;
    }
}
