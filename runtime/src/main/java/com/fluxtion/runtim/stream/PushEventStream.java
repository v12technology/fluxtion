package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;

public class PushEventStream<T> extends AbstractEventStream<T, T> {

    @PushReference
    private transient Object target;
    private final SerializableConsumer<T> eventStreamConsumer;
    private String auditInfo;

    public PushEventStream(EventStream<T> inputEventStream, SerializableConsumer<T> eventStreamConsumer) {
        super(inputEventStream);
        this.eventStreamConsumer = eventStreamConsumer;
        this.target = eventStreamConsumer.captured()[0];
        auditInfo = target.getClass().getSimpleName() + "->" + eventStreamConsumer.method().getName();
    }

    @OnEvent
    public void push(){
        auditLog.info("pushTarget", auditInfo);
        eventStreamConsumer.accept(get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

}
