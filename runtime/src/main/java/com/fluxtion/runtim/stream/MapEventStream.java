package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.*;

import java.lang.reflect.Method;

import static com.fluxtion.runtim.partition.LambdaReflection.SerializableIntUnaryOperator;

/**
 * Base class for all mapping operations
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link EventStream} type
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



    //***************** INTEGER map producers START *****************//
    /**
     * Base class for mapping to an {@link com.fluxtion.runtim.stream.EventStream.IntEventStream}
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToIntEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Integer, S> implements IntEventStream{

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
    //***************** INTEGER map producers END *****************//



    //***************** DOUBLE map producers START *****************//

    /**
     * Base class for mapping to an {@link com.fluxtion.runtim.stream.EventStream.DoubleEventStream}
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToDoubleEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Double, S> implements DoubleEventStream{

        protected transient double result;

        public AbstractMapToDoubleEventStream(S inputEventStream, Method method) {
            super(inputEventStream, method);
        }

        @Override
        public Double get() {
            return getAsDouble();
        }

        @Override
        public double getAsDouble() {
            return result;
        }
    }

    public static class MapRef2ToDoubleEventStream<R, S extends EventStream<R>> extends AbstractMapToDoubleEventStream<R, S> {
        private final SerializableToDoubleFunction<R> intUnaryOperator;

        public MapRef2ToDoubleEventStream(S inputEventStream, SerializableToDoubleFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().get());
        }
    }

    public static class MapDouble2ToDoubleEventStream extends AbstractMapToDoubleEventStream<Double, DoubleEventStream> {
        private final SerializableDoubleUnaryOperator intUnaryOperator;

        public MapDouble2ToDoubleEventStream(DoubleEventStream inputEventStream, SerializableDoubleUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().getAsDouble());
        }
    }
    //***************** DOUBLE map producers END *****************//


    //***************** LONG map producers START *****************//

    /**
     * Base class for mapping to an {@link com.fluxtion.runtim.stream.EventStream.LongEventStream}
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToLongEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Long, S> implements LongEventStream{

        protected transient long result;

        public AbstractMapToLongEventStream(S inputEventStream, Method method) {
            super(inputEventStream, method);
        }

        @Override
        public Long get() {
            return getAsLong();
        }

        @Override
        public long getAsLong() {
            return result;
        }
    }


    public static class MapRef2ToLongEventStream<R, S extends EventStream<R>> extends AbstractMapToLongEventStream<R, S> {
        private final LambdaReflection.SerializableToLongFunction<R> intUnaryOperator;

        public MapRef2ToLongEventStream(S inputEventStream, LambdaReflection.SerializableToLongFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().get());
        }
    }

    public static class MapLong2ToLongEventStream extends AbstractMapToLongEventStream<Long, LongEventStream> {
        private final SerializableLongUnaryOperator intUnaryOperator;

        public MapLong2ToLongEventStream(LongEventStream inputEventStream, SerializableLongUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator.method());
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().getAsLong());
        }
    }

    //***************** DOUBLE map producers END *****************//
}
