package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.FilterGroupByFunctionInvoker;
import com.fluxtion.runtime.stream.groupby.MapGroupByFunctionInvoker;

public interface GroupByFunction {


    static <K, V, A, O, G extends GroupByStreamed<K, V>> SerializableBiFunction<
            G, A, GroupByStreamed<K, O>> mapValueByKey(
            SerializableBiFunction<V, A, O> mappingBiFunction,
            SerializableFunction<A, K> keyFunction
    ) {
        MapGroupByFunctionInvoker invoker = new MapGroupByFunctionInvoker(keyFunction, mappingBiFunction, null);
        return invoker::mapKeyedValue;
    }

    static <K, V, G extends GroupByStreamed<K, V>> SerializableFunction<G, GroupByStreamed<K, V>> filterValues(
            SerializableFunction<V, Boolean> mappingFunction) {
        return new FilterGroupByFunctionInvoker(mappingFunction)::filterValues;
    }

}
