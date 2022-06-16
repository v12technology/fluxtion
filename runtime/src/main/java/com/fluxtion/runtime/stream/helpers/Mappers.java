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
        return Aggregates.intMin().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator minimumDouble() {
        return Aggregates.doubleMin().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator minimumLong() {
        return Aggregates.longMin().get()::aggregateLong;
    }

    static SerializableIntUnaryOperator maximumInt() {
        return Aggregates.intMax().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator maximumDouble() {
        return Aggregates.doubleMax().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator maximumLong() {
        return Aggregates.longMax().get()::aggregateLong;
    }

    static CountNode newCountNode() {
        return new CountNode();
    }

    static int parseInt(String input){
        return Integer.parseInt(input);
    }

    static double parseDouble(String input){
        return Double.parseDouble(input);
    }

    static long parseLong(String input){
        return Long.parseLong(input);
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

}
