package com.fluxtion.runtime.dataflow.aggregate.function;

import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.function.Supplier;

public class AggregateFlowFunctionWrapper<T, R, S extends FlowFunction<T>, F extends AggregateFlowFunction<T, R, F>>
        extends MapFlowFunction<T, R, S> {

    private final Supplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    public AggregateFlowFunctionWrapper(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.mapFunction = windowFunctionSupplier.get();
//        Anchor.anchorCaptured(this, windowFunctionSupplier);
        auditInfo = mapFunction.getClass().getSimpleName() + "->aggregate";
    }

    protected void initialise() {
    }

    @Override
    public boolean isStatefulFunction() {
        return true;
    }

    @Override
    protected void resetOperation() {
        result = mapFunction.reset();
    }

    @Override
    protected void mapOperation() {
        result = mapFunction.aggregate(getInputEventStream().get());
    }


}
