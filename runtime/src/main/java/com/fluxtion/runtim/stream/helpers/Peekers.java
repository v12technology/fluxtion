package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.partition.LambdaReflection;
import lombok.Value;

public interface Peekers {

    static <T> LambdaReflection.SerializableConsumer<T> console(String message){
        return new TemplateMessage<>(message)::templateAndLogToConsole;
    }

    static void println(Object message){
        System.out.println(message);
    }

    @Value
    class TemplateMessage<T> {
        String message;

        public void templateAndLogToConsole(T input){
            System.out.println(message.replace("{}", input.toString()));
        }
    }

}
