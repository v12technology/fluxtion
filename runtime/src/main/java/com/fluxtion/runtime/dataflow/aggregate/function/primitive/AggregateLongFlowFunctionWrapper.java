package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

public class AggregateLongFlowFunctionWrapper<F extends AggregateLongFlowFunction<F>>
        extends MapFlowFunction<Long, Long, LongFlowFunction> implements LongFlowFunction {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    private long result;

    public AggregateLongFlowFunctionWrapper(LongFlowFunction inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
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
        result = mapFunction.resetLong();
    }

    @Override
    protected void mapOperation() {
        result = mapFunction.aggregateLong(getInputEventStream().getAsLong());
    }

    @Override
    public long getAsLong() {
        return result;
    }

    @Override
    public Long get() {
        return getAsLong();
    }

}
