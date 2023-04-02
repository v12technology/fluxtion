package com.fluxtion.compiler.builder.dataflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamHelper {
    static Object getSource(Object input) {
        Object returnValue = input;
        if (input instanceof FlowDataSupplier) {
            returnValue = ((FlowDataSupplier) input).flowSupplier();
        }
        return returnValue;
    }

    static List<Object> getSourcesAsList(Object... inputs) {
        ArrayList<Object> list = new ArrayList<>();
        if (inputs != null) {
            Arrays.stream(inputs).map(StreamHelper::getSource).forEach(list::add);
        }
        return list;
    }

}
