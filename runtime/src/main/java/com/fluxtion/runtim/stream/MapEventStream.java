package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableToIntFunction;

import java.lang.reflect.Method;

import static com.fluxtion.runtim.partition.LambdaReflection.SerializableIntUnaryOperator;

/**
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous EventStream type
 */
public abstract class MapEventStream<T, R, S extends EventStream<T>> extends AbstractEventStream<T, R, S> {

    protected transient String auditInfo;
    protected transient R result;

    public MapEventStream(S inputEventStream, Method method) {
        super(inputEventStream);
            auditInfo = method.getDeclaringClass().getSimpleName() + "->" + method.getName();
    }

    @OnEvent
    public final boolean map() {
        auditLog.info("mapFunction", auditInfo);
        if (executeUpdate()) {
            auditLog.info("invokeMapFunction", true);
            mapOperation();
        } else {
            auditLog.info("invokeMapFunction", false);
        }
        return fireEventUpdateNotification();
    }

    @Override
    public R get() {
        return result;
    }

    abstract protected void mapOperation();

    public static class MapRef2RefEventStream<T, R, S extends EventStream<T>> extends  MapEventStream<T, R, S> {

        private final LambdaReflection.SerializableFunction<T, R> mapFunction;
        public MapRef2RefEventStream(S inputEventStream, SerializableFunction<T, R> mapFunction) {
            super(inputEventStream, mapFunction.method());
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().get());
        }

    }

    public static class MapInt2ToIntEventStream extends AbstractMapToIntEventStream<Integer, IntEventStream> {
        private final SerializableIntUnaryOperator intUnaryOperator;

        public MapInt2ToIntEventStream(IntEventStream inputEventStream, SerializableIntUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().getAsInt());
        }
    }

    public static class MapRef2ToIntEventStream<R, S extends EventStream<R>> extends AbstractMapToIntEventStream<R, S> {
        private final SerializableToIntFunction<R> intUnaryOperator;

        public MapRef2ToIntEventStream(S inputEventStream, SerializableToIntFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().get());
        }
    }

}
