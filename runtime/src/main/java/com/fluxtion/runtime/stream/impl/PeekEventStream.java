package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.runtime.stream.EventStream;
import lombok.ToString;

public class PeekEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    @NoTriggerReference
    final SerializableConsumer<? super T> eventStreamConsumer;
    transient final String auditInfo;

    public PeekEventStream(S inputEventStream, SerializableConsumer<? super T> eventStreamConsumer) {
        super(inputEventStream, eventStreamConsumer);
        this.eventStreamConsumer = eventStreamConsumer;
        auditInfo = eventStreamConsumer.method().getDeclaringClass().getSimpleName()
                + "->" + eventStreamConsumer.method().getName();
    }

    @OnTrigger
    public void peek() {
        auditLog.info("peekConsumer", auditInfo);
        eventStreamConsumer.accept(get());
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

    @ToString
    public static class IntPeekEventStream extends PeekEventStream<Integer, IntEventStream> implements IntEventStream {

        public IntPeekEventStream(IntEventStream inputEventStream, SerializableConsumer<? super Integer> eventStreamConsumer) {
            super(inputEventStream, eventStreamConsumer);
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }


    @ToString
    public static class DoublePeekEventStream extends PeekEventStream<Double, DoubleEventStream> implements DoubleEventStream {

        public DoublePeekEventStream(DoubleEventStream inputEventStream, SerializableConsumer<? super Double> eventStreamConsumer) {
            super(inputEventStream, eventStreamConsumer);
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }
    }


    @ToString
    public static class LongPeekEventStream extends PeekEventStream<Long, LongEventStream> implements LongEventStream {

        public LongPeekEventStream(LongEventStream inputEventStream, SerializableConsumer<? super Long> eventStreamConsumer) {
            super(inputEventStream, eventStreamConsumer);
        }

        @Override
        public long getAsLong() {
            return getInputEventStream().getAsLong();
        }
    }

}
