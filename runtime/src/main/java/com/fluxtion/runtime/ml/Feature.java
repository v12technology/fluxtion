package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.runtime.partition.LambdaReflection;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;

@Experimental
public interface Feature extends NamedNode, @ExportService CalibrationProcessor {

    default String identifier() {
        return getClass().getSimpleName();
    }

    @Override
    default String getName() {
        return Introspector.decapitalize(identifier());
    }

    double value();

    @SafeVarargs
    static <T> List<Feature> include(FlowSupplier<T> inputDataFlow, LambdaReflection.SerializableToDoubleFunction<T>... featureExtractors) {
        List<Feature> featureList = new ArrayList<>(featureExtractors.length);
        for (LambdaReflection.SerializableToDoubleFunction<T> featureExtractor : featureExtractors) {
            featureList.add(
                    PropertyToFeature.build(featureExtractor.method().getName(), inputDataFlow, featureExtractor)
            );
        }
        return featureList;
    }

}
