package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableIntFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongFunction;

public class FilterEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    final SerializableFunction<T, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterEventStream(S inputEventStream, SerializableFunction<T, Boolean> filterFunction) {
        super(inputEventStream, filterFunction);
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnTrigger
    public boolean filter() {
        boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().get());
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


    public static class IntFilterEventStream extends AbstractEventStream<Integer, Integer, IntEventStream> implements IntEventStream {

        final SerializableIntFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public IntFilterEventStream(IntEventStream inputEventStream, SerializableIntFunction<Boolean> filterFunction) {
            super(inputEventStream, filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsInt());
            boolean fireNotification = filter & fireEventUpdateNotification();
            auditLog.info("filterFunction", auditInfo);
            auditLog.info("filterPass", filter);
            auditLog.info("publishToChild", fireNotification);
            return fireNotification;
        }

        @Override
        public Integer get() {
            return getAsInt();
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }


    public static class DoubleFilterEventStream extends AbstractEventStream<Double, Double, DoubleEventStream> implements DoubleEventStream {

        final SerializableDoubleFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public DoubleFilterEventStream(DoubleEventStream inputEventStream, SerializableDoubleFunction<Boolean> filterFunction) {
            super(inputEventStream,filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsDouble());
            boolean fireNotification = filter & fireEventUpdateNotification();
            auditLog.info("filterFunction", auditInfo);
            auditLog.info("filterPass", filter);
            auditLog.info("publishToChild", fireNotification);
            return fireNotification;
        }

        @Override
        public Double get() {
            return getAsDouble();
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }
    }


    public static class LongFilterEventStream extends AbstractEventStream<Long, Long, LongEventStream> implements LongEventStream {

        final SerializableLongFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public LongFilterEventStream(LongEventStream inputEventStream, SerializableLongFunction<Boolean> filterFunction) {
            super(inputEventStream, filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsLong());
            boolean fireNotification = filter & fireEventUpdateNotification();
            auditLog.info("filterFunction", auditInfo);
            auditLog.info("filterPass", filter);
            auditLog.info("publishToChild", fireNotification);
            return fireNotification;
        }

        @Override
        public Long get() {
            return getAsLong();
        }

        @Override
        public long getAsLong() {
            return getInputEventStream().getAsLong();
        }
    }

}
