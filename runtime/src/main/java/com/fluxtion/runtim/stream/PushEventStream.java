package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;

public class PushEventStream<T> extends AbstractEventStream<T, T> {

    @PushReference
    private Object target;
    private SerializableConsumer<T> eventStreamConsumer;

//    public PushEventStream(
//            EventStream<T> inputEventStream,
//            LambdaReflection.SerializableConsumer<T> eventStreamConsumer) {
//        super(inputEventStream);
//        this.target = eventStreamConsumer.captured()[0];
//        this.eventStreamConsumer = eventStreamConsumer;
//    }

    public PushEventStream(EventStream<T> inputEventStream) {
        super(inputEventStream);
    }

    public void setEventStreamConsumer(SerializableConsumer<T> eventStreamConsumer) {
        this.eventStreamConsumer = eventStreamConsumer;
        this.target = eventStreamConsumer.captured()[0];
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @OnEvent
    public void push(){
        eventStreamConsumer.accept(get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

}
