package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.MapEventStream;

import java.util.function.Supplier;

public class AggregateStream <T, R, S extends EventStream<T>, F extends AggregateWindowFunction<T, R, F>>
extends MapEventStream<T, R, S> {

    private final Supplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    public AggregateStream(S inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
        super(inputEventStream, null);
        this.windowFunctionSupplier = windowFunctionSupplier;
        this.mapFunction = windowFunctionSupplier.get();
        auditInfo = mapFunction.getClass().getSimpleName() + "->aggregate";
    }

    protected void initialise() {    }

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
