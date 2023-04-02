package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Map.Entry;

public class GroupByFilterFlowFunctionWrapper {

    @NoTriggerReference
    private final SerializableFunction mapFunction;
    private final transient GroupByHashMap outputCollection = new GroupByHashMap();

    public <T> GroupByFilterFlowFunctionWrapper(SerializableFunction<T, Boolean> mapFunction) {
        this.mapFunction = mapFunction;
    }

    //required for serialised version
    public <K, V> GroupBy<K, V> filterValues(Object inputMap) {
        return filterValues((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> filterValues(GroupBy<K, V> inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Entry entry = (Entry) e;
            if ((boolean) mapFunction.apply(entry.getValue())) {
                outputCollection.toMap().put(entry.getKey(), entry.getValue());
            }
        });
        return outputCollection;
    }
}
