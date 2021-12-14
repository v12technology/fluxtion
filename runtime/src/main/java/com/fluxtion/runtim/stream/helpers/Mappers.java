package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.annotations.OnEvent;
import lombok.ToString;
import lombok.Value;

import static com.fluxtion.runtim.partition.LambdaReflection.*;

public interface Mappers {

    SerializableIntUnaryOperator SUM_INT = new SumInt()::add;
    SerializableDoubleUnaryOperator SUM_DOUBLE = new SumDouble()::add;
    SerializableLongUnaryOperator SUM_LONG = new SumLong()::add;

    SerializableIntUnaryOperator MIN_INT = new Min()::minInt;
    SerializableDoubleUnaryOperator MIN_DOUBLE = new Min()::minDouble;
    SerializableLongUnaryOperator MIN_LONG = new Min()::minLong;

    SerializableIntUnaryOperator MAX_INT = new Max()::maxInt;
    SerializableDoubleUnaryOperator MAX_DOUBLE = new Max()::maxDouble;
    SerializableLongUnaryOperator MAX_LONG = new Max()::maxLong;

    SerializableBiIntFunction ADD_INTS = Mappers::addInts;
    SerializableBiDoubleFunction ADD_DOUBLES = Mappers::addDoubles;
    SerializableBiLongFunction ADD_LONGS = Mappers::addLongs;

    SerializableBiIntFunction SUBTRACT_INTS = Mappers::subtractInts;
    SerializableBiDoubleFunction SUBTRACT_DOUBLES = Mappers::subtractDoubles;
    SerializableBiLongFunction SUBTRACT_LONGS = Mappers::subtractLongs;

    SerializableBiIntFunction MULTIPLY_INTS = Mappers::multiplyInts;
    SerializableBiDoubleFunction MULTIPLY_DOUBLES = Mappers::multiplyDoubles;
    SerializableBiLongFunction MULTIPLY_LONGS = Mappers::multiplyLongs;

    SerializableBiIntFunction DIVIDE_INTS = Mappers::divideInts;
    SerializableBiLongFunction DIVIDE_LONGS = Mappers::divideLongs;
    SerializableBiDoubleFunction DIVIDE_DOUBLES = Mappers::divideDoubles;

    static Count newCount() {
        return new Count();
    }

    static SerializableFunction<?, String> onjToMessage(String message) {
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableIntFunction<String> intToMessage(String message) {
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableDoubleFunction<String> doubleToMessage(String message) {
        return new ConstantStringMapper(message)::toMessage;
    }

    static SerializableLongFunction<String> longToMessage(String message) {
        return new ConstantStringMapper(message)::toMessage;
    }


    @Value
    class ConstantStringMapper {
        String message;

        public String toMessage(Object value) {
            return message;
        }

        public String toMessage(int value) {
            return message;
        }

        public String toMessage(double value) {
            return message;
        }

        public String toMessage(long value) {
            return message;
        }
    }

    //add
    static int addInts(int a, int b) {
        return a + b;
    }

    static double addDoubles(double a, double b) {
        return a + b;
    }

    static long addLongs(long a, long b) {
        return a + b;
    }
    
    //subtract
    static int subtractInts(int a, int b) {
        return a - b;
    }

    static double subtractDoubles(double a, double b) {
        return a - b;
    }

    static long subtractLongs(long a, long b) {
        return a - b;
    }

    //multiply
    static int multiplyInts(int a, int b) {
        return a * b;
    }

    static double multiplyDoubles(double a, double b) {
        return a * b;
    }

    static long multiplyLongs(long a, long b) {
        return a * b;
    }

    //divide
    static int divideInts(int a, int b) {
        return a / b;
    }

    static double divideDoubles(double a, double b) {
        return a / b;
    }

    static long divideLongs(long a, long b) {
        return a / b;
    }
    
    @ToString
    class SumInt {
        int sum;

        public int add(int add) {
            sum += add;
            return sum;
        }
    }

    @ToString
    class SumDouble {
        double sum;

        public double add(double add) {
            sum += add;
            return sum;
        }
    }

    @ToString
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
        public boolean increment() {
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }
    }

    class Max {
        int maxInt;
        long maxLong;
        double maxDouble;

        public int maxInt(int input) {
            maxInt = Math.max(maxInt, input);
            return maxInt;
        }


        public double maxDouble(double input) {
            maxDouble = Math.max(maxDouble, input);
            return maxDouble;
        }

        public long maxLong(long input) {
            maxLong = Math.max(maxLong, input);
            return maxLong;
        }
    }


    class Min {
        int minInt;
        long minLong;
        double minDouble;

        public int minInt(int input) {
            minInt = Math.min(minInt, input);
            return minInt;
        }

        public double minDouble(double input) {
            minDouble = Math.min(minDouble, input);
            return minDouble;
        }

        public long minLong(long input) {
            minLong = Math.min(minLong, input);
            return minLong;
        }
    }

}
