package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableToIntFunction;

import static com.fluxtion.runtim.partition.LambdaReflection.SerializableIntUnaryOperator;

/**
 *
 * @param <R> Incoming type
 * @param <T> Output type
 * @param <S> Previous EventStream type
 */
public class MapEventStream<R, T, S extends EventStream<R>> extends AbstractEventStream<R, T, S> {

    private final LambdaReflection.SerializableFunction<R, T> mapFunction;
    protected transient String auditInfo;

    private transient T result;

    public MapEventStream(S inputEventStream, SerializableFunction<R, T> mapFunction) {
        super(inputEventStream);
        this.mapFunction = mapFunction;
        if(mapFunction!=null){
            auditInfo = mapFunction.method().getDeclaringClass().getSimpleName() + "->" + mapFunction.method().getName();
        }
    }

    public MapEventStream(S inputEventStream) {
        super(inputEventStream);
        mapFunction = null;
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
    public T get() {
        return result;
    }

    protected void mapOperation() {
        result = mapFunction.apply(getInputEventStream().get());
    }

    public static class MapRef2IntEventStream <R, S extends EventStream<R>> extends MapEventStream<R, Integer, S> implements IntEventStream {

        private transient int result;
        private final LambdaReflection.SerializableToIntFunction<R> intUnaryOperator;

        public MapRef2IntEventStream(S inputEventStream, SerializableToIntFunction<R> intUnaryOperator) {
            super(inputEventStream);
            this.intUnaryOperator = intUnaryOperator;
            auditInfo = intUnaryOperator.method().getDeclaringClass().getSimpleName() + "->" + intUnaryOperator.method().getName();
        }

        @Override
        public Integer get() {
            return getAsInt();
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt( getInputEventStream().get());
        }

        @Override
        public int getAsInt() {
            return result;
        }
    }


    public static class MapInt2IntEventStream extends MapEventStream<Integer, Integer, IntEventStream> implements IntEventStream {

        private transient int result;
        private final SerializableIntUnaryOperator intUnaryOperator;

        public MapInt2IntEventStream(IntEventStream inputEventStream, SerializableIntUnaryOperator intUnaryOperator) {
            super(inputEventStream);
            this.intUnaryOperator = intUnaryOperator;
            auditInfo = intUnaryOperator.method().getDeclaringClass().getSimpleName() + "->" + intUnaryOperator.method().getName();
        }

        @Override
        public Integer get() {
            return getAsInt();
        }

        @Override
        protected void mapOperation() {
            result = intUnaryOperator.applyAsInt( getInputEventStream().getAsInt());
        }

        @Override
        public int getAsInt() {
            return result;
        }
    }

}
