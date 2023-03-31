package com.fluxtion.runtime.stream.impl;

import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.DoubleFlowSupplier;
import com.fluxtion.runtime.stream.EventStream;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.FlowSupplier;
import com.fluxtion.runtime.stream.IntFlowSupplier;
import com.fluxtion.runtime.stream.LongFlowSupplier;
import lombok.ToString;

public class WrappingEventSupplier<T, S extends EventStream<T>> implements FlowSupplier<T> {

    private boolean triggered = false;
    protected final S inputEventStream;

    public WrappingEventSupplier(S inputEventStream) {
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
    public static class WrappingIntEventSupplier extends WrappingEventSupplier<Integer, IntEventStream> implements IntFlowSupplier {

        public WrappingIntEventSupplier(IntEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public int getAsInt() {
            return inputEventStream.getAsInt();
        }
    }

    @ToString
    public static class WrappingDoubleEventSupplier extends WrappingEventSupplier<Double, DoubleEventStream> implements DoubleFlowSupplier {

        public WrappingDoubleEventSupplier(DoubleEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public double getAsDouble() {
            return inputEventStream.getAsDouble();
        }
    }

    @ToString
    public static class WrappingLongEventSupplier extends WrappingEventSupplier<Long, LongEventStream> implements LongFlowSupplier {

        public WrappingLongEventSupplier(LongEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public long getAsLong() {
            return inputEventStream.getAsLong();
        }
    }

}