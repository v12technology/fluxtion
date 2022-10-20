package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtime.stream.EventStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamHelper {
    static Object getSource(Object input) {
        Object returnValue = input;
        if (input instanceof EventStream.EventSupplierAccessor) {
            returnValue = ((EventStream.EventSupplierAccessor) input).getEventSupplier();
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
