package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.EventStream.DoubleEventStream;
import com.fluxtion.runtime.stream.EventStream.DoubleEventSupplier;
import com.fluxtion.runtime.stream.EventStream.EventSupplier;
import com.fluxtion.runtime.stream.EventStream.IntEventStream;
import com.fluxtion.runtime.stream.EventStream.IntEventSupplier;
import com.fluxtion.runtime.stream.EventStream.LongEventStream;
import com.fluxtion.runtime.stream.EventStream.LongEventSupplier;
import lombok.ToString;

public class WrappingEventSupplier<T, S extends EventStream<T>> implements EventSupplier<T> {

    private boolean triggered = false;
    protected final S inputEventStream;

    public WrappingEventSupplier(S inputEventStream) {
        this.inputEventStream = inputEventStream;
    }

    @OnTrigger
    public boolean triggered(){
        triggered = true;
        return true;
    }

    @AfterEvent
    public void afterEvent(){
        triggered = false;
    }

    @Initialise
    public void init(){
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
    public static class WrappingIntEventSupplier extends WrappingEventSupplier<Integer, IntEventStream> implements IntEventSupplier {

        public WrappingIntEventSupplier(IntEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public int getAsInt() {
            return inputEventStream.getAsInt();
        }
    }

    @ToString
    public static class WrappingDoubleEventSupplier extends WrappingEventSupplier<Double, DoubleEventStream> implements DoubleEventSupplier {

        public WrappingDoubleEventSupplier(DoubleEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public double getAsDouble() {
            return inputEventStream.getAsDouble();
        }
    }

    @ToString
    public static class WrappingLongEventSupplier extends WrappingEventSupplier<Long, LongEventStream> implements LongEventSupplier {

        public WrappingLongEventSupplier(LongEventStream inputEventStream) {
            super(inputEventStream);
        }

        @Override
        public long getAsLong() {
            return inputEventStream.getAsLong();
        }
    }

}
