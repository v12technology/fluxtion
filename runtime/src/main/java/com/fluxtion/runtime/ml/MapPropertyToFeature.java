package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableToDoubleFunction;

/**
 * uses a lambda {@link SerializableToDoubleFunction} to extract a property from an incoming FlowSupplier
 * then applies a mapping function to supply a double as the value for the feature.
 *
 * @param <T>
 */
public class MapPropertyToFeature<T, S> extends FlowSuppliedFeature<T> {
    private final SerializableFunction<T, S> propertyExtractor;
    private final SerializableToDoubleFunction<S> propertyMapper;

    public MapPropertyToFeature(
            @AssignToField("name") String name,
            @AssignToField("dataFlowSupplier") FlowSupplier<T> dataFlowSupplier,
            @AssignToField("propertyExtractor") SerializableFunction<T, S> propertyExtractor,
            @AssignToField("propertyMapper") SerializableToDoubleFunction<S> propertyMapper
    ) {
        super(name, name, dataFlowSupplier);
        this.propertyExtractor = propertyExtractor;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public double extractFeatureValue() {
        return propertyMapper.applyAsDouble(propertyExtractor.apply(data()));
    }
}

