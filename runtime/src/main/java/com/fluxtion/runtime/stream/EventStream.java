package com.fluxtion.runtime.stream;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public interface EventStream <R> {

    R get();

    default boolean hasDefaultValue(){
        return false;
    }

    interface IntEventStream extends TriggeredEventStream<Integer>, IntSupplier{
        default Integer get(){
            return getAsInt();
        }
    }
    interface DoubleEventStream extends TriggeredEventStream<Double>, DoubleSupplier {
        default Double get(){
            return getAsDouble();
        }
    }
    interface LongEventStream extends TriggeredEventStream<Long>, LongSupplier {
        default Long get(){
            return getAsLong();
        }
    }


}
