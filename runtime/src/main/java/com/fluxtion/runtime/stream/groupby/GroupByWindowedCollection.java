package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.Stateful;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @param <T> Input type
 * @param <K> Key type from input T
 * @param <V> Value type from input T, input to aggregate function
 * @param <A> output type of aggregate calculation
 * @param <F> The aggregate function converts a V into an A
 */
public class GroupByWindowedCollection<T, K, V, A, F extends AggregateFunction<V, A, F>>
        implements AggregateFunction<T, GroupByStreamed<K, A>, GroupByWindowedCollection<T, K, V, A, F>>,
        GroupByStreamed<K, A>, Stateful<GroupByStreamed<K, A>> {

    private final SerializableFunction<T, K> keyFunction;
    private final SerializableFunction<T, V> valueFunction;
    private final SerializableSupplier<F> aggregateFunctionSupplier;
    private transient final Map<K, F> mapOfFunctions;
    private transient final Map<K, A> mapOfValues;
    private transient final Map<K, LongAdder> keyCount;
    private F latestAggregateValue;
    private KeyValue<K, A> keyValue;

    public GroupByWindowedCollection(
            @AssignToField("keyFunction")
            SerializableFunction<T, K> keyFunction,
            @AssignToField("valueFunction")
            SerializableFunction<T, V> valueFunction,
            @AssignToField("aggregateFunctionSupplier")
            SerializableSupplier<F> aggregateFunctionSupplier) {
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
        this.aggregateFunctionSupplier = aggregateFunctionSupplier;
        this.mapOfFunctions = new HashMap<>();
        this.mapOfValues = new HashMap<>();
        this.keyCount = new HashMap<>();
    }

    @Override
    public GroupByStreamed<K, A> get() {
        return this;
    }

    @Override
    public void combine(GroupByWindowedCollection<T, K, V, A, F> add) {
        //merge each if existing
        add.mapOfFunctions.forEach((k, f) -> {
            F targetFunction = mapOfFunctions.computeIfAbsent(k, key -> aggregateFunctionSupplier.get());
            keyCount.computeIfAbsent(k, key -> new LongAdder()).increment();
            targetFunction.combine(f);
            mapOfValues.put(k, targetFunction.get());
        });
    }

    @Override
    public void deduct(GroupByWindowedCollection<T, K, V, A, F> add) {
        //ignore if
        add.mapOfFunctions.forEach((k, f) -> {
            LongAdder currentCount = keyCount.computeIfAbsent(k, key -> new LongAdder());
            currentCount.decrement();
            if (currentCount.intValue() < 1) {
                currentCount.reset();
                //remove completely
                mapOfFunctions.remove(k);
                mapOfValues.remove(k);
            } else {
                //perform deduct
                F targetFunction = mapOfFunctions.get(k);
                targetFunction.deduct(f);
                mapOfValues.put(k, targetFunction.get());
            }
        });
    }

    public GroupByStreamed<K, A> aggregate(T input) {
        K key = keyFunction.apply(input);
        V value = valueFunction.apply(input);
        F currentFunction = mapOfFunctions.get(key);
        if (currentFunction == null) {
            currentFunction = aggregateFunctionSupplier.get();
            mapOfFunctions.put(key, currentFunction);
            keyCount.computeIfAbsent(key, k -> new LongAdder()).increment();
        }
        currentFunction.aggregate(value);
        latestAggregateValue = currentFunction;
        mapOfValues.put(key, latestAggregateValue.get());
        keyValue = new KeyValue<>(key, latestAggregateValue.get());
        return this;
    }

    @Override
    public KeyValue<K, A> lastKeyValue() {
        return keyValue;
    }

    @Override
    public Map<K, A> toMap() {
        return mapOfValues;
    }

    @Override
    public A lastValue() {
        return latestAggregateValue.get();
    }

    @Override
    public Collection<A> values() {
        return toMap().values();
    }

    @Override
    public GroupByStreamed<K, A> reset() {
        mapOfFunctions.clear();
        mapOfValues.clear();
        keyValue = null;
        return this;
    }

    @Override
    public String toString() {
        return "GroupByWindowedCollection{" +
                "mapOfValues=" + mapOfValues +
                '}';
    }
}
