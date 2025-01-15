package com.fluxtion.runtime.dataflow.aggregate.function.primitive;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.aggregate.AggregateLongFlowFunction;

import java.util.function.BooleanSupplier;

public abstract class AbstractLongFlowFunction<T extends AbstractLongFlowFunction<T>>
        implements LongFlowFunction, AggregateLongFlowFunction<T> {

    protected long value;
    protected boolean reset = true;
    @Inject
    public DirtyStateMonitor dirtyStateMonitor;
    private BooleanSupplier dirtySupplier;
    private transient boolean parallelCandidate = false;

    @Initialise
    public void init() {
        dirtySupplier = dirtyStateMonitor.dirtySupplier(this);
    }


    @Override
    public long resetLong() {
        value = 0;
        reset = true;
        return getAsLong();
    }

    @Override
    public boolean hasChanged() {
        return dirtySupplier.getAsBoolean();
    }

    @Override
    public void parallel() {
        parallelCandidate = true;
    }

    @Override
    public boolean parallelCandidate() {
        return parallelCandidate;
    }

    @Override
    public Long reset() {
        return resetLong();
    }

    @Override
    public Long aggregate(Long input) {
        reset = false;
        return aggregateLong(input);
    }

    public Long get() {
        return getAsLong();
    }

    @Override
    public long getAsLong() {
        return value;
    }

}
