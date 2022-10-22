package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Map;
import java.util.Map.Entry;

public class MapGroupByFunctionInvoker {

    private final SerializableFunction mapFunction;
    private final transient GroupByCollection outputCollection = new GroupByCollection();

    public <T, R> MapGroupByFunctionInvoker(SerializableFunction<T, R> mapFunction) {
        this.mapFunction = mapFunction;
    }

    //required for serialised version
    public <K, V> GroupBy<K, V> mapValues(Object inputMap) {
        return mapValues((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapKeys(Object inputMap) {
        return mapKeys((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapEntry(Object inputMap) {
        return mapEntry((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapValues(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.map().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.map().put(entry.getKey(), mapFunction.apply(entry.getValue()));
        });
        return outputCollection;
    }

    public <K, V> GroupBy<K, V> mapKeys(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.map().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.map().put(mapFunction.apply(entry.getKey()), entry.getValue());
        });
        return outputCollection;
    }

    public <K, V> GroupBy<K, V> mapEntry(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.map().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) mapFunction.apply(e);
            outputCollection.map().put(entry.getKey(), entry.getValue());
        });
        return outputCollection;
    }
}
