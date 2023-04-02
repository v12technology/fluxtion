package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableIntFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableLongFunction;

public class FilterFlowFunction<T, S extends FlowFunction<T>> extends AbstractFlowFunction<T, T, S> {

    final SerializableFunction<T, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterFlowFunction(S inputEventStream, SerializableFunction<T, Boolean> filterFunction) {
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


    public static class IntFilterFlowFunction extends AbstractFlowFunction<Integer, Integer, IntFlowFunction> implements IntFlowFunction {

        final SerializableIntFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public IntFilterFlowFunction(IntFlowFunction inputEventStream, SerializableIntFunction<Boolean> filterFunction) {
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


    public static class DoubleFilterFlowFunction extends AbstractFlowFunction<Double, Double, DoubleFlowFunction> implements DoubleFlowFunction {

        final SerializableDoubleFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public DoubleFilterFlowFunction(DoubleFlowFunction inputEventStream, SerializableDoubleFunction<Boolean> filterFunction) {
            super(inputEventStream, filterFunction);
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


    public static class LongFilterFlowFunction extends AbstractFlowFunction<Long, Long, LongFlowFunction> implements LongFlowFunction {

        final SerializableLongFunction<Boolean> filterFunction;
        transient final String auditInfo;

        public LongFilterFlowFunction(LongFlowFunction inputEventStream, SerializableLongFunction<Boolean> filterFunction) {
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
