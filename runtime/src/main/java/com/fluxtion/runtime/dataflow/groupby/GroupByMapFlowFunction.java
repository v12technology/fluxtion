package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.dataflow.groupby.GroupBy.KeyValue;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public class GroupByMapFlowFunction {

    private final SerializableFunction mapFunction;
    private final SerializableBiFunction mapFrom2MapsBiFunction;
    @SepNode
    public Object defaultValue;

    private final transient GroupByHashMap outputCollection = new GroupByHashMap();
    private final transient GroupByView wrappedCollection = new GroupByView();

    public <T, R> GroupByMapFlowFunction(SerializableFunction<T, R> mapFunction) {
        this(mapFunction, null);
    }

    public <A, B, R> GroupByMapFlowFunction(SerializableBiFunction<A, B, R> mapFrom2MapsBiFunction) {
        this(null, mapFrom2MapsBiFunction);
    }

    public <K, V, A, O> GroupByMapFlowFunction(SerializableFunction<A, K> mapFunction, SerializableBiFunction<V, A, O> mapFrom2MapsBiFunction) {
        this.mapFunction = mapFunction;
        this.mapFrom2MapsBiFunction = mapFrom2MapsBiFunction;
    }

    public <K, V, A, O> GroupByMapFlowFunction(SerializableFunction<A, K> mapFunction, SerializableBiFunction<V, A, O> mapFrom2MapsBiFunction, Object defaultValue) {
        this.mapFunction = mapFunction;
        this.mapFrom2MapsBiFunction = mapFrom2MapsBiFunction;
        this.defaultValue = defaultValue;
    }

    //required for serialised version
    public <K, V> GroupBy<K, V> mapValues(Object inputMap) {
        return mapValues((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapKeyedValue(Object inputMap, Object secondArgument) {
        return mapKeyedValue((GroupBy) inputMap, secondArgument);
    }

    public <K, R> GroupBy<K, R> mapValueWithKeyValue(Object inputMap, KeyValue secondArgument) {
        return mapValueWithKeyValue((GroupBy) inputMap, secondArgument);
    }

    public <K, V> GroupBy<K, V> biMapValuesWithParamMap(Object firstArgGroupBy, Object secondArgGroupBY) {
        return biMapValuesWithParamMap((GroupBy) firstArgGroupBy, (GroupBy) secondArgGroupBY);
    }

    public <K, V> GroupBy<K, V> mapKeys(Object inputMap) {
        return mapKeys((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapEntry(Object inputMap) {
        return mapEntry((GroupBy) inputMap);
    }

    public <K, V> GroupBy<K, V> mapValues(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.toMap().put(entry.getKey(), mapFunction.apply(entry.getValue()));
        });
        return outputCollection;
    }

    public <K, V> GroupBy<K, V> mapKeys(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.toMap().put(mapFunction.apply(entry.getKey()), entry.getValue());
        });
        return outputCollection;
    }

    public <K, V> GroupBy<K, V> mapEntry(GroupBy inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) mapFunction.apply(e);
            outputCollection.toMap().put(entry.getKey(), entry.getValue());
        });
        return outputCollection;
    }

    public <K, G extends GroupBy, R> GroupBy<K, R> mapKeyedValue(G inputMap, Object argumentProvider) {
        wrappedCollection.reset();
        Object key = mapFunction.apply(argumentProvider);
        Object item = inputMap.toMap().get(key);
        if (item != null) {
            KeyValue kv = new KeyValue(key, mapFrom2MapsBiFunction.apply(item, argumentProvider));
            outputCollection.fromMap(inputMap.toMap());
            outputCollection.add(kv);
            wrappedCollection.setGroupBy(outputCollection);
            wrappedCollection.setKeyValue(kv);
        }
        return wrappedCollection;
    }

    public <K, G extends GroupBy, R> GroupBy<K, R> mapValueWithKeyValue(G inputMap, KeyValue argumentProvider) {
        wrappedCollection.reset();
        Object key = argumentProvider.getKey();
        Object item = inputMap.toMap().get(key);
        if (item != null) {
            KeyValue kv = new KeyValue(key, mapFrom2MapsBiFunction.apply(item, argumentProvider.getValue()));
            outputCollection.fromMap(inputMap.toMap());
            outputCollection.add(kv);
            wrappedCollection.setGroupBy(outputCollection);
            wrappedCollection.setKeyValue(kv);
        }
        return wrappedCollection;
    }

    public <K, G extends GroupBy, H extends GroupBy, R> GroupBy<K, R> biMapValuesWithParamMap(G firstArgGroupBy, H secondArgGroupBY) {
        outputCollection.reset();
        Map arg2Map = (secondArgGroupBY == null && defaultValue != null) ? Collections.emptyMap() : secondArgGroupBY.toMap();
        firstArgGroupBy.toMap().forEach((key, arg1) -> {
            Object arg2 = arg2Map.getOrDefault(key, defaultValue);
            if (arg2 != null) {
                outputCollection.toMap().put(key, mapFrom2MapsBiFunction.apply(arg1, arg2));
            }
        });
        return outputCollection;
    }
}
