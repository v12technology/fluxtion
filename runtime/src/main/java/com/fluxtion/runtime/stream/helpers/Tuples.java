package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.stream.groupby.Tuple;
import lombok.Value;

public class Tuples {

    public static <F, S, TIN extends Tuple<? extends F, ? extends S>> SerializableFunction<TIN, Tuple<F, S>>
    replaceNull(F first, S second) {
        return new ReplaceNull<>(first, second)::replaceNull;
    }

    public static <F, S, R> SerializableFunction<Tuple<F, S>, R>
    mapTuple(SerializableBiFunction<F, S, R> tupleMapFunction) {
        return new MapTuple<>(tupleMapFunction)::mapTuple;
    }


    public static class ReplaceNull<F, S> {
        private final F firstValue;
        private final S secondValue;

        public ReplaceNull(
                @AssignToField("firstValue")
                F firstValue,
                @AssignToField("secondValue")
                S secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }

        public Tuple<F, S> replaceNull(Tuple<? extends F, ? extends S> in) {
            F first = in.getFirst() == null ? firstValue : in.getFirst();
            S second = in.getSecond() == null ? secondValue : in.getSecond();
            return new Tuple<>(first, second);
        }
    }

    @Value
    public static class MapTuple<F, S, R> {
        SerializableBiFunction<F, S, R> tupleMapFunction;

        public R mapTuple(Tuple<? extends F, ? extends S> tuple) {
            return tupleMapFunction.apply(tuple.getFirst(), tuple.getSecond());
        }

    }
}
