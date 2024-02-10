package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.NoPropagateFunction;

import java.util.List;

/**
 * Accepts {@link Calibration} messages for processing and applying to features/models.
 */
public interface CalibrationProcessor {

    /**
     * A list of calibrations that are to be applied to the features. A calibration has a featureIdentifier field
     * that receivers can use to filter the incoming calibration and apply as necessary.
     *
     * @param calibration list of calibrations to apply
     * @return change notification for propagation
     */
    boolean setCalibration(List<Calibration> calibration);

    /**
     * Reset all calibrations to 1
     *
     * @return change notification for propagation
     */
    @NoPropagateFunction
    default boolean resetToOne() {
        return false;
    }

    /**
     * Reset all calibrations to o
     *
     * @return change notification for propagation
     */
    @NoPropagateFunction
    default boolean resetToZero() {
        return false;
    }
}
