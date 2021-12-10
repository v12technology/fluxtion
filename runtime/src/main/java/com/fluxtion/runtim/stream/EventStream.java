package com.fluxtion.runtim.stream;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public interface EventStream <T> {

    T get();

    interface IntEventStream extends TriggeredEventStream<Integer>, IntSupplier{}
    interface DoubleEventStream extends TriggeredEventStream<Integer>, DoubleSupplier {}
    interface LongEventStream extends TriggeredEventStream<Integer>, LongSupplier {}
}
