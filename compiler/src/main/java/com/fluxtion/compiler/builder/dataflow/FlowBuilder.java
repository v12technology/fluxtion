package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.function.AggregateFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.aggregate.function.TimedSlidingWindow;
import com.fluxtion.runtime.dataflow.aggregate.function.TumblingWindow;
import com.fluxtion.runtime.dataflow.function.BinaryMapFlowFunction.BinaryMapToRefFlowFunction;
import com.fluxtion.runtime.dataflow.function.FlatMapArrayFlowFunction;
import com.fluxtion.runtime.dataflow.function.FlatMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.LookupFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.runtime.dataflow.function.MergeFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;
import com.fluxtion.runtime.dataflow.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.runtime.dataflow.groupby.GroupByTimedSlidingWindow;
import com.fluxtion.runtime.dataflow.groupby.GroupByTumblingWindow;
import com.fluxtion.runtime.dataflow.helpers.Aggregates;
import com.fluxtion.runtime.dataflow.helpers.Collectors;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue;
import com.fluxtion.runtime.dataflow.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.dataflow.helpers.Mappers;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public <S, R> FlowBuilder<R> mapBiFunction(SerializableBiFunction<T, S, R> int2IntFunction,
                                               FlowBuilder<S> stream2Builder) {

        TriggeredFlowFunction<T> e1 = eventStream;
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

    public <R> FlowBuilder<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction) {
        return new FlowBuilder<>(new FlatMapArrayFlowFunction<>(eventStream, iterableFunction));
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

    public <V, K1, A, F extends AggregateFlowFunction<V, A, F>> GroupByFlowBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier) {
        MapFlowFunction<T, GroupBy<K1, A>, TriggeredFlowFunction<T>> x = new MapRef2RefFlowFunction<>(eventStream,
                new GroupByFlowFunctionWrapper<>(keyFunction, valueFunction, aggregateFunctionSupplier)::aggregate);
        return new GroupByFlowBuilder<>(x);
    }

    public <K1, A, F extends AggregateFlowFunction<T, A, F>> GroupByFlowBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction, SerializableSupplier<F> aggregateFunctionSupplier) {
        return groupBy(keyFunction, Mappers::identity, aggregateFunctionSupplier);
    }

    public <V, K1> GroupByFlowBuilder<K1, V> groupBy(
            SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Aggregates.identityFactory());
    }

    public <K> GroupByFlowBuilder<K, T> groupBy(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity);
    }

    public <K> GroupByFlowBuilder<K, List<T>> groupByToList(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity, Collectors.toList());
    }

    public <K> GroupByFlowBuilder<K, Set<T>> groupByToSet(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity, Collectors.toSet());
    }

    public <K> GroupByFlowBuilder<K, List<T>> groupByToList(
            SerializableFunction<T, K> keyFunction,
            int maxElementsInList) {
        return groupBy(keyFunction, Mappers::identity, Collectors.toList(maxElementsInList));
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


    /*
    TODO:
    ================
    co-group joining multiple aggregates into a single row/object

    Done:
    ================
    outer joins
    innerjoin
    groupBy - sliding window
    add peek to primitive streams
    stateful support for functions
    Use transient reference in any stream that has an instance function reference. Remove anchor
    add standard Binary and Map functions for primitives, sum, max, min, add, multiply etc.
    add standard predicates for primitives
    windowing sliding
    windowing tumbling
    De-dupe filter
    mapOnNotify
    id for eventStream
    flatmap
    groupBy
    groupBy - tumbling window
    More tests
    merge

    optional:
    ================
    add peek functions to support log and audit helpers
    zip - really just a stateful function
     */

}
