package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;

public final class PeekEventStream<T> extends AbstractEventStream<T, T> {

    final SerializableConsumer<? super T> eventStreamConsumer;
    transient final String auditInfo;

    public PeekEventStream(EventStream<T> inputEventStream, SerializableConsumer<? super T> eventStreamConsumer) {
        super(inputEventStream);
        this.eventStreamConsumer = eventStreamConsumer;
        auditInfo = eventStreamConsumer.method().getDeclaringClass().getSimpleName() + "->" + eventStreamConsumer.method().getName();
    }

    @OnEvent
    public void peek(){
        auditLog.info("peekConsumer", auditInfo);
        eventStreamConsumer.accept(get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }


}
