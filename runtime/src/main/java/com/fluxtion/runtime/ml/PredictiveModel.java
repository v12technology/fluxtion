package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.feature.Experimental;

import java.util.List;

@Experimental
public interface PredictiveModel {

    double predictedValue();

    List<Feature> features();
}
