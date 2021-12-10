package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;

public final class PeekEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    final SerializableConsumer<? super T> eventStreamConsumer;
    transient final String auditInfo;

    public PeekEventStream(S inputEventStream, SerializableConsumer<? super T> eventStreamConsumer) {
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
