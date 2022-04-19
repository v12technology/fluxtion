package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.NoEventReference;
import com.fluxtion.runtime.annotations.OnEvent;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.aggregate.BaseSlidingWindowFunction;
import com.fluxtion.runtime.stream.aggregate.SlidingWindowedValueStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

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
    @NoEventReference
    protected transient Stateful<R> resetFunction;


    @SuppressWarnings("unchecked")
    public MapEventStream(S inputEventStream, MethodReferenceReflection methodReferenceReflection) {
        super(inputEventStream, methodReferenceReflection);
        Method method = methodReferenceReflection.method();
        auditInfo = method.getDeclaringClass().getSimpleName() + "->" + method.getName();
        if(isStatefulFunction()){
            resetFunction = (Stateful<R>) methodReferenceReflection.captured()[0];
        }
    }

    @OnEvent
    public final boolean map() {
        auditLog.info("mapFunction", auditInfo);
        if (executeUpdate()) {
            auditLog.info("invokeMapFunction", true);
            mapOperation();
        } else if(reset()) {
            auditLog.info("invokeMapFunction", false);
            auditLog.info("reset", true);
            resetOperation();
        } else {
            auditLog.info("invokeMapFunction", false);
        }
        return fireEventUpdateNotification();
    }

    @Override
    protected void initialise() {
        Method method = getStreamFunction().method();
        if (DefaultValueSupplier.class.isAssignableFrom(method.getDeclaringClass())) {
            mapOperation();
        }
    }


    @Override
    public R get() {
        return result;
    }

    abstract protected void mapOperation();

    protected void resetOperation(){
        result = resetFunction.reset();
    }


    //***************** REFERENCE map producers START *****************//
    @EqualsAndHashCode(callSuper = true)
    public static class MapRef2RefEventStream<T, R, S extends EventStream<T>> extends MapEventStream<T, R, S> {

        private final SerializableFunction<T, R> mapFunction;

        public MapRef2RefEventStream(S inputEventStream, SerializableFunction<T, R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().get());
        }

    }

    /**
     *
     * I, R, T extends BaseSlidingWindowFunction<I, R, T>
     *
     *
     * @param <T> Incoming type
     * @param <R> Outgoing type
     * @param <S> Previous EventStream
     */
    public static class SlidingWindowMapRef2RefEventStream< T, R, S extends EventStream<T>, W extends BaseSlidingWindowFunction<T, R, W>> extends MapEventStream<T, R, S> {

        private final SerializableFunction<T, R> mapFunction;
        private Supplier<W> function;
        private SlidingWindowedValueStream<T, R, W> windowValueStream;

        public SlidingWindowMapRef2RefEventStream(S inputEventStream, SerializableFunction<T, R> mapFunction, Supplier<W> windowFunctionSupplier) {
            super(inputEventStream, mapFunction);
            windowValueStream = new SlidingWindowedValueStream<>(windowFunctionSupplier, 1);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {

            windowValueStream.aggregate(getInputEventStream().get());

            result = mapFunction.apply(getInputEventStream().get());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapInt2RefEventStream<R> extends MapEventStream<Integer, R, IntEventStream> {

        private final SerializableIntFunction<R> mapFunction;

        public MapInt2RefEventStream(IntEventStream inputEventStream, SerializableIntFunction<R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().getAsInt());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapDouble2RefEventStream<R> extends MapEventStream<Double, R, DoubleEventStream> {

        private final SerializableDoubleFunction<R> mapFunction;

        public MapDouble2RefEventStream(DoubleEventStream inputEventStream, SerializableDoubleFunction<R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().getAsDouble());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapLong2RefEventStream<R> extends MapEventStream<Long, R, LongEventStream> {

        private final SerializableLongFunction<R> mapFunction;

        public MapLong2RefEventStream(LongEventStream inputEventStream, SerializableLongFunction<R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().getAsLong());
        }

    }

    //***************** REFERENCE map producers START *****************//


    //***************** INTEGER map producers START *****************//

    /**
     * Base class for mapping to an {@link com.fluxtion.runtime.stream.EventStream.IntEventStream}
     *
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToIntEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Integer, S> implements IntEventStream {

        protected transient int result;

        public AbstractMapToIntEventStream(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation(){
            result = resetFunction.reset();
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

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapRef2ToIntEventStream<R, S extends EventStream<R>> extends AbstractMapToIntEventStream<R, S> {
        private final SerializableToIntFunction<R> intUnaryOperator;

        public MapRef2ToIntEventStream(S inputEventStream, SerializableToIntFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().get());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapInt2ToIntEventStream extends AbstractMapToIntEventStream<Integer, IntEventStream> {
        @NoEventReference
        private final SerializableIntUnaryOperator intUnaryOperator;

        public MapInt2ToIntEventStream(IntEventStream inputEventStream, SerializableIntUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().getAsInt());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapDouble2ToIntEventStream extends AbstractMapToIntEventStream<Double, DoubleEventStream> {
        private final SerializableDoubleToIntFunction intUnaryOperator;

        public MapDouble2ToIntEventStream(DoubleEventStream inputEventStream, SerializableDoubleToIntFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().getAsDouble());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapLong2ToIntEventStream extends AbstractMapToIntEventStream<Long, LongEventStream> {
        private final SerializableLongToIntFunction intUnaryOperator;

        public MapLong2ToIntEventStream(LongEventStream inputEventStream, SerializableLongToIntFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt(getInputEventStream().getAsLong());
        }
    }

    //***************** INTEGER map producers END *****************//


    //***************** DOUBLE map producers START *****************//

    /**
     * Base class for mapping to an {@link com.fluxtion.runtime.stream.EventStream.DoubleEventStream}
     *
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToDoubleEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Double, S> implements DoubleEventStream {

        protected transient double result;

        public AbstractMapToDoubleEventStream(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation(){
            result = resetFunction.reset();
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

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapRef2ToDoubleEventStream<R, S extends EventStream<R>> extends AbstractMapToDoubleEventStream<R, S> {
        private final SerializableToDoubleFunction<R> intUnaryOperator;

        public MapRef2ToDoubleEventStream(S inputEventStream, SerializableToDoubleFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().get());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapDouble2ToDoubleEventStream extends AbstractMapToDoubleEventStream<Double, DoubleEventStream> {
        private final SerializableDoubleUnaryOperator intUnaryOperator;

        public MapDouble2ToDoubleEventStream(DoubleEventStream inputEventStream, SerializableDoubleUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().getAsDouble());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapInt2ToDoubleEventStream extends AbstractMapToDoubleEventStream<Integer, IntEventStream> {
        private final SerializableIntToDoubleFunction intUnaryOperator;

        public MapInt2ToDoubleEventStream(IntEventStream inputEventStream, SerializableIntToDoubleFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().getAsInt());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapLong2ToDoubleEventStream extends AbstractMapToDoubleEventStream<Long, LongEventStream> {
        private final SerializableLongToDoubleFunction intUnaryOperator;

        public MapLong2ToDoubleEventStream(LongEventStream inputEventStream, SerializableLongToDoubleFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsDouble(getInputEventStream().getAsLong());
        }
    }

    //***************** DOUBLE map producers END *****************//


    //***************** LONG map producers START *****************//

    /**
     * Base class for mapping to an {@link com.fluxtion.runtime.stream.EventStream.LongEventStream}
     *
     * @param <T> Input type
     * @param <S> {@link EventStream} input type
     */
    abstract static class AbstractMapToLongEventStream<T, S extends EventStream<T>> extends MapEventStream<T, Long, S> implements LongEventStream {

        protected transient long result;

        public AbstractMapToLongEventStream(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation(){
            result = resetFunction.reset();
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

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapRef2ToLongEventStream<R, S extends EventStream<R>> extends AbstractMapToLongEventStream<R, S> {
        private final LambdaReflection.SerializableToLongFunction<R> intUnaryOperator;

        public MapRef2ToLongEventStream(S inputEventStream, LambdaReflection.SerializableToLongFunction<R> intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().get());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapLong2ToLongEventStream extends AbstractMapToLongEventStream<Long, LongEventStream> {
        private final SerializableLongUnaryOperator intUnaryOperator;

        public MapLong2ToLongEventStream(LongEventStream inputEventStream, SerializableLongUnaryOperator intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().getAsLong());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapInt2ToLongEventStream extends AbstractMapToLongEventStream<Integer, IntEventStream> {
        private final SerializableIntToLongFunction intUnaryOperator;

        public MapInt2ToLongEventStream(IntEventStream inputEventStream, SerializableIntToLongFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().getAsInt());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    public static class MapDouble2ToLongEventStream extends AbstractMapToLongEventStream<Double, DoubleEventStream> {
        private final SerializableDoubleToLongFunction intUnaryOperator;

        public MapDouble2ToLongEventStream(DoubleEventStream inputEventStream, SerializableDoubleToLongFunction intUnaryOperator) {
            super(inputEventStream, intUnaryOperator);
            this.intUnaryOperator = intUnaryOperator;
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsLong(getInputEventStream().getAsDouble());
        }
    }
    //***************** LONG map producers END *****************//
}
