package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoubleFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiIntFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongFunction;

import java.lang.reflect.Method;

import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;

/**
 * @param <R> Type of input stream for first argument
 * @param <Q> Type of input stream for second argument
 * @param <T> Output type of this stream
 * @param <S> The type of {@link EventStream} that wraps R
 * @param <U> The type of {@link EventStream} that wraps Q
 */
public abstract class BinaryMapEventStream<R, Q, T, S extends EventStream<R>, U extends EventStream<Q>>
        extends AbstractEventStream.AbstractBinaryEventStream<R, Q, T, S, U> {

    protected transient String auditInfo;
    protected transient T result;

    public BinaryMapEventStream(
            S inputEventStream,
            U inputEventStream_2,
            MethodReferenceReflection methodReferenceReflection) {
        super(inputEventStream, inputEventStream_2, methodReferenceReflection);
        Method method = methodReferenceReflection.method();
        auditInfo = method.getDeclaringClass().getSimpleName() + "->" + method.getName();
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
    public T get() {
        return result;
    }

    abstract protected void mapOperation();

    protected void resetOperation() {
        inputStreamTriggered_2 = false;
        if (resetFunction != null)
            result = resetFunction.reset();
//        super.resetOperation();
//        System.out.println("Call to binary function reset - not implemented");
    }

    public static class BinaryMapToRefEventStream<R, Q, T, S extends EventStream<R>, U extends EventStream<Q>>
            extends BinaryMapEventStream<R, Q, T, S, U> {

        private final SerializableBiFunction<R, Q, T> mapFunction;

        public BinaryMapToRefEventStream(S inputEventStream_1, U inputEventStream_2, SerializableBiFunction<R, Q, T> methodReferenceReflection) {
            super(inputEventStream_1, inputEventStream_2, methodReferenceReflection);
            mapFunction = methodReferenceReflection;
        }

        @Override
        protected void mapOperation() {
            result = mapFunction.apply(getInputEventStream_1().get(), getInputEventStream_2().get());
        }
    }

    public static class BinaryMapToIntEventStream<S extends IntEventStream, U extends IntEventStream>
            extends BinaryMapEventStream<Integer, Integer, Integer, S, U>
            implements IntEventStream {

        protected transient int result;
        private final SerializableBiIntFunction mapFunction;

        public BinaryMapToIntEventStream(S inputEventStream_1, U inputEventStream_2, SerializableBiIntFunction methodReferenceReflection) {
            super(inputEventStream_1, inputEventStream_2, methodReferenceReflection);
            mapFunction = methodReferenceReflection;
        }

        @Override
        protected void mapOperation() {
            result = mapFunction.applyAsInt(getInputEventStream_1().getAsInt(), getInputEventStream_2().getAsInt());
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


    public static class BinaryMapToDoubleEventStream<S extends DoubleEventStream, U extends DoubleEventStream>
            extends BinaryMapEventStream<Double, Double, Double, S, U>
            implements DoubleEventStream {

        protected transient double result;
        private final SerializableBiDoubleFunction mapFunction;

        public BinaryMapToDoubleEventStream(S inputEventStream_1, U inputEventStream_2, SerializableBiDoubleFunction methodReferenceReflection) {
            super(inputEventStream_1, inputEventStream_2, methodReferenceReflection);
            mapFunction = methodReferenceReflection;
        }

        @Override
        protected void mapOperation() {
            result = mapFunction.applyAsDouble(getInputEventStream_1().getAsDouble(), getInputEventStream_2().getAsDouble());
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


    public static class BinaryMapToLongEventStream<S extends LongEventStream, U extends LongEventStream>
            extends BinaryMapEventStream<Long, Long, Long, S, U>
            implements LongEventStream {

        protected transient long result;
        private final SerializableBiLongFunction mapFunction;

        public BinaryMapToLongEventStream(S inputEventStream_1, U inputEventStream_2, SerializableBiLongFunction methodReferenceReflection) {
            super(inputEventStream_1, inputEventStream_2, methodReferenceReflection);
            mapFunction = methodReferenceReflection;
        }

        @Override
        protected void mapOperation() {
            result = mapFunction.applyAsLong(getInputEventStream_1().getAsLong(), getInputEventStream_2().getAsLong());
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
}
