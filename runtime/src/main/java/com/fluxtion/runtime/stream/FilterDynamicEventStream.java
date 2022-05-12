package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.stream.AbstractEventStream.AbstractBinaryEventStream;

public class FilterDynamicEventStream<T, A, S extends EventStream<T>, B extends EventStream<A>> extends AbstractBinaryEventStream<T, A, T, S, B> {

    final SerializableBiFunction<T, A, Boolean> filterFunction;
    transient final String auditInfo;

    @SepNode
    private A defaultValue;

    public FilterDynamicEventStream(S inputEventStream, B inputEventStream_2, SerializableBiFunction<T, A, Boolean> filterFunction) {
        super(inputEventStream, inputEventStream_2, filterFunction);
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnTrigger
    public boolean filter() {
        boolean filter = inputStreamTriggered
                & (inputStreamTriggered_2 | defaultValue!=null)
                && (isPublishTriggered() || filterFunction.apply(getInputEventStream().get(), secondArgument()));
        boolean fireNotification = filter & fireEventUpdateNotification();
        auditLog.info("filterFunction", auditInfo);
        auditLog.info("filterPass", filter);
        auditLog.info("publishToChild", fireNotification);
        return fireNotification;
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

    public A getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(A defaultValue) {
        this.defaultValue = defaultValue;
    }

    private A secondArgument(){
        if(inputStreamTriggered_2){
            return getInputEventStream_2().get();
        }
        return defaultValue;
    }


//    public static class IntFilterEventStream extends AbstractEventStream<Integer, Integer, IntEventStream> implements IntEventStream {
//
//        final SerializableIntFunction<Boolean> filterFunction;
//        transient final String auditInfo;
//
//        public IntFilterEventStream(IntEventStream inputEventStream, SerializableIntFunction<Boolean> filterFunction) {
//            super(inputEventStream, filterFunction);
//            this.filterFunction = filterFunction;
//            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
//        }
//
//        @OnTrigger
//        public boolean filter() {
//            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsInt());
//            boolean fireNotification = filter & fireEventUpdateNotification();
//            auditLog.info("filterFunction", auditInfo);
//            auditLog.info("filterPass", filter);
//            auditLog.info("publishToChild", fireNotification);
//            return fireNotification;
//        }
//
//        @Override
//        public Integer get() {
//            return getAsInt();
//        }
//
//        @Override
//        public int getAsInt() {
//            return getInputEventStream().getAsInt();
//        }
//    }
//
//
//    public static class DoubleFilterEventStream extends AbstractEventStream<Double, Double, DoubleEventStream> implements DoubleEventStream {
//
//        final SerializableDoubleFunction<Boolean> filterFunction;
//        transient final String auditInfo;
//
//        public DoubleFilterEventStream(DoubleEventStream inputEventStream, SerializableDoubleFunction<Boolean> filterFunction) {
//            super(inputEventStream,filterFunction);
//            this.filterFunction = filterFunction;
//            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
//        }
//
//        @OnTrigger
//        public boolean filter() {
//            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsDouble());
//            boolean fireNotification = filter & fireEventUpdateNotification();
//            auditLog.info("filterFunction", auditInfo);
//            auditLog.info("filterPass", filter);
//            auditLog.info("publishToChild", fireNotification);
//            return fireNotification;
//        }
//
//        @Override
//        public Double get() {
//            return getAsDouble();
//        }
//
//        @Override
//        public double getAsDouble() {
//            return getInputEventStream().getAsDouble();
//        }
//    }
//
//
//    public static class LongFilterEventStream extends AbstractEventStream<Long, Long, LongEventStream> implements LongEventStream {
//
//        final SerializableLongFunction<Boolean> filterFunction;
//        transient final String auditInfo;
//
//        public LongFilterEventStream(LongEventStream inputEventStream, SerializableLongFunction<Boolean> filterFunction) {
//            super(inputEventStream, filterFunction);
//            this.filterFunction = filterFunction;
//            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
//        }
//
//        @OnTrigger
//        public boolean filter() {
//            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsLong());
//            boolean fireNotification = filter & fireEventUpdateNotification();
//            auditLog.info("filterFunction", auditInfo);
//            auditLog.info("filterPass", filter);
//            auditLog.info("publishToChild", fireNotification);
//            return fireNotification;
//        }
//
//        @Override
//        public Long get() {
//            return getAsLong();
//        }
//
//        @Override
//        public long getAsLong() {
//            return getInputEventStream().getAsLong();
//        }
//    }

}
