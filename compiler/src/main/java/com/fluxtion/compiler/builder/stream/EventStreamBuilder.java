package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtim.stream.*;
import com.fluxtion.runtim.stream.helpers.DefaultValue;

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

    public EventStreamBuilder<T> peek(SerializableConsumer<T> peekFunction) {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, peekFunction));
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
