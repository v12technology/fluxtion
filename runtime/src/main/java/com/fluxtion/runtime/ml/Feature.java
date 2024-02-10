package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.node.NamedNode;

import java.beans.Introspector;

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

}
