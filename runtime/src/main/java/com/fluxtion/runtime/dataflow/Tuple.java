package com.fluxtion.runtime.dataflow;

import com.fluxtion.runtime.dataflow.groupby.MutableTuple;

public interface Tuple<F, S> {
    static <F, S> Tuple<F, S> build(F first, S second) {
        return new MutableTuple<>(first, second);
    }

    F getFirst();

    S getSecond();
}
