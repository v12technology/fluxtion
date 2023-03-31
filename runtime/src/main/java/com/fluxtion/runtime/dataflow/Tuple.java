package com.fluxtion.runtime.dataflow;

import lombok.Value;

@Value
public class Tuple<F, S> {
    F first;
    S second;
}
