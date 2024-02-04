package com.fluxtion.runtime.ml;

import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.node.NamedNode;

@Experimental
public interface Feature extends NamedNode, @ExportService CalibrationProcessor {

    default String identifier() {
        return getClass().getSimpleName();
    }

    default int version() {
        return 0;
    }

    @Override
    default String getName() {
        return identifier() + "_" + version();
    }

    double value();

}
