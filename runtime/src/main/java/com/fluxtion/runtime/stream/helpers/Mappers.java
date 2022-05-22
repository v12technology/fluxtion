package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.Stateful;
import lombok.ToString;

import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoubleFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiIntFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleUnaryOperator;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableIntUnaryOperator;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableLongUnaryOperator;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableToIntFunction;

public interface Mappers {

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

    static <T> SerializableToIntFunction<T> count() {
        return Aggregates.counting().get()::aggregate;
    }

    static SerializableIntUnaryOperator countInt() {
        return Aggregates.counting().get()::increment;
    }

    static SerializableLongUnaryOperator countLong() {
        return Aggregates.counting().get()::increment;
    }

    static SerializableDoubleUnaryOperator countDouble() {
        return Aggregates.counting().get()::increment;
    }

    static SerializableIntUnaryOperator cumSumInt() {
        return Aggregates.intSum().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator cumSumDouble() {
        return Aggregates.doubleSum().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator cumSumLong() {
        return Aggregates.longSum().get()::aggregateLong;
    }

    static SerializableIntUnaryOperator minimumInt() {
        return new Min()::minInt;
    }

    static SerializableDoubleUnaryOperator minimumDouble() {
        return new Min()::minDouble;
    }

    static SerializableLongUnaryOperator minimumLong() {
        return new Min()::minLong;
    }

    static SerializableIntUnaryOperator maximumInt() {
        return new Max()::maxInt;
    }

    static SerializableDoubleUnaryOperator maximumDouble() {
        return new Max()::maxDouble;
    }

    static SerializableLongUnaryOperator maximumLong() {
        return new Max()::maxLong;
    }

    static CountNode newCountNode() {
        return new CountNode();
    }

    @ToString
    class CountNode implements Stateful<Integer>{
        int count;

        @OnTrigger
        public boolean increment() {
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }

        @Override
        public Integer reset() {
            count = 0;
            return count;
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

    class Max implements Stateful<Number> {
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

        @Override
        public Integer reset() {
            maxInt = 0;
            maxLong = 0;
            maxDouble = 0;
            return maxInt;
        }
    }


    class Min implements Stateful<Number> {
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

        @Override
        public Integer reset() {
            minInt = 0;
            minLong = 0;
            minDouble = 0;
            return minInt;
        }

    }

}
