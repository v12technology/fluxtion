package com.fluxtion.runtime.dataflow.helpers;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtime.time.Clock;

public interface Peekers {

    /**
     * logs the contents of a streamed node to console:
     * <ul>
     *     <li>{} is replaced the to string of the node being peeked</li>
     *     <li>%e is replaced with millisecond event time stamp</li>
     *     <li>%t is replaced with millisecond wall clock time stamp</li>
     *     <li>%p is replaced with millisecond process time stamp</li>
     *     <li>%de is replaced with millisecond event time stamp delta from start</li>
     *     <li>%dt is replaced with millisecond wall clock time stamp delta from start</li>
     *     <li>%dp is replaced with millisecond process time stamp delta from start</li>
     * </ul>
     */
    static <T> LambdaReflection.SerializableConsumer<T> console(String message) {
        return new TemplateMessage<>(message, null)::templateAndLogToConsole;
    }

    static <T, R> LambdaReflection.SerializableConsumer<T> console(String message, SerializableFunction<T, R> transform) {
        return new TemplateMessage<>(message, transform)::templateAndLogToConsole;
    }

    static void println(Object message) {
        System.out.println(message);
    }


    class TemplateMessage<T> {
        @Inject
        @NoTriggerReference
        public Clock clock;
        private final String message;
        private final SerializableFunction<T, ?> transformFunction;
        private transient long initialTime;

        public TemplateMessage(String message, SerializableFunction<T, ?> transformFunction) {
            this.message = message;
            this.transformFunction = transformFunction;
        }

        public TemplateMessage(String message) {
            this.message = message;
            this.transformFunction = null;
        }

        @Initialise
        public void initialise() {
            initialTime = clock.getWallClockTime();
        }

        public void templateAndLogToConsole(T input) {
            if (initialTime > clock.getWallClockTime()) {
                initialTime = clock.getWallClockTime();
            }
            String output = transformFunction == null ? input.toString() : transformFunction.apply(input).toString();
            System.out.println(
                    message.replace("{}", output).replace("%e", "" + clock.getEventTime())
                            .replace("{}", output).replace("%t", "" + clock.getWallClockTime())
                            .replace("{}", output).replace("%p", "" + clock.getProcessTime())
                            .replace("{}", output).replace("%de", "" + (clock.getEventTime() - initialTime))
                            .replace("{}", output).replace("%dt", "" + (clock.getWallClockTime() - initialTime))
                            .replace("{}", output).replace("%dp", "" + (clock.getProcessTime() - initialTime))
            );
        }
    }

}
