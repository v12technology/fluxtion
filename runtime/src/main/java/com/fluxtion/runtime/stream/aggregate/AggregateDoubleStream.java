package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.MapEventStream;

public class AggregateDoubleStream<F extends BaseDoubleSlidingWindowFunction<F>>
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
}
