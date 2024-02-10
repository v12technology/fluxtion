package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableToDoubleFunction;

/**
 * uses a lambda {@link SerializableToDoubleFunction} to extract a property from an incoming FlowSupplier and use that as
 * the value for a feature.
 *
 * @param <T>
 */
public class PropertyToFeature<T> extends FlowSuppliedFeature<T> {
    private final SerializableToDoubleFunction<T> propertyExtractor;

    public static <T> PropertyToFeature<T> build(
            String name,
            FlowSupplier<T> dataFlowSupplier,
            SerializableToDoubleFunction<T> propertyExtractor) {
        return new PropertyToFeature<>(name, dataFlowSupplier, propertyExtractor);
    }

    public PropertyToFeature(
            @AssignToField("name") String name,
            @AssignToField("dataFlowSupplier") FlowSupplier<T> dataFlowSupplier,
            @AssignToField("propertyExtractor") SerializableToDoubleFunction<T> propertyExtractor) {
        super(name, name, dataFlowSupplier);
        this.propertyExtractor = propertyExtractor;
    }

    @Override
    public double extractFeatureValue() {
        return propertyExtractor.applyAsDouble(data());
    }
}

