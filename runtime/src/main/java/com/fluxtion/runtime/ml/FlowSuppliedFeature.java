package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowSupplier;

/**
 * Inherit this class and add to {@link PredictiveLinearRegressionModel} to calculate a linear regression.
 * The subclass implements {@link #extractFeatureValue()}, all the event triggering and co_efficient adjustments are
 * implemented in this class.
 *
 * @param <T>
 */
public abstract class FlowSuppliedFeature<T> extends AbstractFeature implements CalibrationProcessor {
    protected final FlowSupplier<T> dataFlowSupplier;

    public FlowSuppliedFeature(FlowSupplier<T> dataFlowSupplier) {
        this.dataFlowSupplier = dataFlowSupplier;
    }

    public FlowSuppliedFeature(
            @AssignToField("name") String name,
            @AssignToField("identifier") String identifier,
            FlowSupplier<T> dataFlowSupplier) {
        super(name, identifier);
        this.dataFlowSupplier = dataFlowSupplier;
    }

    @OnTrigger
    public boolean calculateFeature() {
        double newValue = extractFeatureValue() * co_efficient * weight;
        boolean changed = newValue != value;
        value = newValue;
        return changed;
    }

    /**
     * Implement this method to extract the value of this feature as a double
     *
     * @return the feature value
     */
    public abstract double extractFeatureValue();

    protected T data() {
        return dataFlowSupplier.get();
    }
}
