package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntConsumer;

public class PushEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    @PushReference
    private transient final Object target;
    private final SerializableConsumer<T> eventStreamConsumer;
    private transient final String auditInfo;

    public PushEventStream(S inputEventStream, SerializableConsumer<T> eventStreamConsumer) {
        super(inputEventStream);
        this.eventStreamConsumer = eventStreamConsumer;
        this.target = eventStreamConsumer.captured()[0];
        auditInfo = target.getClass().getSimpleName() + "->" + eventStreamConsumer.method().getName();
    }

    @OnEvent
    public boolean push(){
        auditLog.info("pushTarget", auditInfo);
        if(executeUpdate()){
            eventStreamConsumer.accept(get());
        }
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }


    public static class IntPushEventStream extends AbstractEventStream<Integer, Integer, IntEventStream> implements IntEventStream {

        @PushReference
        private transient final Object target;
        private final SerializableIntConsumer intConsumer;
        private transient final String auditInfo;

        public IntPushEventStream(IntEventStream inputEventStream, SerializableIntConsumer intConsumer) {
            super(inputEventStream);
            this.intConsumer = intConsumer;
            this.target = intConsumer.captured()[0];
            auditInfo = target.getClass().getSimpleName() + "->" + intConsumer.method().getName();
        }

        @OnEvent
        public boolean push(){
            auditLog.info("pushTarget", auditInfo);
            if(executeUpdate()){
                intConsumer.accept(get());
            }
            return fireEventUpdateNotification();
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }

        @Override
        public Integer get() {
            return getAsInt();
        }
    }

}
