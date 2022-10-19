package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Map;
import java.util.Map.Entry;

public class MapGroupByFunctionInvoker {//extends GroupByInvoker {

    private final SerializableFunction mapFunction;
    private final transient GroupByCollection outputCollection = new GroupByCollection();

    public <T, R> MapGroupByFunctionInvoker(SerializableFunction<T, R> mapFunction) {
//        super(mapFunction);
        this.mapFunction = mapFunction;
//        Anchor.anchorCaptured(this, mapFunction);
    }

    //required for serialised version
    public <K, V> GroupBy<K, V> mapValues(Object inputMap) {
        return mapValues((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapValues(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.map().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.map().put(entry.getKey(), mapFunction.apply(entry.getValue()));
        });
        return outputCollection;
    }
}
