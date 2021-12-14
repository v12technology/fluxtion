package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.annotations.OnEvent;
import lombok.ToString;
import lombok.Value;

import static com.fluxtion.runtim.partition.LambdaReflection.*;

public interface Mappers {

    SerializableDoubleUnaryOperator SUM_DOUBLE = new SumDouble()::add;
    SerializableLongUnaryOperator SUM_LONG = new SumLong()::add;
    SerializableIntUnaryOperator SUM_INT = new SumInt()::add;

    static Count newCount(){
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

    class SumInt {
        int sum;

        public int add(int add) {
            sum += add;
            return sum;
        }
    }

    class SumDouble {
        double sum;

        public double add(double add) {
            sum += add;
            return sum;
        }
    }

    class SumLong {
        long sum;

        public long add(long add) {
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
