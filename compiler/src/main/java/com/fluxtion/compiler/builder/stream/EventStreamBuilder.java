package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;
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

    public EventStreamBuilder<T> filter( LambdaReflection.SerializableFunction<T, Boolean> filterFunction){
        return new EventStreamBuilder<>( new FilterEventStream<>(eventStream, filterFunction));
    }

    public EventStreamBuilder<T> defaultValue(T defaultValue){
        return map(new DefaultValue<>(defaultValue)::getOrDefault);
    }

    //PROCESSING - START
    public <R> EventStreamBuilder<R> map(LambdaReflection.SerializableFunction<T, R> mapFunction) {
        return new EventStreamBuilder<>( new MapEventStream.MapRef2RefEventStream<>(eventStream, mapFunction));
    }

    public IntStreamBuilder<T, EventStream<T>> mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntStreamBuilder<>( new MapEventStream.MapRef2ToIntEventStream<>(eventStream, mapFunction));
    }

    public DoubleStreamBuilder<T, EventStream<T>> mapToDouble(LambdaReflection.SerializableToDoubleFunction<T> mapFunction) {
        return new DoubleStreamBuilder<>( new MapEventStream.MapRef2ToDoubleEventStream<>(eventStream, mapFunction));
    }

    public LongStreamBuilder<T, EventStream<T>> mapToLong(LambdaReflection.SerializableToLongFunction<T> mapFunction) {
        return new LongStreamBuilder<>( new MapEventStream.MapRef2ToLongEventStream<>(eventStream, mapFunction));
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


    /*
    TODO:
    ================
    De-dupe filter
    binaryMap


    optional:
    ================
    log - helper function
    audit - helper function
    merge/zip
    flatmap

     DONE
    ================
    Default helper
    subscribe
    wrapNode
    updateTrigger
    peek
    get
    push
    filter
    notify
    tests
    resetTrigger
    publishTrigger
    primitive map
    primitive tests
     */

}
