package com.fluxtion.runtime.stream.groupby;

import lombok.Value;

@Value
public class Tuple<F, S> {
    F first;
    S second;
}
