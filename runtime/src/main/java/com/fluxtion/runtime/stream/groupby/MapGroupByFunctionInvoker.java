package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.GroupByStreamed.KeyValue;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public class MapGroupByFunctionInvoker {

    private final SerializableFunction mapFunction;
    private final SerializableBiFunction mapFrom2MapsBiFunction;
    @SepNode
    public Object defaultValue;

    private final transient GroupByCollection outputCollection = new GroupByCollection();
    private final transient WrappingGroupByStreamed wrappedCollection = new WrappingGroupByStreamed();

    public <T, R> MapGroupByFunctionInvoker(SerializableFunction<T, R> mapFunction) {
        this(mapFunction, null);
    }

    public <A, B, R> MapGroupByFunctionInvoker(SerializableBiFunction<A, B, R> mapFrom2MapsBiFunction) {
        this(null, mapFrom2MapsBiFunction);
    }

    public <K, V, A, O> MapGroupByFunctionInvoker(SerializableFunction<A, K> mapFunction, SerializableBiFunction<V, A, O> mapFrom2MapsBiFunction) {
        this.mapFunction = mapFunction;
        this.mapFrom2MapsBiFunction = mapFrom2MapsBiFunction;
    }

    public <K, V, A, O> MapGroupByFunctionInvoker(SerializableFunction<A, K> mapFunction, SerializableBiFunction<V, A, O> mapFrom2MapsBiFunction, Object defaultValue) {
        this.mapFunction = mapFunction;
        this.mapFrom2MapsBiFunction = mapFrom2MapsBiFunction;
        this.defaultValue = defaultValue;
    }

    //required for serialised version
    public <K, V> GroupByStreamed<K, V> mapValues(Object inputMap) {
        return mapValues((GroupByStreamed) inputMap);
    }

    public <K, V> GroupByStreamed<K, V> mapKeyedValue(Object inputMap, Object secondArgument) {
        return mapKeyedValue((GroupByStreamed) inputMap, secondArgument);
    }

    public <K, R> GroupByStreamed<K, R> mapValueWithKeyValue(Object inputMap, KeyValue secondArgument) {
        return mapValueWithKeyValue((GroupByStreamed) inputMap, secondArgument);
    }

    public <K, V> GroupByStreamed<K, V> biMapValuesWithParamMap(Object firstArgGroupBy, Object secondArgGroupBY) {
        return biMapValuesWithParamMap((GroupByStreamed) firstArgGroupBy, (GroupByStreamed) secondArgGroupBY);
    }

    public <K, V> GroupByStreamed<K, V> mapKeys(Object inputMap) {
        return mapKeys((GroupByStreamed) inputMap);
    }

    public <K, V> GroupByStreamed<K, V> mapEntry(Object inputMap) {
        return mapEntry((GroupByStreamed) inputMap);
    }

    public <K, V> GroupByStreamed<K, V> mapValues(GroupByStreamed inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.toMap().put(entry.getKey(), mapFunction.apply(entry.getValue()));
        });
        return outputCollection;
    }

    public <K, V> GroupByStreamed<K, V> mapKeys(GroupByStreamed inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) e;
            outputCollection.toMap().put(mapFunction.apply(entry.getKey()), entry.getValue());
        });
        return outputCollection;
    }

    public <K, V> GroupByStreamed<K, V> mapEntry(GroupByStreamed inputMap) {
        outputCollection.reset();
        inputMap.toMap().entrySet().forEach(e -> {
            Map.Entry entry = (Entry) mapFunction.apply(e);
            outputCollection.toMap().put(entry.getKey(), entry.getValue());
        });
        return outputCollection;
    }

    public <K, G extends GroupByStreamed, R> GroupByStreamed<K, R> mapKeyedValue(G inputMap, Object argumentProvider) {
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

    public <K, G extends GroupByStreamed, R> GroupByStreamed<K, R> mapValueWithKeyValue(G inputMap, KeyValue argumentProvider) {
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

    public <K, G extends GroupByStreamed, H extends GroupByStreamed, R> GroupByStreamed<K, R> biMapValuesWithParamMap(G firstArgGroupBy, H secondArgGroupBY) {
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
