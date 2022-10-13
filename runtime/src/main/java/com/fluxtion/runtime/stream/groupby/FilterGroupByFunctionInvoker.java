package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.Anchor;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Map.Entry;

public class FilterGroupByFunctionInvoker {//extends GroupByInvoker {

    //    @NoTriggerReference
//    @SepNode
    private final SerializableFunction mapFunction;
    private final transient GroupByCollection outputCollection = new GroupByCollection();

    public <T> FilterGroupByFunctionInvoker(SerializableFunction<T, Boolean> mapFunction) {
//        super(mapFunction);
        this.mapFunction = mapFunction;
        Anchor.anchorCaptured(this, mapFunction);
//        Anchor.anchorCaptured(this, mapFunction);
    }

    //required for serialised version
    public <K, V> GroupBy<K, V> filterValues(Object inputMap) {
        return filterValues((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> filterValues(GroupBy<K, V> inputMap) {
        outputCollection.reset();
        inputMap.map().entrySet().forEach(e -> {
            Entry entry = (Entry) e;
            if ((boolean) mapFunction.apply(entry.getValue())) {
                outputCollection.map().put(entry.getKey(), entry.getValue());
            }
        });
        return outputCollection;
    }
}
