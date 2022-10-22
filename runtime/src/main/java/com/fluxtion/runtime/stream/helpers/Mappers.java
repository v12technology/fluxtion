package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.Stateful;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.TopNByValue;
import lombok.ToString;

import java.util.List;
import java.util.Map.Entry;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

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

    static <T> T identity(T in) {
        return in;
    }

    static <T> SerializableToIntFunction<T> count() {
        return Aggregates.countFactory().get()::aggregate;
    }

    static SerializableIntUnaryOperator countInt() {
        return Aggregates.countFactory().get()::increment;
    }

    static SerializableLongUnaryOperator countLong() {
        return Aggregates.countFactory().get()::increment;
    }

    static SerializableDoubleUnaryOperator countDouble() {
        return Aggregates.countFactory().get()::increment;
    }

    static SerializableIntUnaryOperator cumSumInt() {
        return Aggregates.intSumFactory().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator cumSumDouble() {
        return Aggregates.doubleSumFactory().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator cumSumLong() {
        return Aggregates.longSumFactory().get()::aggregateLong;
    }

    static SerializableIntUnaryOperator minimumInt() {
        return Aggregates.intMinFactory().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator minimumDouble() {
        return Aggregates.doubleMinFactory().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator minimumLong() {
        return Aggregates.longMinFactory().get()::aggregateLong;
    }

    static SerializableIntUnaryOperator maximumInt() {
        return Aggregates.intMaxFactory().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator maximumDouble() {
        return Aggregates.doubleMaxFactory().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator maximumLong() {
        return Aggregates.longMaxFactory().get()::aggregateLong;
    }

    static CountNode newCountNode() {
        return new CountNode();
    }

    static int parseInt(String input) {
        return Integer.parseInt(input);
    }

    static double parseDouble(String input) {
        return Double.parseDouble(input);
    }

    static long parseLong(String input) {
        return Long.parseLong(input);
    }

    static <K, V extends Comparable<V>> SerializableFunction<GroupBy<K, V>, List<Entry<K, V>>> topNByValue(int count) {
        return new TopNByValue(count)::filter;
    }

    static <K, V, T extends Comparable<T>> SerializableFunction<GroupBy<K, V>, List<Entry<K, V>>> topNByValue(int count, SerializableFunction<V, T> propertyAccessor) {
        TopNByValue topNByValue = new TopNByValue(count);
        topNByValue.comparing = propertyAccessor;
        return topNByValue::filter;
    }

    @ToString
    class CountNode implements Stateful<Integer> {
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
