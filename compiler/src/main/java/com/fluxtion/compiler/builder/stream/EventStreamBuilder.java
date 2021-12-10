package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtim.stream.*;

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

    //PROCESSING - START
    public <R> EventStreamBuilder<R> map(LambdaReflection.SerializableFunction<T, R> mapFunction) {
        return new EventStreamBuilder<>( new MapEventStream<>(eventStream, mapFunction));
    }

    public IntStreamBuilder<T, EventStream<T>> mapToInt(LambdaReflection.SerializableToIntFunction<T> mapFunction) {
        return new IntStreamBuilder<>( new MapEventStream.MapRef2IntEventStream<>(eventStream, mapFunction));
    }

    public EventStreamBuilder<T> filter( LambdaReflection.SerializableFunction<T, Boolean> filterFunction){
        return new EventStreamBuilder<>( new FilterEventStream<>(eventStream, filterFunction));
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
    log - helper function
    ??? maybe not - need to test - implement last
    primitive map
    primitive tests
    ??? maybe not - need to test - implement last

    optional:
    ================
    audit - helper function
    merge/zip
    flatmap
    binaryMap
    tests

     DONE
    ================
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
     */

}
