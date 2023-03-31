package com.fluxtion.runtime.stream;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface EventStream<R> extends Supplier<R> {

    default boolean hasDefaultValue() {
        return false;
    }

    interface IntEventStream extends EventStream<Integer>, IntSupplier {
        default Integer get() {
            return getAsInt();
        }
    }

    interface DoubleEventStream extends EventStream<Double>, DoubleSupplier {
        default Double get() {
            return getAsDouble();
        }
    }

    interface LongEventStream extends EventStream<Long>, LongSupplier {
        default Long get() {
            return getAsLong();
        }
    }

}
