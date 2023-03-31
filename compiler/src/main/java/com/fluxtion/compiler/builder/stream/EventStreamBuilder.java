package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.AggregateFunction;
import com.fluxtion.runtime.stream.FlowSupplier;
import com.fluxtion.runtime.stream.GroupByStreamed;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.AggregateStream;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.aggregate.TumblingWindowStream;
import com.fluxtion.runtime.stream.groupby.GroupByWindowedCollection;
import com.fluxtion.runtime.stream.groupby.SlidingGroupByWindowStream;
import com.fluxtion.runtime.stream.groupby.TumblingGroupByWindowStream;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.Collectors;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.stream.impl.BinaryMapEventStream;
import com.fluxtion.runtime.stream.impl.FlatMapArrayEventStream;
import com.fluxtion.runtime.stream.impl.FlatMapEventStream;
import com.fluxtion.runtime.stream.impl.LookupEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream.MapRef2RefEventStream;
import com.fluxtion.runtime.stream.impl.MergeEventStream;
import com.fluxtion.runtime.stream.impl.WrappingEventSupplier;

import java.util.List;

public class EventStreamBuilder<T> extends AbstractEventStreamBuilder<T, EventStreamBuilder<T>> implements EventSupplierAccessor<FlowSupplier<T>> {


    EventStreamBuilder(TriggeredEventStream<T> eventStream) {
        super(eventStream);
        EventProcessorBuilderService.service().add(eventStream);
    }

    @Override
    protected EventStreamBuilder<T> connect(TriggeredEventStream<T> stream) {
        return new EventStreamBuilder<>(stream);
    }


    @Override
    protected <R> EventStreamBuilder<R> connectMap(TriggeredEventStream<R> stream) {
        return new EventStreamBuilder<>(stream);
    }


    @Override
    protected EventStreamBuilder<T> identity() {
        return this;
    }

    public FlowSupplier<T> runtimeSupplier() {
        return EventProcessorBuilderService.service().add(new WrappingEventSupplier<>(eventStream));
    }

    public EventStreamBuilder<T> defaultValue(T defaultValue) {
        return map(new DefaultValue<>(defaultValue)::getOrDefault);
    }

    public EventStreamBuilder<T> defaultValue(SerializableSupplier<T> defaultValue) {
        return map(new DefaultValueFromSupplier<>(defaultValue)::getOrDefault);
    }

    public <R, I, L> EventStreamBuilder<R> lookup(SerializableFunction<T, I> lookupKeyFunction,
                                                  SerializableFunction<I, L> lookupFunction,
                                                  SerializableBiFunction<T, L, R> enrichFunction) {
        return new EventStreamBuilder<>(new LookupEventStream<>(eventStream, lookupKeyFunction, lookupFunction, enrichFunction));
    }

    //PROCESSING - START
    public <R> EventStreamBuilder<R> map(SerializableFunction<T, R> mapFunction) {
        return super.mapBase(mapFunction);
    }

    public <S, R> EventStreamBuilder<R> mapBiFunction(SerializableBiFunction<T, S, R> int2IntFunction,
                                                      EventStreamBuilder<S> stream2Builder) {

        TriggeredEventStream<T> e1 = eventStream;
        return new EventStreamBuilder<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public EventStreamBuilder<T> merge(EventStreamBuilder<? extends T> streamToMerge) {
        return new EventStreamBuilder<>(new MergeEventStream<>(eventStream, streamToMerge.eventStream));
    }

    public <S, R> EventStreamBuilder<R> flatMap(SerializableFunction<T, Iterable<R>> iterableFunction) {
        return new EventStreamBuilder<>(new FlatMapEventStream<>(eventStream, iterableFunction));
    }

    public <S, R> EventStreamBuilder<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction) {
        return new EventStreamBuilder<>(new FlatMapArrayEventStream<>(eventStream, iterableFunction));
    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilder<R>
    aggregate(SerializableSupplier<F> aggregateFunction) {
        return new EventStreamBuilder<>(new AggregateStream<>(eventStream, aggregateFunction));
    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilder<R>
    tumblingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new EventStreamBuilder<>(
                new TumblingWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilder<R>
    slidingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int bucketsPerWindow) {
        return new EventStreamBuilder<>(
                new TimedSlidingWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis, bucketsPerWindow));
    }

    public <V, K1, A, F extends AggregateFunction<V, A, F>> GroupByStreamBuilder<K1, A>
    groupBy(SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier) {
        MapEventStream<T, GroupByStreamed<K1, A>, TriggeredEventStream<T>> x = new MapRef2RefEventStream<>(eventStream,
                new GroupByWindowedCollection<>(keyFunction, valueFunction, aggregateFunctionSupplier)::aggregate);
        return new GroupByStreamBuilder<>(x);
    }

    public <V, K1> GroupByStreamBuilder<K1, V>
    groupBy(SerializableFunction<T, K1> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Aggregates.identityFactory());
    }

    public <K> GroupByStreamBuilder<K, T>
    groupBy(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity);
    }

    public <V, K> GroupByStreamBuilder<K, List<T>>
    groupByAsList(SerializableFunction<T, K> keyFunction) {
        return groupBy(keyFunction, Mappers::identity, Collectors.toList());
    }

    public <V, K> GroupByStreamBuilder<K, List<T>>
    groupByAsList(SerializableFunction<T, K> keyFunction, int maxElementsInList) {
        return groupBy(keyFunction, Mappers::identity, Collectors.toList(maxElementsInList));
    }

    public <V, K, A, F extends AggregateFunction<V, A, F>> GroupByStreamBuilder<K, A>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    SerializableSupplier<F> aggregateFunctionSupplier,
                    int bucketSizeMillis) {
        return new GroupByStreamBuilder<>(new TumblingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis
        ));
    }

    public <V, K> GroupByStreamBuilder<K, V>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    int bucketSizeMillis) {
        return groupByTumbling(keyFunction, valueFunction, Aggregates.identityFactory(), bucketSizeMillis);
    }

    public <V, K, A, F extends AggregateFunction<V, A, F>> GroupByStreamBuilder<K, A>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new GroupByStreamBuilder<>(new SlidingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <V, K> GroupByStreamBuilder<K, V>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return groupBySliding(keyFunction, valueFunction, Aggregates.identityFactory(), bucketSizeMillis, numberOfBuckets);
    }

    public <K, A, F extends AggregateFunction<T, A, F>> GroupByStreamBuilder<K, A>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new GroupByStreamBuilder<>(new SlidingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                Mappers::identity,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <I, Z extends EventStreamBuilder<I>> Z mapOnNotify(I target) {
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
