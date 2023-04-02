package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.DefaultValueSupplier;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Method;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

/**
 * Base class for all mapping operations
 *
 * @param <T> Incoming type
 * @param <R> Output type
 * @param <S> Previous {@link FlowFunction} type
 */
public abstract class MapFlowFunction<T, R, S extends FlowFunction<T>> extends AbstractFlowFunction<T, R, S> {

    protected transient String auditInfo;
    protected transient R result;

    @SuppressWarnings("unchecked")
    public MapFlowFunction(S inputEventStream, MethodReferenceReflection methodReferenceReflection) {
        super(inputEventStream, methodReferenceReflection);
        if (methodReferenceReflection != null) {
            Method method = methodReferenceReflection.method();
            auditInfo = method.getDeclaringClass().getSimpleName() + "->" + method.getName();
        }
    }

    @OnTrigger
    public final boolean map() {
        auditLog.info("mapFunction", auditInfo);
        if (executeUpdate()) {
            auditLog.info("invokeMapFunction", true);
            mapOperation();
        } else if (reset()) {
            auditLog.info("invokeMapFunction", false);
            auditLog.info("reset", true);
//            resetOperation();
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
    public boolean hasDefaultValue() {
        return DefaultValueSupplier.class.isAssignableFrom(getStreamFunction().method().getDeclaringClass());
    }

    @Override
    public R get() {
        return result;
    }

    abstract protected void mapOperation();

    protected void resetOperation() {
        result = resetFunction.reset();
    }


    //***************** REFERENCE map producers START *****************//
    @EqualsAndHashCode(callSuper = true)
    public static class MapRef2RefFlowFunction<T, R, S extends FlowFunction<T>> extends MapFlowFunction<T, R, S> {

        private final SerializableFunction<T, R> mapFunction;

        public MapRef2RefFlowFunction(S inputEventStream, SerializableFunction<T, R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().get());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapInt2RefFlowFunction<R> extends MapFlowFunction<Integer, R, IntFlowFunction> {

        private final SerializableIntFunction<R> mapFunction;

        public MapInt2RefFlowFunction(IntFlowFunction inputEventStream, SerializableIntFunction<R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().getAsInt());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapDouble2RefFlowFunction<R> extends MapFlowFunction<Double, R, DoubleFlowFunction> {

        private final SerializableDoubleFunction<R> mapFunction;

        public MapDouble2RefFlowFunction(DoubleFlowFunction inputEventStream, SerializableDoubleFunction<R> mapFunction) {
            super(inputEventStream, mapFunction);
            this.mapFunction = mapFunction;
        }

        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream().getAsDouble());
        }

    }

    @EqualsAndHashCode(callSuper = true)
    public static class MapLong2RefFlowFunction<R> extends MapFlowFunction<Long, R, LongFlowFunction> {

        private final SerializableLongFunction<R> mapFunction;

        public MapLong2RefFlowFunction(LongFlowFunction inputEventStream, SerializableLongFunction<R> mapFunction) {
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
     * Base class for mapping to an {@link IntFlowFunction}
     *
     * @param <T> Input type
     * @param <S> {@link FlowFunction} input type
     */
    abstract static class AbstractMapToIntFlowFunction<T, S extends FlowFunction<T>> extends MapFlowFunction<T, Integer, S> implements IntFlowFunction {

        protected transient int result;

        public AbstractMapToIntFlowFunction(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation() {
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
    public static class MapRef2ToIntFlowFunction<R, S extends FlowFunction<R>> extends AbstractMapToIntFlowFunction<R, S> {
        private final SerializableToIntFunction<R> intUnaryOperator;

        public MapRef2ToIntFlowFunction(S inputEventStream, SerializableToIntFunction<R> intUnaryOperator) {
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
    public static class MapInt2ToIntFlowFunction extends AbstractMapToIntFlowFunction<Integer, IntFlowFunction> {
        @NoTriggerReference
        private final SerializableIntUnaryOperator intUnaryOperator;

        public MapInt2ToIntFlowFunction(IntFlowFunction inputEventStream, SerializableIntUnaryOperator intUnaryOperator) {
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
    public static class MapDouble2ToIntFlowFunction extends AbstractMapToIntFlowFunction<Double, DoubleFlowFunction> {
        private final SerializableDoubleToIntFunction intUnaryOperator;

        public MapDouble2ToIntFlowFunction(DoubleFlowFunction inputEventStream, SerializableDoubleToIntFunction intUnaryOperator) {
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
    public static class MapLong2ToIntFlowFunction extends AbstractMapToIntFlowFunction<Long, LongFlowFunction> {
        private final SerializableLongToIntFunction intUnaryOperator;

        public MapLong2ToIntFlowFunction(LongFlowFunction inputEventStream, SerializableLongToIntFunction intUnaryOperator) {
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
     * Base class for mapping to an {@link DoubleFlowFunction}
     *
     * @param <T> Input type
     * @param <S> {@link FlowFunction} input type
     */
    abstract static class AbstractMapToDoubleFlowFunction<T, S extends FlowFunction<T>> extends MapFlowFunction<T, Double, S> implements DoubleFlowFunction {

        protected transient double result;

        public AbstractMapToDoubleFlowFunction(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation() {
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
    public static class MapRef2ToDoubleFlowFunction<R, S extends FlowFunction<R>> extends AbstractMapToDoubleFlowFunction<R, S> {
        private final SerializableToDoubleFunction<R> intUnaryOperator;

        public MapRef2ToDoubleFlowFunction(S inputEventStream, SerializableToDoubleFunction<R> intUnaryOperator) {
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
    public static class MapDouble2ToDoubleFlowFunction extends AbstractMapToDoubleFlowFunction<Double, DoubleFlowFunction> {
        private final SerializableDoubleUnaryOperator intUnaryOperator;

        public MapDouble2ToDoubleFlowFunction(DoubleFlowFunction inputEventStream, SerializableDoubleUnaryOperator intUnaryOperator) {
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
    public static class MapInt2ToDoubleFlowFunction extends AbstractMapToDoubleFlowFunction<Integer, IntFlowFunction> {
        private final SerializableIntToDoubleFunction intUnaryOperator;

        public MapInt2ToDoubleFlowFunction(IntFlowFunction inputEventStream, SerializableIntToDoubleFunction intUnaryOperator) {
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
    public static class MapLong2ToDoubleFlowFunction extends AbstractMapToDoubleFlowFunction<Long, LongFlowFunction> {
        private final SerializableLongToDoubleFunction intUnaryOperator;

        public MapLong2ToDoubleFlowFunction(LongFlowFunction inputEventStream, SerializableLongToDoubleFunction intUnaryOperator) {
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
     * Base class for mapping to an {@link LongFlowFunction}
     *
     * @param <T> Input type
     * @param <S> {@link FlowFunction} input type
     */
    abstract static class AbstractMapToLongFlowFunction<T, S extends FlowFunction<T>> extends MapFlowFunction<T, Long, S> implements LongFlowFunction {

        protected transient long result;

        public AbstractMapToLongFlowFunction(S inputEventStream, MethodReferenceReflection method) {
            super(inputEventStream, method);
        }

        protected void resetOperation() {
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
    public static class MapRef2ToLongFlowFunction<R, S extends FlowFunction<R>> extends AbstractMapToLongFlowFunction<R, S> {
        private final LambdaReflection.SerializableToLongFunction<R> intUnaryOperator;

        public MapRef2ToLongFlowFunction(S inputEventStream, LambdaReflection.SerializableToLongFunction<R> intUnaryOperator) {
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
    public static class MapLong2ToLongFlowFunction extends AbstractMapToLongFlowFunction<Long, LongFlowFunction> {
        private final SerializableLongUnaryOperator intUnaryOperator;

        public MapLong2ToLongFlowFunction(LongFlowFunction inputEventStream, SerializableLongUnaryOperator intUnaryOperator) {
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
    public static class MapInt2ToLongFlowFunction extends AbstractMapToLongFlowFunction<Integer, IntFlowFunction> {
        private final SerializableIntToLongFunction intUnaryOperator;

        public MapInt2ToLongFlowFunction(IntFlowFunction inputEventStream, SerializableIntToLongFunction intUnaryOperator) {
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
    public static class MapDouble2ToLongFlowFunction extends AbstractMapToLongFlowFunction<Double, DoubleFlowFunction> {
        private final SerializableDoubleToLongFunction intUnaryOperator;

        public MapDouble2ToLongFlowFunction(DoubleFlowFunction inputEventStream, SerializableDoubleToLongFunction intUnaryOperator) {
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
