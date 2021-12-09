package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.SepContext;
import com.fluxtion.runtim.event.DefaultFilteredEventHandler;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtim.stream.*;

public class EventStreamBuilder<T> {

    private final TriggeredEventStream<T> eventStream;

    public EventStreamBuilder(TriggeredEventStream<T> eventStream) {
        SepContext.service().add(eventStream);
        this.eventStream = eventStream;
    }

    static <T> EventStreamBuilder<T> subscribe(Class<T> classSubscription) {
        return new EventStreamBuilder<>(new DefaultFilteredEventHandler<>(classSubscription));
    }

    public <R> EventStreamBuilder<R> map(LambdaReflection.SerializableFunction<T, R> mapFunction) {
        return new EventStreamBuilder<>( new MapEventStream<>(eventStream, mapFunction));
    }

    public EventStreamBuilder<T> filter( LambdaReflection.SerializableFunction<T, Boolean> filterFunction){
        return new EventStreamBuilder<>( new FilterEventStream<>(eventStream, filterFunction));
    }

    public EventStreamBuilder<T> peek(SerializableConsumer<T> peekFunction) {
        return new EventStreamBuilder<>(new PeekEventStream<>(eventStream, peekFunction));
    }

    public EventStreamBuilder<T> push(SerializableConsumer<T> pushFunction) {
        SepContext.service().add(pushFunction.captured()[0]);
        return new EventStreamBuilder<>(new PushEventStream<>(eventStream, pushFunction));
    }

    public EventStreamBuilder<T> notify(Object target) {
        SepContext.service().add(target);
        return new EventStreamBuilder<>(new NotifyEventStream<>(eventStream, target));
    }

    //TRIGGERS
    public EventStreamBuilder<T> updateTrigger(Object updateTrigger){
        eventStream.setUpdateTriggerOverride(updateTrigger);
        return this;
    }

    public EventStreamBuilder<T> updateTrigger(EventStreamBuilder<?> updateTrigger){
        eventStream.setUpdateTriggerOverride(updateTrigger.eventStream);
        return this;
    }

    /*
    TODO:
    binaryMap

    resetTrigger
    publishTrigger

    all tests

    ??? maybe not - need to test - implement last
    primitive map
    primitive get
    primitive tests
    ??? maybe not - need to test - implement last

    optional:
    merge/zip
    flatmap

     DONE
     =======
     updateTrigger
     peek
     get
     push
     filter
     notify
     tests
     */

}
