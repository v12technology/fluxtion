package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.feature.Experimental;

@Experimental
public class MutableDouble {
    double value;

    public MutableDouble(double value) {
        this.value = value;
    }

    public MutableDouble() {
        this(Double.NaN);
    }

    void reset() {
        value = Double.NaN;
    }
}
