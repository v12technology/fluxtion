package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.EventProcessorConfigService;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.*;
import com.fluxtion.runtime.stream.EventStream.EventSupplier;
import com.fluxtion.runtime.stream.aggregate.AggregateFunction;
import com.fluxtion.runtime.stream.aggregate.AggregateStream;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.aggregate.TumblingWindowStream;
import com.fluxtion.runtime.stream.groupby.*;
import com.fluxtion.runtime.stream.helpers.Aggregates;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.DefaultValue.DefaultValueFromSupplier;
import com.fluxtion.runtime.stream.helpers.Mappers;
import com.fluxtion.runtime.stream.helpers.Peekers;

public class EventStreamBuilder<T> {

    final TriggeredEventStream<T> eventStream;

    EventStreamBuilder(TriggeredEventStream<T> eventStream) {
        EventProcessorConfigService.service().add(eventStream);
        this.eventStream = eventStream;
    }

    public EventSupplier<T> eventStream() {
        return EventProcessorConfigService.service().add(new WrappingEventSupplier<>(eventStream));
    }

    //TRIGGERS - START
    public EventStreamBuilder<T> updateTrigger(Object updateTrigger) {
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public EventStreamBuilder<T> publishTrigger(Object publishTrigger) {
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public EventStreamBuilder<T> publishTriggerOverride(Object publishTrigger) {
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public EventStreamBuilder<T> resetTrigger(Object resetTrigger) {
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public EventStreamBuilder<T> filter(SerializableFunction<T, Boolean> filterFunction) {
        return new EventStreamBuilder<>(new FilterEventStream<>(eventStream, filterFunction));
    }

    public <P> EventStreamBuilder<T> filterByProperty(SerializableFunction<T, P> accessor, SerializableFunction<P, Boolean> filterFunction) {
        return new EventStreamBuilder<>(new FilterByPropertyEventStream<>(eventStream, accessor, filterFunction));
    }

    public <S> EventStreamBuilder<T> filter(
            SerializableBiFunction<T, S, Boolean> predicate,
            EventStreamBuilder<S> secondArgument) {
        return new EventStreamBuilder<>(
                new FilterDynamicEventStream<>(eventStream, secondArgument.eventStream, predicate));
    }

    public <P, S> EventStreamBuilder<T> filterByProperty(
            SerializableBiFunction<P, S, Boolean> predicate,
            SerializableFunction<T, P> accessor,
            EventStreamBuilder<S> secondArgument) {
        return new EventStreamBuilder<>(
                new FilterByPropertyDynamicEventStream<>(eventStream, accessor, secondArgument.eventStream, predicate));
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
        return new EventStreamBuilder<>(new MapEventStream.MapRef2RefEventStream<>(eventStream, mapFunction));
    }

    public <S, R> EventStreamBuilder<R> mapBiFunction(SerializableBiFunction<T, S, R> int2IntFunction,
                                                      EventStreamBuilder<S> stream2Builder) {
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

    public <V, K, A, F extends AggregateFunction<V, A, F>> EventStreamBuilder<GroupByStreamed<K, A>>
    groupBy(SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction,
            SerializableSupplier<F> aggregateFunctionSupplier) {
        return map(new GroupByWindowedCollection<>(keyFunction, valueFunction, aggregateFunctionSupplier)::aggregate);
    }

    public <V, K> EventStreamBuilder<GroupByStreamed<K, V>>
    groupBy(SerializableFunction<T, K> keyFunction,
            SerializableFunction<T, V> valueFunction) {
        return groupBy(keyFunction, valueFunction, Aggregates.identity());
    }

    public <V, K, A, F extends AggregateFunction<V, A, F>> EventStreamBuilder<GroupBy<K, A>>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    SerializableSupplier<F> aggregateFunctionSupplier,
                    int bucketSizeMillis) {
        return new EventStreamBuilder<>(new TumblingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis
        ));
    }

    public <V, K> EventStreamBuilder<GroupBy<K, V>>
    groupByTumbling(SerializableFunction<T, K> keyFunction,
                    SerializableFunction<T, V> valueFunction,
                    int bucketSizeMillis) {
        return groupByTumbling(keyFunction, valueFunction, Aggregates.identity(), bucketSizeMillis);
    }

    public <V, K, A, F extends AggregateFunction<V, A, F>> EventStreamBuilder<GroupBy<K, A>>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new EventStreamBuilder<>(new SlidingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                valueFunction,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <V, K> EventStreamBuilder<GroupBy<K, V>>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableFunction<T, V> valueFunction,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return groupBySliding(keyFunction, valueFunction, Aggregates.identity(), bucketSizeMillis, numberOfBuckets);
    }

    public <K, A, F extends AggregateFunction<T, A, F>> EventStreamBuilder<GroupBy<K, A>>
    groupBySliding(SerializableFunction<T, K> keyFunction,
                   SerializableSupplier<F> aggregateFunctionSupplier,
                   int bucketSizeMillis,
                   int numberOfBuckets) {
        return new EventStreamBuilder<>(new SlidingGroupByWindowStream<>(
                eventStream,
                aggregateFunctionSupplier,
                keyFunction,
                Mappers::valueIdentity,
                bucketSizeMillis,
                numberOfBuckets
        ));
    }

    public <R> EventStreamBuilder<R> mapOnNotify(R target) {
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public IntStreamBuilder mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntStreamBuilder(new MapEventStream.MapRef2ToIntEventStream<>(eventStream, mapFunction));
    }

    public DoubleStreamBuilder mapToDouble(LambdaReflection.SerializableToDoubleFunction<T> mapFunction) {
        return new DoubleStreamBuilder(new MapEventStream.MapRef2ToDoubleEventStream<>(eventStream, mapFunction));
    }

    public LongStreamBuilder mapToLong(LambdaReflection.SerializableToLongFunction<T> mapFunction) {
        return new LongStreamBuilder(new MapEventStream.MapRef2ToLongEventStream<>(eventStream, mapFunction));
    }

    //OUTPUTS - START
    public EventStreamBuilder<T> push(SerializableConsumer<T> pushFunction) {
//        EventProcessorConfigService.service().add(pushFunction.captured()[0]);
        return new EventStreamBuilder<>(new PushEventStream<>(eventStream, pushFunction));
    }

    public EventStreamBuilder<T> sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publish);
    }

    public EventStreamBuilder<T> notify(Object target) {
        EventProcessorConfigService.service().add(target);
        return new EventStreamBuilder<>(new NotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<T> processAsNewGraphEvent() {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, new InternalEventDispatcher()::dispatchToGraph));
    }

    public EventStreamBuilder<T> peek(SerializableConsumer<T> peekFunction) {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, peekFunction));
    }

    public EventStreamBuilder<T> console(String in) {
        return peek(Peekers.console(in));
    }

    public EventStreamBuilder<T> console() {
        return console("{}");
    }

    //META-DATA
    public EventStreamBuilder<T> id(String nodeId) {
        EventProcessorConfigService.service().add(eventStream, nodeId);
        return this;
    }

    /*
    TODO:
    ================
    join
    co-group joining multiple aggregates into a single row/object

    Done:
    ================
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
