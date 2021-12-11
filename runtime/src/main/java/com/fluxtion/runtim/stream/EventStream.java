package com.fluxtion.runtim.stream;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public interface EventStream <R> {

    R get();

    interface IntEventStream extends TriggeredEventStream<Integer>, IntSupplier{}
    interface DoubleEventStream extends TriggeredEventStream<Double>, DoubleSupplier {}
    interface LongEventStream extends TriggeredEventStream<Long>, LongSupplier {}


}
