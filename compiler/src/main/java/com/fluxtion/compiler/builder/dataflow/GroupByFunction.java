package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByMapFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

public interface GroupByFunction {

    static <K, V, A, O, G extends GroupBy<K, V>> SerializableBiFunction<G, A, GroupBy<K, O>> mapValueByKey(
            SerializableBiFunction<V, A, O> mappingBiFunction,
            SerializableFunction<A, K> keyFunction) {
        GroupByMapFlowFunction invoker = new GroupByMapFlowFunction(keyFunction, mappingBiFunction, null);
        return invoker::mapKeyedValue;
    }

}
