package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.SepContext;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.BinaryMapEventStream;
import com.fluxtion.runtime.stream.InternalEventDispatcher;
import com.fluxtion.runtime.stream.FilterEventStream;
import com.fluxtion.runtime.stream.FlatMapArrayEventStream;
import com.fluxtion.runtime.stream.FlatMapEventStream;
import com.fluxtion.runtime.stream.LookupEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.stream.MapOnNotifyEventStream;
import com.fluxtion.runtime.stream.NotifyEventStream;
import com.fluxtion.runtime.stream.PeekEventStream;
import com.fluxtion.runtime.stream.PushEventStream;
import com.fluxtion.runtime.stream.TriggeredEventStream;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;
import com.fluxtion.runtime.stream.aggregate.TimedSlidingWindowStream;
import com.fluxtion.runtime.stream.helpers.DefaultValue;
import com.fluxtion.runtime.stream.helpers.Peekers;
import com.fluxtion.runtime.time.FixedRateTrigger;

import java.util.function.Supplier;

public class EventStreamBuilder<T> {

    final TriggeredEventStream<T> eventStream;

    EventStreamBuilder(TriggeredEventStream<T> eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    //TRIGGERS - START
    public EventStreamBuilder<T> updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerNode(StreamHelper.getSource(updateTrigger));
        return this;
    }

    public EventStreamBuilder<T> publishTrigger(Object publishTrigger){
        eventStream.setPublishTriggerNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public EventStreamBuilder<T> publishTriggerOverride(Object publishTrigger){
        eventStream.setPublishTriggerOverrideNode(StreamHelper.getSource(publishTrigger));
        return this;
    }

    public EventStreamBuilder<T> resetTrigger(Object resetTrigger){
        eventStream.setResetTriggerNode(StreamHelper.getSource(resetTrigger));
        return this;
    }

    public EventStreamBuilder<T> filter( SerializableFunction<T, Boolean> filterFunction){
        return new EventStreamBuilder<>( new FilterEventStream<>(eventStream, filterFunction));
    }

    public EventStreamBuilder<T> defaultValue(T defaultValue){
        return map(new DefaultValue<>(defaultValue)::getOrDefault);
    }

    public <R, I, L> EventStreamBuilder<R> lookup(SerializableFunction<I, L> lookupFunction,
                                                  SerializableFunction<T, I> lookupKeyFunction,
                                                  SerializableBiFunction<T, L, R> enrichFunction){
        return new EventStreamBuilder<>( new LookupEventStream<>(eventStream, lookupKeyFunction, lookupFunction, enrichFunction));
    }

    //PROCESSING - START
    public <R> EventStreamBuilder<R> map(SerializableFunction<T, R> mapFunction) {
        return new EventStreamBuilder<>( new MapEventStream.MapRef2RefEventStream<>(eventStream, mapFunction));
    }

    public <S, R> EventStreamBuilder<R> map(SerializableBiFunction<T, S, R> int2IntFunction, EventStreamBuilder<S> stream2Builder) {
        return new EventStreamBuilder<>(
                new BinaryMapEventStream.BinaryMapToRefEventStream<>(
                        eventStream, stream2Builder.eventStream, int2IntFunction)
        );
    }

    public <S, R> EventStreamBuilder<R> flatMap(SerializableFunction<T, Iterable<R>> iterableFunction){
        return new EventStreamBuilder<>(new FlatMapEventStream<>(eventStream, iterableFunction));
    }

    public <S, R> EventStreamBuilder<R> flatMapFromArray(SerializableFunction<T, R[]> iterableFunction){
        return new EventStreamBuilder<>(new FlatMapArrayEventStream<>(eventStream, iterableFunction));
    }

    public <S, R, F extends BaseSlidingWindowFunction<T, R, F>> EventStreamBuilder<R> slidingMap(
            SerializableSupplier<F> aggregateFunction, int bucketSizeMillis, int bucketsPerWindow){
        return new EventStreamBuilder<>(
                new TimedSlidingWindowStream<>(eventStream, aggregateFunction, bucketSizeMillis, bucketsPerWindow));
    }

    //todo change to use slidingMap with bucket of 1
    public <R> EventStreamBuilder<R> tumblingMap(SerializableFunction<T, R> mapFunction, int bucketSizeMillis) {
        EventStreamBuilder<R> stream = new EventStreamBuilder<>(
                new MapEventStream.MapRef2RefEventStream<>(eventStream, mapFunction));
        FixedRateTrigger trigger = new FixedRateTrigger(bucketSizeMillis);
        stream.publishTriggerOverride(trigger);
        return stream;
    }

    public <R> EventStreamBuilder<R> mapOnNotify(R target){
        return new EventStreamBuilder<>(new MapOnNotifyEventStream<>(eventStream, target));
    }

    public IntStreamBuilder mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntStreamBuilder( new MapEventStream.MapRef2ToIntEventStream<>(eventStream, mapFunction));
    }

    public DoubleStreamBuilder mapToDouble(LambdaReflection.SerializableToDoubleFunction<T> mapFunction) {
        return new DoubleStreamBuilder( new MapEventStream.MapRef2ToDoubleEventStream<>(eventStream, mapFunction));
    }

    public LongStreamBuilder mapToLong(LambdaReflection.SerializableToLongFunction<T> mapFunction) {
        return new LongStreamBuilder( new MapEventStream.MapRef2ToLongEventStream<>(eventStream, mapFunction));
    }

    //OUTPUTS - START
    public EventStreamBuilder<T> push(SerializableConsumer<T> pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new EventStreamBuilder<>(new PushEventStream<>(eventStream, pushFunction));
    }

    public EventStreamBuilder<T> notify(Object target) {
        SepContext.service().add(target);
        return new EventStreamBuilder<>(new NotifyEventStream<>(eventStream, target));
    }

    public EventStreamBuilder<T> processAsNewGraphEvent() {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, new InternalEventDispatcher()::dispatchToGraph));
    }

    public EventStreamBuilder<T> peek(SerializableConsumer<T> peekFunction) {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, peekFunction));
    }

    public EventStreamBuilder<T> console(String in){
        return peek(Peekers.console(in));
    }

    //META-DATA
    public EventStreamBuilder<T> id(String nodeId){
        SepContext.service().add(eventStream, nodeId);
        return this;
    }

    /*
    TODO:
    ================
    More tests

    Done:
    ================
    add peek to primitive streams
    stateful support for functions
    Use transient reference in any stream that has an instance function reference. Remove anchor
    add standard Binary and Map functions for primitives, sum, max, min, add, multiply etc.
    add standard predicates for primitives
    De-dupe filter
    mapOnNotify
    id for eventStream

    optional:
    ================
    log - helper function
    audit - helper function
    add peek functions to support log and audit helpers
    merge/zip
    flatmap
     */

}
