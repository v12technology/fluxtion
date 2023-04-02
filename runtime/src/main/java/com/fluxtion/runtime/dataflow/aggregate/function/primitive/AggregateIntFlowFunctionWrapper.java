package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateIntFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public class AggregateIntFlowFunctionWrapper<F extends AggregateIntFlowFunction<F>>
        extends MapFlowFunction<Integer, Integer, IntFlowFunction>
        implements IntFlowFunction {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F aggregateFunction;

    private int result;

    public AggregateIntFlowFunctionWrapper(IntFlowFunction inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.aggregateFunction = windowFunctionSupplier.get();
        auditInfo = aggregateFunction.getClass().getSimpleName() + "->aggregateInt";
    }

    protected void initialise() {
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @Override
    protected void resetOperation() {
        result = aggregateFunction.resetInt();
    }

    @Override
    protected void mapOperation() {
        result = aggregateFunction.aggregateInt(getInputEventStream().getAsInt());
    }

    @Override
    public int getAsInt() {
        return result;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

}
