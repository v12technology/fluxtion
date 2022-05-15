package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.Stateful;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> Input type
 * @param <K> Key type from input T
 * @param <V> Value type from input T, input to aggregate function
 * @param <A> output type of aggregate calculation
 * @param <F> The aggregate function converts a V into an A
 */
public class GroupByCollection<T, K, V, A, F extends BaseSlidingWindowFunction<V, A, F>> implements GroupBy<K, A>, Stateful<A> {

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
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
        this.aggregateFunctionSupplier = aggregateFunctionSupplier;
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

    @Override
    public A reset() {
        map.clear();
        mapOfValues.clear();
        return null;
    }
}
