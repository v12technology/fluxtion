package com.fluxtion.compiler.builder.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamHelper {
    static Object getSource(Object input) {
        Object returnValue = input;
        if (input instanceof EventStreamBuilder<?>) {
            EventStreamBuilder<?> eventStreamBuilder = (EventStreamBuilder<?>) input;
            returnValue = eventStreamBuilder.eventStream;
        } else if (input instanceof IntStreamBuilder) {
            IntStreamBuilder eventStreamBuilder = (IntStreamBuilder) input;
            returnValue = eventStreamBuilder.eventStream;
        } else if (input instanceof DoubleStreamBuilder) {
            DoubleStreamBuilder eventStreamBuilder = (DoubleStreamBuilder) input;
            returnValue = eventStreamBuilder.eventStream;
        } else if (input instanceof LongStreamBuilder) {
            LongStreamBuilder eventStreamBuilder = (LongStreamBuilder) input;
            returnValue = eventStreamBuilder.eventStream;
        }
        return returnValue;
    }

    static List<Object> getSourcesAsList(Object... inputs){
        ArrayList<Object> list = new ArrayList<>();
        if(inputs!=null){
            Arrays.stream(inputs).map(StreamHelper::getSource).forEach(list::add);
        }
        return list;
    }

}
