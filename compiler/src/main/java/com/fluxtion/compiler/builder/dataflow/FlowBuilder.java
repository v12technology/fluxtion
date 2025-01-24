/*
 * Copyright (c) 2025 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.aggregate.function.FixSizedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TimedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TumblingWindow;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToRefFlowFunction;
import com.fluxtion.runtime.dataflow.function.*;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.*;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Collectors;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.*;

public class FlowBuilder<T> extends AbstractFlowBuilder<T, FlowBuilder<T>> implements FlowDataSupplier<FlowSupplier<T>> {


    FlowBuilder(TriggeredFlowFunction<T> eventStream) {
        super(eventStream);
        EventProcessorBuilderService.service().add(eventStream);
    }

    @Override
    protected FlowBuilder<T> connect(TriggeredFlowFunction<T> stream) {
        return new FlowBuilder<>(stream);
    }


    @Override
    protected <R> FlowBuilder<R> connectMap(TriggeredFlowFunction<R> stream) {
        return new FlowBuilder<>(stream);
    }


    @Override
    protected FlowBuilder<T> identity() {
        return this;
    }

    public FlowSupplier<T> flowSupplier() {
        return eventStream;
    }

    public FlowBuilder<T> defaultValue(T defaultValue) {
        return map(new DefaultValue<>(defaultValue)::getOrDefault);
    }

    public FlowBuilder<T> defaultValue(SerializableSupplier<T> defaultValue) {
        return map(new DefaultValueFromSupplier<>(defaultValue)::getOrDefault);
    }

    public <R, I, L> FlowBuilder<R> lookup(SerializableFunction<T, I> lookupKeyFunction,
                                           SerializableFunction<I, L> lookupFunction,
                                           SerializableBiFunction<T, L, R> enrichFunction) {
        return new FlowBuilder<>(new LookupFlowFunction<>(eventStream, lookupKeyFunction, lookupFunction, enrichFunction));
    }

    //PROCESSING - START
    public <R> FlowBuilder<R> map(SerializableFunction<T, R> mapFunction) {
        return super.mapBase(mapFunction);
    }

    public FlowBuilder<Set<T>> mapToSet() {
        return map(Collectors.toSet());
    }

    public <R> FlowBuilder<Set<R>> mapToSet(SerializableFunction<T, R> mapFunction) {
        return map(mapFunction).map(Collectors.toSet());
    }

    public FlowBuilder<List<T>> mapToList() {
        return map(Collectors.toList());
    }

    public FlowBuilder<Collection<T>> mapToCollection() {
        return map(Collectors.toCollection());
    }

    public <R> FlowBuilder<List<R>> mapToList(SerializableFunction<T, R> mapFunction) {
        return map(mapFunction).map(Collectors.toList());
    }

    public FlowBuilder<List<T>> mapToList(int maxElements) {
        return map(Collectors.toList(maxElements));
    }

    public <R> FlowBuilder<List<R>> mapToList(SerializableFunction<T, R> mapFunction, int maxElements) {
        return map(mapFunction).map(Collectors.toList(maxElements));
    }

    public <S, R> FlowBuilder<R> mapBiFunction(SerializableBiFunction<T, S, R> int2IntFunction,
                                               FlowBuilder<S> stream2Builder) {
        return new FlowBuilder<>(
                new BinaryMapToRefFlowFunction<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public FlowBuilder<T> merge(FlowBuilder<? extends T> streamToMerge) {
        return new FlowBuilder<>(new MergeFlowFunction<>(eventStream, streamToMerge.eventStream));
    }

    @SuppressWarnings("unchecked")
    public FlowBuilder<T> merge(FlowBuilder<? extends T> streamToMerge, FlowBuilder<? extends T>... streamsToMerge) {
        List<FlowFunction<? extends T>> mergeList = new ArrayList<>();
        mergeList.add(eventStream);
        mergeList.add(streamToMerge.eventStream);
        for (FlowBuilder<? extends T> flowBuilder : streamsToMerge) {
            mergeList.add(flowBuilder.eventStream);
        }
        return new FlowBuilder<>(new MergeFlowFunction<>(mergeList));
    }

    public <R> FlowBuilder<R> flatMap(SerializableFunction<T, Iterable<R>> iterableFunction) {
        return new FlowBuilder<>(new FlatMapFlowFunction<>(eventStream, iterableFunction));
    }

    public <R> FlowBuilder<R> flatMap(SerializableFunction<T, Iterable<R>> iterableFunction, String flatMapCompleteSignal) {
        FlatMapFlowFunction<T, R, TriggeredFlowFunction<T>> flatMapIteratorFlowFunction = new FlatMapFlowFunction<>(eventStream, iterableFunction);
        flatMapIteratorFlowFunction.setFlatMapCompleteSignal(flatMapCompleteSignal);
        return new FlowBuilder<>(flatMapIteratorFlowFunction);
    }

    public <R> FlowBuilder<R> flatMapFromIterator(SerializableFunction<T, Iterator<R>> iterableFunction) {
        return new FlowBuilder<>(new FlatMapIteratorFlowFunction<>(eventStream, iterableFunction));
    }

    public <R> FlowBuilder<R> flatMapFromIterator(SerializableFunction<T, Iterator<R>> iterableFunction, String flatMapCompleteSignal) {
        FlatMapIteratorFlowFunction<T, R, TriggeredFlowFunction<T>> flatMapIteratorFlowFunction = new FlatMapIteratorFlowFunction<>(eventStream, iterableFunction);
        flatMapIteratorFlowFunction.setFlatMapCompleteSignal(flatMapCompleteSignal);
        return new FlowBuilder<>(flatMapIteratorFlowFunction);
    }

    public <R> FlowBuilder<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction) {
        return new FlowBuilder<>(new FlatMapArrayFlowFunction<>(eventStream, iterableFunction));
    }

    public <R> FlowBuilder<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction, String flatMapCompleteSignal) {
        FlatMapArrayFlowFunction<T, R, TriggeredFlowFunction<T>> flatMapIteratorFlowFunction = new FlatMapArrayFlowFunction<>(eventStream, iterableFunction);
        flatMapIteratorFlowFunction.setFlatMapCompleteSignal(flatMapCompleteSignal);
        return new FlowBuilder<>(flatMapIteratorFlowFunction);
    }

    public <R, F extends AggregateFlowFunction<T, R, F>> FlowBuilder<R>
    aggregate(SerializableSupplier<F> aggregateFunction) {
        return new FlowBuilder<>(new AggregateFlowFunctionWrapper<>(eventStream, aggregateFunction));
    }

    public <R, F extends AggregateFlowFunction<T, R, F>> FlowBuilder<R>
    tumblingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new FlowBuilder<>(
                new TumblingWindow<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <R, F extends AggregateFlowFunction<T, R, F>> FlowBuilder<R>
    slidingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int bucketsPerWindow) {
        return new FlowBuilder<>(
                new TimedSlidingWindow<>(eventStream, aggregateFunction, bucketSizeMillis, bucketsPerWindow));
    }

    public <R, F extends AggregateFlowFunction<T, R, F>> FlowBuilder<R>
    slidingAggregateByCount(SerializableSupplier<F> aggregateFunction, int elementsInWindow) {
        return new FlowBuilder<>(
                new FixSizedSlidingWindow<>(eventStream, aggregateFunction, elementsInWindow));
    }

    /**
     * Aggregates a flow using a key function to group by and an aggregating function to process new values for a keyed
     * bucket.
     *
     * @param keyFunction               The key function that groups and buckets incoming values
     * @param valueFunction             The value that is extracted from the incoming stream and applied to the aggregating function
     * @param aggregateFunctionSupplier A factory that supplies aggregating functions, each function has its own function instance
     * @param <V>                       Value type extracted from the incoming data flow
     * @param <K1>                      The type of the key used to group values
     * @param <A>                       The return type of the aggregating function
     * @param <F>                       The aggregating function type
     * @return A GroupByFlowBuilder for the aggregated flow
     */
    public <V, K1, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier) {
        MapFlowFunction<T, GroupBy<K1, A>, TriggeredFlowFunction<T>> x = new MapRef2RefFlowFunction<>(eventStream,
                new GroupByFlowFunctionWrapper<>(keyFunction, valueFunction, aggregateFunctionSupplier)::aggregate)
                .defaultValue(GroupBy.emptyCollection());
        return new GroupByFlowBuilder<>(x);
    }

    /**
     * Specialisation of groupBy where the value is the identity of the incoming data flow
     *
     * @param keyFunction               The key function that groups and buckets incoming values
     * @param aggregateFunctionSupplier A factory that supplies aggregating functions, each function has its own function instance
     * @param <K1>                      The type of the key used to group values
     * @param <A>                       The return type of the aggregating function
     * @param <F>                       The aggregating function type
     * @return A GroupByFlowBuilder for the aggregated flow
     * @see FlowBuilder#groupBy(SerializableFunction, SerializableFunction, SerializableSupplier)
     */
    public <K1, A, F extends AggregateFlowFunction<T, A, F>> GroupByFlowBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction, SerializableSupplier<F> aggregateFunctionSupplier) {
        return groupBy(keyFunction, Mappers::identity, aggregateFunctionSupplier);
    }

    /**
     * Specialisation of groupBy where the output of the groupBy is the last value received for a bucket. The value is
     * extracted using the value function
     *
     * @param keyFunction   The key function that groups and buckets incoming values
     * @param valueFunction The value that is extracted from the incoming stream and applied to the aggregating function
     * @param <V>           Value type extracted from the incoming data flow
     * @param <K1>          The type of the key used to group values
     * @return A GroupByFlowBuilder for the aggregated flow
     * @see FlowBuilder#groupBy(SerializableFunction, SerializableFunction, SerializableSupplier)
     */
    public <V, K1> GroupByFlowBuilder<K1, V> groupBy(
            SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Aggregates.identityFactory());
    }

    /**
     * Specialisation of groupBy where the output of the groupBy is the last value received for a bucket, where
     * the value is the identity of the incoming data flow
     *
     * @param keyFunction The key function that groups and buckets incoming values
     * @param <K>         The type of the key used to group values
     * @return A GroupByFlowBuilder for the aggregated flow
     */
    public <K> GroupByFlowBuilder<K, T> groupBy(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity);
    }

    /**
     * Creates a GroupByFlowBuilder using a compound key created by a set of method reference accessors to for the value.
     * The value is the last value supplied
     *
     * @param keyFunctions multi arg key accessors
     * @return GroupByFlowBuilder keyed on properties
     */
    @SafeVarargs
    public final GroupByFlowBuilder<GroupByKey<T>, T> groupByFields(
            SerializableFunction<T, ?>... keyFunctions) {
        return groupBy(GroupByKey.build(keyFunctions));
    }

    /**
     * Aggregates a flow using a key to group by and an aggregating function to process new values for a keyed
     * bucket. The key is a compound key created by a set of method reference accessors to for the value.
     *
     * @param aggregateFunctionSupplier A factory that supplies aggregating functions, each function has its own function instance
     * @param keyFunctions              multi arg key accessors
     * @param <A>                       The return type of the aggregating function
     * @param <F>                       The aggregating function type
     * @return A GroupByFlowBuilder for the aggregated flow
     * @see FlowBuilder#groupBy(SerializableFunction, SerializableFunction, SerializableSupplier)
     */
    @SafeVarargs
    public final <A, F extends AggregateFlowFunction<T, A, F>> GroupByFlowBuilder<GroupByKey<T>, A> groupByFieldsAggregate(
            SerializableSupplier<F> aggregateFunctionSupplier,
            SerializableFunction<T, ?>... keyFunctions) {
        return groupBy(GroupByKey.build(keyFunctions), aggregateFunctionSupplier);
    }

    /**
     * Creates a GroupByFlowBuilder using a compound key created by a set of method reference accessors to for the key
     * The value is extracted from the input using the value function
     *
     * @param valueFunction the value that will be stored in the groupBy
     * @param keyFunctions  multi arg key accessors
     * @return GroupByFlowBuilder keyed on properties
     */
    @SafeVarargs
    public final <V> GroupByFlowBuilder<GroupByKey<T>, V> groupByFieldsAndGet(
            SerializableFunction<T, V> valueFunction,
            SerializableFunction<T, ?>... keyFunctions) {
        return groupBy(GroupByKey.build(keyFunctions), valueFunction);
    }

    /**
     * Creates a GroupByFlowBuilder using a compound key created by a set of method reference accessors to for the key
     * The value is extracted from the input using the value function and is used as an input to the aggregating function
     *
     * @param valueFunction             the value that will be stored in the groupBy
     * @param aggregateFunctionSupplier A factory that supplies aggregating functions, each function has its own function instance
     * @param keyFunctions              multi arg key accessors
     * @param <V>                       Value type extracted from the incoming data flow
     * @param <A>                       The return type of the aggregating function
     * @param <F>                       The aggregating function type
     * @return A GroupByFlowBuilder for the aggregated flow
     * @see FlowBuilder#groupBy(SerializableFunction, SerializableFunction, SerializableSupplier)
     */
    @SafeVarargs
    public final <V, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<GroupByKey<T>, A> groupByFieldsGetAndAggregate(
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier,
            SerializableFunction<T, ?>... keyFunctions) {
        return groupBy(GroupByKey.build(keyFunctions), valueFunction, aggregateFunctionSupplier);
    }

    public <K> GroupByFlowBuilder<K, List<T>> groupByToList(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity, Collectors.listFactory());
    }

    /**
     * Aggregates a set of instances into a multimap style structure. The key is a compound key made up from the accessors
     * of the input data
     *
     * @param keyFunctions The accessors that make up the compound key
     * @return The GroupByFlowBuilder that represents the multimap
     */
    @SafeVarargs
    public final GroupByFlowBuilder<GroupByKey<T>, List<T>> groupByToList(SerializableFunction<T, ?>... keyFunctions) {
        return groupByFieldsAggregate(Collectors.listFactory(), keyFunctions);
    }


    public <K, V> GroupByFlowBuilder<K, List<V>> groupByToList(
            SerializableFunction<T, K> keyFunction, SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Collectors.listFactory());
    }

    public <K> GroupByFlowBuilder<K, Set<T>> groupByToSet(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity, Collectors.setFactory());
    }

    /**
     * Aggregates a set of instances into a multiset style structure. The key is a compound key made up from the accessors
     * of the input data
     *
     * @param keyFunctions The accessors that make up the compound key
     * @return The GroupByFlowBuilder that represents the multimap
     */
    @SafeVarargs
    public final GroupByFlowBuilder<GroupByKey<T>, Set<T>> groupByToSet(SerializableFunction<T, ?>... keyFunctions) {
        return groupByFieldsAggregate(Collectors.setFactory(), keyFunctions);
    }

    public <K, V> GroupByFlowBuilder<K, Set<V>> groupByToSet(SerializableFunction<T, K> keyFunction, SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Collectors.setFactory());
    }

    public <K> GroupByFlowBuilder<K, List<T>> groupByToList(
            SerializableFunction<T, K> keyFunction,
            int maxElementsInList) {
        return groupBy(keyFunction, Mappers::identity, Collectors.listFactory(maxElementsInList));
    }

    public <V, K, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<K, A>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    SerializableSupplier<F> aggregateFunctionSupplier,
                    int bucketSizeMillis) {
        return new GroupByFlowBuilder<>(new GroupByTumblingWindow<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis
        ));
    }

    public <V, K> GroupByFlowBuilder<K, V>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    int bucketSizeMillis) {
        return groupByTumbling(keyFunction, valueFunction, Aggregates.identityFactory(), bucketSizeMillis);
    }

    public <V, K, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<K, A>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new GroupByFlowBuilder<>(new GroupByTimedSlidingWindow<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <V, K> GroupByFlowBuilder<K, V>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return groupBySliding(keyFunction, valueFunction, Aggregates.identityFactory(), bucketSizeMillis, numberOfBuckets);
    }

    public <K, A, F extends AggregateFlowFunction<T, A, F>> GroupByFlowBuilder<K, A>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new GroupByFlowBuilder<>(new GroupByTimedSlidingWindow<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                Mappers::identity,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <I, Z extends FlowBuilder<I>> Z mapOnNotify(I target) {
        return super.mapOnNotifyBase(target);
    }

    public StaticEventProcessor build() {
        List<Object> nodeList = GenerationContext.SINGLETON.getNodeList();
        EventProcessor<?> eventProcessor = Fluxtion.interpret(c -> {
            for (Object node : nodeList) {
                c.addNode(node);
            }
        });
        eventProcessor.init();
        return eventProcessor;
    }


    /*
    Done:
    ================

    optional:
    ================
    add peek functions to support log and audit helpers
    zip - really just a stateful function
     */

}
