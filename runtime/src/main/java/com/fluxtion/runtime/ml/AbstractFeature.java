package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoPropagateFunction;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.annotations.feature.Experimental;

import java.beans.Introspector;
import java.util.List;

@Experimental
public abstract class AbstractFeature implements Feature, @ExportService CalibrationProcessor {

    private final transient String name;
    private final transient String identifier;
    protected double co_efficient;
    protected double weight;
    protected double value;

    public AbstractFeature() {
        identifier = getClass().getSimpleName();
        name = Introspector.decapitalize(identifier);
    }

    public AbstractFeature(
            @AssignToField("name") String name,
            @AssignToField("identifier") String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    @Initialise
    public void init() {
        co_efficient = 0;
        weight = 0;
        value = 0;
    }

    @Override
    @NoPropagateFunction
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

    public double co_efficient() {
        return co_efficient;
    }

    public double weight() {
        return weight;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }
}
