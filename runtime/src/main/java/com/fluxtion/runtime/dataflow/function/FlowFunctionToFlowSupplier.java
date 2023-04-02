package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.DoubleFlowSupplier;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowSupplier;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowSupplier;
import lombok.ToString;

public class FlowFunctionToFlowSupplier<T, S extends FlowFunction<T>> implements FlowSupplier<T> {

    private boolean triggered = false;
    protected final S inputEventStream;

    public FlowFunctionToFlowSupplier(S inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

    @OnTrigger
    public boolean triggered() {
        triggered = true;
        return true;
    }

    @AfterEvent
    public void afterEvent() {
        triggered = false;
    }

    @Initialise
    public void init() {
        triggered = false;
    }

    @Override
    public T get() {
        return inputEventStream.get();
    }

    @Override
    public boolean hasChanged() {
        return triggered;
    }


    @ToString
    public static class IntFlowFunctionToFlowSupplier extends FlowFunctionToFlowSupplier<Integer, IntFlowFunction> implements IntFlowSupplier {

        public IntFlowFunctionToFlowSupplier(IntFlowFunction inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public int getAsInt() {
            return inputEventStream.getAsInt();
        }
    }

    @ToString
    public static class DoubleFlowFunctionToFlowSupplier extends FlowFunctionToFlowSupplier<Double, DoubleFlowFunction> implements DoubleFlowSupplier {

        public DoubleFlowFunctionToFlowSupplier(DoubleFlowFunction inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public double getAsDouble() {
            return inputEventStream.getAsDouble();
        }
    }

    @ToString
    public static class LongFlowFunctionToFlowSupplier extends FlowFunctionToFlowSupplier<Long, LongFlowFunction> implements LongFlowSupplier {

        public LongFlowFunctionToFlowSupplier(LongFlowFunction inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public long getAsLong() {
            return inputEventStream.getAsLong();
        }
    }

}
