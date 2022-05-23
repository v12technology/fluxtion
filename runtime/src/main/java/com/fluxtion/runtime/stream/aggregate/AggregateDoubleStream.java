package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.MapEventStream;
import com.fluxtion.runtime.time.FixedRateTrigger;

public class AggregateDoubleStream<F extends DoubleAggregateFunction<F>>
        extends MapEventStream<Double, Double, DoubleEventStream> implements DoubleEventStream {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    private double result;

    public AggregateDoubleStream(DoubleEventStream inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.mapFunction = windowFunctionSupplier.get();
        auditInfo = mapFunction.getClass().getSimpleName() + "->aggregateInt";
    }

    protected void initialise() {
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @Override
    protected void resetOperation() {
        result = mapFunction.resetDouble();
    }

    @Override
    protected void mapOperation() {
        result = mapFunction.aggregateDouble(getInputEventStream().getAsDouble());
    }

    @Override
    public double getAsDouble() {
        return result;
    }

    @Override
    public Double get() {
        return getAsDouble();
    }

    public static class TumblingDoubleWindowStream<F extends DoubleAggregateFunction<F>>
            extends EventLogNode
            implements DoubleEventStream {

        @NoTriggerReference
        private final DoubleEventStream inputEventStream;
        private final SerializableSupplier<F> windowFunctionSupplier;
        private transient final F windowFunction;
        public FixedRateTrigger rollTrigger;

        private double value;

        public TumblingDoubleWindowStream(DoubleEventStream inputEventStream,
                                          SerializableSupplier<F> windowFunctionSupplier,
                                          int windowSizeMillis) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
            rollTrigger = FixedRateTrigger.atMillis(windowSizeMillis);
        }

        public TumblingDoubleWindowStream(DoubleEventStream inputEventStream,
                                          SerializableSupplier<F> windowFunctionSupplier) {
            this.inputEventStream = inputEventStream;
            this.windowFunctionSupplier = windowFunctionSupplier;
            this.windowFunction = windowFunctionSupplier.get();
        }

        @Override
        public double getAsDouble() {
            return value;
        }

        @OnParentUpdate
        public void timeTriggerFired(FixedRateTrigger rollTrigger) {
            value = windowFunction.getAsDouble();
            windowFunction.reset();
        }

        @OnParentUpdate
        public void updateData(DoubleEventStream inputEventStream) {
            windowFunction.aggregateDouble(inputEventStream.getAsDouble());
        }

        @OnTrigger
        public boolean triggered() {
            return true;
        }

        @Override
        public void setUpdateTriggerNode(Object updateTriggerNode) {

        }

        @Override
        public void setPublishTriggerNode(Object publishTriggerNode) {

        }

        @Override
        public void setResetTriggerNode(Object resetTriggerNode) {

        }

        @Override
        public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

        }
    }
}
