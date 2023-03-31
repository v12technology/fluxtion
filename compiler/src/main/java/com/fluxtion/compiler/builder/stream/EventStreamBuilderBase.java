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
import com.fluxtion.runtime.stream.impl.MapEventStream;
import com.fluxtion.runtime.stream.impl.MapEventStream.MapRef2RefEventStream;
import com.fluxtion.runtime.stream.impl.MergeEventStream;
import com.fluxtion.runtime.stream.impl.WrappingEventSupplier;

import java.util.List;

public class EventStreamBuilderBase<T> extends AbstractEventStreamBuilder<T, EventStreamBuilderBase<T>> implements EventSupplierAccessor<FlowSupplier<T>> {


    EventStreamBuilderBase(TriggeredEventStream<T> eventStream) {
        super(eventStream);
        EventProcessorBuilderService.service().add(eventStream);
    }

    @Override
    protected EventStreamBuilderBase<T> connect(TriggeredEventStream<T> stream) {
        return new EventStreamBuilderBase<>(stream);
    }


    @Override
    protected <R> EventStreamBuilderBase<R> connectMap(TriggeredEventStream<R> stream) {
        return new EventStreamBuilderBase<>(stream);
    }


    @Override
    protected EventStreamBuilderBase<T> identity() {
        return this;
    }

    public FlowSupplier<T> runtimeSupplier() {
        return EventProcessorBuilderService.service().add(new WrappingEventSupplier<>(eventStream));
    }

    public EventStreamBuilderBase<T> defaultValue(T defaultValue) {
        return map(new DefaultValue<>(defaultValue)::getOrDefault);
    }

    public EventStreamBuilderBase<T> defaultValue(SerializableSupplier<T> defaultValue) {
        return map(new DefaultValueFromSupplier<>(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public <R> EventStreamBuilderBase<R> map(SerializableFunction<T, R> mapFunction) {
        return super.mapBase(mapFunction);
    }

    public <S, R> EventStreamBuilderBase<R> mapBiFunction(SerializableBiFunction<T, S, R> int2IntFunction,
                                                          EventStreamBuilderBase<S> stream2Builder) {

        TriggeredEventStream<T> e1 = eventStream;
        return new EventStreamBuilderBase<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public EventStreamBuilderBase<T> merge(EventStreamBuilderBase<? extends T> streamToMerge) {
        return new EventStreamBuilderBase<>(new MergeEventStream<>(eventStream, streamToMerge.eventStream));
    }

//    public <S, R> EventStreamBuilderBase<R> flatMap(SerializableFunction<T, Iterable<R>> iterableFunction) {
//        return new EventStreamBuilderBase<>(new FlatMapEventStream<>(eventStream, iterableFunction));
//    }
//
//    public <S, R> EventStreamBuilderBase<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction) {
//        return new EventStreamBuilderBase<>(new FlatMapArrayEventStream<>(eventStream, iterableFunction));
//    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilderBase<R>
    aggregate(SerializableSupplier<F> aggregateFunction) {
        return new EventStreamBuilderBase<>(new AggregateStream<>(eventStream, aggregateFunction));
    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilderBase<R>
    tumblingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis) {
        return new EventStreamBuilderBase<>(
                new TumblingWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis));
    }

    public <S, R, F extends AggregateFunction<T, R, F>> EventStreamBuilderBase<R>
    slidingAggregate(SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int bucketsPerWindow) {
        return new EventStreamBuilderBase<>(
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

    public <I, Z extends EventStreamBuilderBase<I>> Z mapOnNotify(I target) {
        return super.mapOnNotifyBase(target);
    }


}
