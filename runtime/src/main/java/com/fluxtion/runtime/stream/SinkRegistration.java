package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.event.DefaultEvent;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class SinkRegistration<T> extends DefaultEvent{

    private Consumer<T> consumer;
    private IntConsumer intConsumer;

    private DoubleConsumer doubleConsumer;

    private LongConsumer longConsumer;

    public static <S> SinkRegistration<S> sink(String filterId, Consumer<S> consumer){
        return new SinkRegistration<>(filterId, consumer);
    }

    public static SinkRegistration<Integer> intSink(String filterId, IntConsumer consumer){
        return new SinkRegistration<>(filterId, consumer);
    }

    public static SinkRegistration<Integer> doubleSink(String filterId, DoubleConsumer consumer){
        return new SinkRegistration<>(filterId, consumer);
    }

    public static SinkRegistration<Integer> longSink(String filterId, LongConsumer consumer){
        return new SinkRegistration<>(filterId, consumer);
    }

    private SinkRegistration(String filterId, Consumer<T> consumer) {
        super(filterId);
        this.consumer = consumer;
    }

    private SinkRegistration(String filterId, IntConsumer consumer) {
        super(filterId);
        this.intConsumer = consumer;
    }

    private SinkRegistration(String filterId, DoubleConsumer consumer) {
        super(filterId);
        this.doubleConsumer = consumer;
    }

    private SinkRegistration(String filterId, LongConsumer consumer) {
        super(filterId);
        this.longConsumer = consumer;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public IntConsumer getIntConsumer() {
        return intConsumer;
    }

    public DoubleConsumer getDoubleConsumer() {
        return doubleConsumer;
    }

    public LongConsumer getLongConsumer() {
        return longConsumer;
    }
}
