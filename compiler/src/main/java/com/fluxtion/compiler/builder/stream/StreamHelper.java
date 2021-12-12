package com.fluxtion.compiler.builder.stream;

import com.fluxtion.runtim.partition.LambdaReflection;
import com.fluxtion.runtim.stream.helpers.DefaultValue;

class StreamHelper {
    static Object getSource(Object input) {
        Object returnValue = input;
        if (input instanceof EventStreamBuilder<?>) {
            EventStreamBuilder<?> eventStreamBuilder = (EventStreamBuilder<?>) input;
            returnValue = eventStreamBuilder.eventStream;
        } else if (input instanceof IntStreamBuilder<?, ?>) {
            IntStreamBuilder<?, ?> eventStreamBuilder = (IntStreamBuilder<?, ?>) input;
            returnValue = eventStreamBuilder.eventStream;
        }
        return returnValue;
    }

}
