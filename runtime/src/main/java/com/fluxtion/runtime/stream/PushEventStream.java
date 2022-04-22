package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableIntConsumer;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongConsumer;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class PushEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    @PushReference
    private transient final Object target;
    private final SerializableConsumer<T> eventStreamConsumer;
    private transient final String auditInfo;

    public PushEventStream(S inputEventStream, SerializableConsumer<T> eventStreamConsumer) {
        super(inputEventStream, null);
        this.eventStreamConsumer = eventStreamConsumer;
        if(eventStreamConsumer.captured().length > 0) {
            this.target = eventStreamConsumer.captured()[0];
        }else{
            this.target = null;
        }
        auditInfo = eventStreamConsumer.method().getDeclaringClass().getSimpleName() + "->" + eventStreamConsumer.method().getName();
    }

    @OnTrigger
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

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class IntPushEventStream extends AbstractEventStream<Integer, Integer, IntEventStream> implements IntEventStream {

        @PushReference
        private transient final Object target;
        private final SerializableIntConsumer intConsumer;
        private transient final String auditInfo;

        public IntPushEventStream(IntEventStream inputEventStream, SerializableIntConsumer intConsumer) {
            super(inputEventStream, null);
            this.intConsumer = intConsumer;
            if(intConsumer.captured().length > 0) {
                this.target = intConsumer.captured()[0];
            }else{
                this.target = null;
            }
            auditInfo = intConsumer.method().getDeclaringClass().getSimpleName() + "->" + intConsumer.method().getName();
        }

        @OnTrigger
        public boolean push(){
            auditLog.info("pushTarget", auditInfo);
            if(executeUpdate()){
                intConsumer.accept(getAsInt());
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

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class DoublePushEventStream extends AbstractEventStream<Double, Double, DoubleEventStream> implements DoubleEventStream {

        @PushReference
        private transient final Object target;
        private final SerializableDoubleConsumer intConsumer;
        private transient final String auditInfo;

        public DoublePushEventStream(DoubleEventStream inputEventStream, SerializableDoubleConsumer intConsumer) {
            super(inputEventStream, null);
            this.intConsumer = intConsumer;
            if(intConsumer.captured().length > 0) {
                this.target = intConsumer.captured()[0];
            }else{
                this.target = null;
            }
            auditInfo = intConsumer.method().getDeclaringClass().getSimpleName() + "->" + intConsumer.method().getName();
        }

        @OnTrigger
        public boolean pushValue(){
            auditLog.info("pushTarget", auditInfo);
            if(executeUpdate()){
                intConsumer.accept(getAsDouble());
            }
            return fireEventUpdateNotification();
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }

        @Override
        public Double get() {
            return getAsDouble();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class LongPushEventStream extends AbstractEventStream<Long, Long, LongEventStream> implements LongEventStream {

        @PushReference
        private transient final Object target;
        private final SerializableLongConsumer intConsumer;
        private transient final String auditInfo;

        public LongPushEventStream(LongEventStream inputEventStream, SerializableLongConsumer intConsumer) {
            super(inputEventStream, null);
            this.intConsumer = intConsumer;
            if(intConsumer.captured().length > 0) {
                this.target = intConsumer.captured()[0];
            }else{
                this.target = null;
            }
            auditInfo = intConsumer.method().getDeclaringClass().getSimpleName() + "->" + intConsumer.method().getName();
        }

        @OnTrigger
        public boolean push(){
            auditLog.info("pushTarget", auditInfo);
            if(executeUpdate()){
                intConsumer.accept(getAsLong());
            }
            return fireEventUpdateNotification();
        }

        @Override
        public long getAsLong() {
            return getInputEventStream().getAsLong();
        }

        @Override
        public Long get() {
            return getAsLong();
        }
    }
}
