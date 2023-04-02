package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.function.AbstractFlowFunction.AbstractBinaryEventStream;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoublePredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiIntPredicate;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongPredicate;

public class FilterDynamicFlowFunction<T, A, S extends FlowFunction<T>, B extends FlowFunction<A>>
        extends AbstractBinaryEventStream<T, A, T, S, B> {

    final SerializableBiFunction<T, A, Boolean> filterFunction;
    transient final String auditInfo;

    public FilterDynamicFlowFunction(@AssignToField("inputEventStream") S inputEventStream,
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

    public static class IntFilterDynamicFlowFunction
            extends AbstractBinaryEventStream<Integer, Integer, Integer, IntFlowFunction, IntFlowFunction>
            implements IntFlowFunction {

        transient final String auditInfo;
        private final SerializableBiIntPredicate filterFunction;

        public IntFilterDynamicFlowFunction(@AssignToField("inputEventStream") IntFlowFunction inputEventStream,
                                            @AssignToField("inputEventStream_2") IntFlowFunction inputEventStream_2,
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


    public static class DoubleFilterDynamicFlowFunction
            extends AbstractBinaryEventStream<Double, Double, Double, DoubleFlowFunction, DoubleFlowFunction>
            implements DoubleFlowFunction {

        transient final String auditInfo;
        @NoTriggerReference
        private final SerializableBiDoublePredicate filterFunction;

        public DoubleFilterDynamicFlowFunction(@AssignToField("inputEventStream") DoubleFlowFunction inputEventStream,
                                               @AssignToField("inputEventStream_2") DoubleFlowFunction inputEventStream_2,
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

    public static class LongFilterDynamicFlowFunction
            extends AbstractBinaryEventStream<Long, Long, Long, LongFlowFunction, LongFlowFunction>
            implements LongFlowFunction {

        transient final String auditInfo;
        private final SerializableBiLongPredicate filterFunction;

        public LongFilterDynamicFlowFunction(@AssignToField("inputEventStream") LongFlowFunction inputEventStream,
                                             @AssignToField("inputEventStream_2") LongFlowFunction inputEventStream_2,
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
