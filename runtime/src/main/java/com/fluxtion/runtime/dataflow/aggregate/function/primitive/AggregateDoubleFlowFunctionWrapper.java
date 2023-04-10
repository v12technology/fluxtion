package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateDoubleFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public class AggregateDoubleFlowFunctionWrapper<F extends AggregateDoubleFlowFunction<F>>
        extends MapFlowFunction<Double, Double, DoubleFlowFunction> implements DoubleFlowFunction {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    private double result;

    public AggregateDoubleFlowFunctionWrapper(DoubleFlowFunction inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
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
