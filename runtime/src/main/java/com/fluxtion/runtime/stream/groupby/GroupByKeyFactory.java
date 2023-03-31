package com.fluxtion.runtime.stream.groupby;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateIdentity;
import com.fluxtion.runtime.stream.aggregate.functions.AggregateToList;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Collectors;
import com.fluxtion.runtime.stream.helpers.Mappers;

import java.util.List;

public class GroupByKeyFactory<T, K> {
    private final SerializableFunction<T, K> keyFunction;

    public GroupByKeyFactory(SerializableFunction<T, K> keyFunction) {
        this.keyFunction = keyFunction;
    }

    public SerializableFunction<T, K> getKeyFunction() {
        return keyFunction;
    }

    public GroupByWindowedCollection<T, K, T, List<T>, AggregateToList<T>> groupByToListFactory() {
        return new GroupByWindowedCollection<>(keyFunction, Mappers::identity, Collectors.toList());
    }

    public GroupByWindowedCollection<T, K, T, T, AggregateIdentity<T>> groupByFactory() {
        return new GroupByWindowedCollection<>(keyFunction, Mappers::identity, Aggregates.identityFactory());
    }


}
