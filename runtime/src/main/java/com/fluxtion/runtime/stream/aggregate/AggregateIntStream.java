package com.fluxtion.runtime.stream.aggregate;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.MapEventStream;

public class AggregateIntStream<F extends BaseIntSlidingWindowFunction<F>>
        extends MapEventStream<Integer, Integer, IntEventStream> implements IntEventStream {
    private final SerializableSupplier<F> windowFunctionSupplier;
    private transient final F mapFunction;

    private int result;

    public AggregateIntStream(IntEventStream inputEventStream, SerializableSupplier<F> windowFunctionSupplier) {
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
        result = mapFunction.resetInt();
    }

    @Override
    protected void mapOperation() {
        result = mapFunction.aggregateInt(getInputEventStream().getAsInt());
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
