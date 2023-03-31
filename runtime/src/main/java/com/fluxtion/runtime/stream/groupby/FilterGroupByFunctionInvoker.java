package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;

import java.util.Map.Entry;

public class FilterGroupByFunctionInvoker {

    @NoTriggerReference
    private final SerializableFunction mapFunction;
    private final transient GroupByCollection outputCollection = new GroupByCollection();

    public <T> FilterGroupByFunctionInvoker(SerializableFunction<T, Boolean> mapFunction) {
        this.mapFunction = mapFunction;
    }

    //required for serialised version
    public <K, V> GroupByStreamed<K, V> filterValues(Object inputMap) {
        return filterValues((GroupByStreamed) inputMap);
    }

    public <K, V> GroupByStreamed<K, V> filterValues(GroupByStreamed<K, V> inputMap) {
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
