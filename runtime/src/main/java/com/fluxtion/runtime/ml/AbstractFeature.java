package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.feature.Experimental;

import java.util.List;

@Experimental
public abstract class AbstractFeature implements Feature, CalibrationProcessor {

    protected double co_efficient;
    protected double weight;
    protected double value;

    @Initialise
    public void init() {
        co_efficient = 0;
        weight = 0;
        value = 0;
    }

    @Override
    public boolean setCalibration(List<Calibration> calibrations) {
        for (int i = 0, calibrationsSize = calibrations.size(); i < calibrationsSize; i++) {
            Calibration calibration = calibrations.get(i);
            if (calibration.getFeatureIdentifier().equals(identifier())) {
                co_efficient = calibration.getCo_efficient();
                weight = calibration.getWeight();
                return true;
            }
        }
        return false;
    }

    @Override
    public double value() {
        return value;
    }

}
