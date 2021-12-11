package com.fluxtion.runtim.stream;

import java.lang.reflect.Method;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public interface EventStream <R> {

    R get();

    interface IntEventStream extends TriggeredEventStream<Integer>, IntSupplier{}
    interface DoubleEventStream extends TriggeredEventStream<Integer>, DoubleSupplier {}
    interface LongEventStream extends TriggeredEventStream<Integer>, LongSupplier {}

    /**
     *
     * @param <T> Input type
     * @param <S> EventStream input type
     */
    abstract class AbstractMapToIntEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Integer, S> implements IntEventStream{

        protected transient int result;

        public AbstractMapToIntEventStream(S inputEventStream, Method method) {
            super(inputEventStream, method);
        }

        @Override
        public Integer get() {
            return getAsInt();
        }

        @Override
        public int getAsInt() {
            return result;
        }
    }
}
