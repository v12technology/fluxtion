package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.feature.Experimental;

@Experimental
public interface PredictiveModel {

    double predictedValue();
}
