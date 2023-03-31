package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoublePredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiIntPredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongPredicate;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.impl.AbstractEventStream.AbstractBinaryEventStream;

public class FilterDynamicEventStream<T, A, S extends EventStream<T>, B extends EventStream<A>>
        extends AbstractBinaryEventStream<T, A, T, S, B> {

    final SerializableBiFunction<T, A, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterDynamicEventStream(@AssignToField("inputEventStream") S inputEventStream,
                                    @AssignToField("inputEventStream_2") B inputEventStream_2,
                                    SerializableBiFunction<T, A, Boolean> filterFunction) {
        super(inputEventStream, inputEventStream_2, filterFunction);
        this.filterFunction = filterFunction;
        auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
    }

    @OnTrigger
    public boolean filter() {
        boolean filter = inputStreamTriggered_1
                & (inputStreamTriggered_2)
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

    private A secondArgument() {
        return getInputEventStream_2().get();
    }

    public static class IntFilterDynamicEventStream
            extends AbstractBinaryEventStream<Integer, Integer, Integer, IntEventStream, IntEventStream>
            implements IntEventStream {

        transient final String auditInfo;
        private final SerializableBiIntPredicate filterFunction;

        public IntFilterDynamicEventStream(@AssignToField("inputEventStream") IntEventStream inputEventStream,
                                           @AssignToField("inputEventStream_2") IntEventStream inputEventStream_2,
                                           SerializableBiIntPredicate filterFunction) {
            super(inputEventStream, inputEventStream_2, filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = inputStreamTriggered_1
                    & (inputStreamTriggered_2)
                    && (isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsInt(), getInputEventStream_2().getAsInt()));
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


    public static class DoubleFilterDynamicEventStream
            extends AbstractBinaryEventStream<Double, Double, Double, DoubleEventStream, DoubleEventStream>
            implements DoubleEventStream {

        transient final String auditInfo;
        @NoTriggerReference
        private final SerializableBiDoublePredicate filterFunction;

        public DoubleFilterDynamicEventStream(@AssignToField("inputEventStream") DoubleEventStream inputEventStream,
                                              @AssignToField("inputEventStream_2") DoubleEventStream inputEventStream_2,
                                              SerializableBiDoublePredicate filterFunction) {
            super(inputEventStream, inputEventStream_2, filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = inputStreamTriggered_1 & (inputStreamTriggered_2)
                    && (isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsDouble(), getInputEventStream_2().getAsDouble()));
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

    public static class LongFilterDynamicEventStream
            extends AbstractBinaryEventStream<Long, Long, Long, LongEventStream, LongEventStream>
            implements LongEventStream {

        transient final String auditInfo;
        private final SerializableBiLongPredicate filterFunction;

        public LongFilterDynamicEventStream(@AssignToField("inputEventStream") LongEventStream inputEventStream,
                                            @AssignToField("inputEventStream_2") LongEventStream inputEventStream_2,
                                            SerializableBiLongPredicate filterFunction) {
            super(inputEventStream, inputEventStream_2, filterFunction);
            this.filterFunction = filterFunction;
            auditInfo = filterFunction.method().getDeclaringClass().getSimpleName() + "->" + filterFunction.method().getName();
        }

        @OnTrigger
        public boolean filter() {
            boolean filter = inputStreamTriggered_1
                    & (inputStreamTriggered_2)
                    && (isPublishTriggered() || filterFunction.apply(getInputEventStream().getAsLong(), getInputEventStream_2().getAsLong()));
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
