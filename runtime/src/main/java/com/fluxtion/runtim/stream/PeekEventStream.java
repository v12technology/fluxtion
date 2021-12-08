package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;

public final class PeekEventStream<T> extends AbstractEventStream<T, T> {

    final SerializableConsumer<T> eventStreamConsumer;

    public PeekEventStream(EventStream<T> inputEventStream, SerializableConsumer<T> eventStreamConsumer) {
        super(inputEventStream);
        this.eventStreamConsumer = eventStreamConsumer;
    }

    @OnEvent
    public void peek(){
        eventStreamConsumer.accept(get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }


}
