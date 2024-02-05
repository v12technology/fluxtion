package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.feature.Experimental;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Experimental
public class PredictiveLinearRegressionModel implements PredictiveModel, @ExportService CalibrationProcessor {

    private transient final Map<Feature, MutableDouble> valueMap;
    private final Feature[] features;
    private double prediction = Double.NaN;

    public PredictiveLinearRegressionModel(Feature... features) {
        this.features = features;
        this.valueMap = new IdentityHashMap<>(features.length);
        for (Feature feature : features) {
            valueMap.put(feature, new MutableDouble(0));
        }
    }

    @Initialise
    public void init() {
        prediction = Double.NaN;
    }

    @Override
    @NoPropagateFunction
    public boolean setCalibration(List<Calibration> calibrations) {
        double previousValue = prediction;
        prediction = 0;
        for (Feature feature : features) {
            prediction += feature.value();
        }
        return previousValue != prediction | Double.isNaN(previousValue) != Double.isNaN(prediction);
    }

    @OnParentUpdate
    public void featureUpdated(Feature featureUpdated) {
        MutableDouble previousValue = valueMap.get(featureUpdated);
        double newValue = featureUpdated.value();
        prediction += newValue - previousValue.value;
        previousValue.value = newValue;
    }

    @OnTrigger
    public boolean calculateInference() {
        return true;
    }


    @Override
    public double predictedValue() {
        return prediction;
    }
}
