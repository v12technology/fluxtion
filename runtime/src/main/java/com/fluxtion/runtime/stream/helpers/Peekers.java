package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.time.Clock;

public interface Peekers {

    /**
     * logs the contents of a streamed node to console:
     * <ul>
     *     <li>{} is replaced the to string of the node being peeked</li>
     *     <li>%t is replaced with millisecond event time stamp</li>
     * </ul>
     *
     */
    static <T> LambdaReflection.SerializableConsumer<T> console(String message) {
        return new TemplateMessage<>(message)::templateAndLogToConsole;
    }

    static void println(Object message) {
        System.out.println(message);
    }


    class TemplateMessage<T> {
        @Inject
        @NoTriggerReference
        public Clock clock;
        private final String message;

        public TemplateMessage(String message) {
            this.message = message;
        }

        public void templateAndLogToConsole(T input) {
            System.out.println(message.replace("{}", input.toString()).replace("%t", "" + clock.getEventTime()));
        }
    }

}
