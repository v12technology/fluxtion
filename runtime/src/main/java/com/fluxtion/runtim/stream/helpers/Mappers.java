package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableDoubleFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableIntFunction;
import com.fluxtion.runtim.partition.LambdaReflection.SerializableLongFunction;
import lombok.ToString;
import lombok.Value;

import static com.fluxtion.runtim.partition.LambdaReflection.SerializableIntUnaryOperator;

public interface Mappers {

    static Count count(){
        return new Count();
    }

    static SerializableFunction<?, String> onjToMessage(String message){
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableIntFunction<String> intToMessage(String message){
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableDoubleFunction<String> doubleToMessage(String message){
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableLongFunction<String> longToMessage(String message){
        return new ConstantStringMapper(message)::toMessage;
    }

    SerializableIntUnaryOperator SUM_INT = new Sum()::add;

    @Value
    class ConstantStringMapper{
        String message;

        public String toMessage(Object value){
            return message;
        }

        public String toMessage(int value){
            return message;
        }

        public String toMessage(double value){
            return message;
        }

        public String toMessage(long value){
            return message;
        }
    }

   class Sum {
        int sum;

        public int add(int add) {
            sum += add;
            return sum;
        }
    }

    @ToString
    class Count {
        int count;

        @OnEvent
        public boolean increment(){
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }
    }

}
