package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> Input type
 * @param <K> Key type
 * @param <A> Value type output of valueFunction
 */
public class GroupByCollection<T, V, K, A, F extends BaseSlidingWindowFunction<V, A, F>> implements GroupBy<K, A> {

    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final SerializableSupplier<F> aggregateFunctionSupplier;
    private transient final Map<K, F> map;
    private transient final Map<K, A> mapOfValues;
    private F latestAggregateValue;
    private KeyValue<K, A> keyValue;

    public GroupByCollection(SerializableFunction<T, K> keyFunction,
                             SerializableFunction<T, V> valueFunction,
                             SerializableSupplier<F> aggregateFunctionSupplier) {
        this.valueFunction = valueFunction;
        this.aggregateFunctionSupplier = aggregateFunctionSupplier;
        this.keyFunction = keyFunction;
        this.map = new HashMap<>();
        mapOfValues = new HashMap<>();
    }

    public GroupBy<K, A> aggregate(T input) {
        K key = keyFunction.apply(input);
        V value = valueFunction.apply(input);
        F currentFunction = map.get(key);
        if (currentFunction == null) {
            currentFunction = aggregateFunctionSupplier.get();
            map.put(key, currentFunction);
        }
        currentFunction.aggregate(value);
        latestAggregateValue = currentFunction;
        mapOfValues.put(key, latestAggregateValue.get());
        keyValue = new KeyValue<>(key, latestAggregateValue.get());
        return this;
    }

    @Override
    public KeyValue<K, A> keyValue(){
        return keyValue;
    }

    @Override
    public Map<K, A> map() {
        return mapOfValues;
    }

    @Override
    public A value() {
        return latestAggregateValue.get();
    }

    @Override
    public Collection<A> values() {
        return map().values();
    }
}
