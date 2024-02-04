package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.feature.Experimental;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Experimental
public class Calibration {
    private String featureIdentifier;
    private Class<? extends Feature> featureClass;
    private int featureVersion;
    private double co_efficient;
    private double weight;

    public String getFeatureIdentifier() {
        return featureIdentifier == null ? featureClass.getSimpleName() : featureIdentifier;
    }
}
