package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.Stateful;
import com.fluxtion.runtime.stream.groupby.GroupBy;
import com.fluxtion.runtime.stream.groupby.GroupByCollection;
import com.fluxtion.runtime.stream.groupby.GroupByStreamed;
import com.fluxtion.runtime.stream.groupby.TopNByValue;
import com.fluxtion.runtime.stream.groupby.Tuple;
import lombok.ToString;

import java.util.List;
import java.util.Map.Entry;

import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiDoubleFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiIntFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableBiLongFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleUnaryOperator;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
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

    //AVERAGE
    static SerializableIntUnaryOperator averageInt() {
        return Aggregates.intAverageFactory().get()::aggregateInt;
    }

    static SerializableDoubleUnaryOperator averageDouble() {
        return Aggregates.doubleAverageFactory().get()::aggregateDouble;
    }

    static SerializableLongUnaryOperator averageLong() {
        return Aggregates.longAverageFactory().get()::aggregateLong;
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

    static <K, V extends Comparable<V>> SerializableFunction<GroupByStreamed<K, V>, List<Entry<K, V>>> topNByValue(int count) {
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

    //cast
    static double int2Double(int in) {
        return in;
    }

    static long int2Long(int in) {
        return in;
    }

    static double long2Double(long in) {
        return in;
    }

    static int long2Int(long in) {
        return (int) in;
    }

    static int double2Int(double in) {
        return (int) in;
    }

    static long double2Long(double in) {
        return (long) in;
    }

    //GroupBy
    static <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> innerJoin(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (leftGroupBy != null && rightGroupBY != null) {
            leftGroupBy.map().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY.map().get(e.getKey());
                if (value2 != null) {
                    joinedGroup.map().put(e.getKey(), new Tuple<>(e.getValue(), value2));
                }
            });
        }
        return joinedGroup;
    }

    static <K1, V1, K2 extends K1, V2> GroupByStreamed<K1, Tuple<V1, V2>> innerJoin(
            GroupByStreamed<K1, V1> leftGroupBy, GroupByStreamed<K2, V2> rightGroupBY) {
        GroupByStreamed<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (leftGroupBy != null && rightGroupBY != null) {
            leftGroupBy.map().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY.map().get(e.getKey());
                if (value2 != null) {
                    joinedGroup.map().put(e.getKey(), new Tuple<>(e.getValue(), value2));
                }
            });
        }
        return joinedGroup;
    }

    static <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> outerJoin(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (leftGroupBy != null) {
            leftGroupBy.map().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY == null ? null : rightGroupBY.map().get(e.getKey());
                joinedGroup.map().put(e.getKey(), new Tuple<>(e.getValue(), value2));
            });
        }
        if (rightGroupBY != null) {
            rightGroupBY.map().entrySet().forEach(e -> {
                V1 value1 = leftGroupBy == null ? null : leftGroupBy.map().get(e.getKey());
                joinedGroup.map().put(e.getKey(), new Tuple<>(value1, e.getValue()));
            });
        }
        return joinedGroup;
    }

    static <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> leftJoin(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (leftGroupBy != null) {
            leftGroupBy.map().entrySet().forEach(e -> {
                V2 value2 = rightGroupBY == null ? null : rightGroupBY.map().get(e.getKey());
                joinedGroup.map().put(e.getKey(), new Tuple<>(e.getValue(), value2));
            });
        }
        return joinedGroup;
    }

    static <K1, V1, K2 extends K1, V2> GroupBy<K1, Tuple<V1, V2>> rightJoin(
            GroupBy<K1, V1> leftGroupBy, GroupBy<K2, V2> rightGroupBY) {
        GroupBy<K1, Tuple<V1, V2>> joinedGroup = new GroupByCollection<>();
        if (rightGroupBY != null) {
            rightGroupBY.map().entrySet().forEach(e -> {
                V1 value1 = leftGroupBy == null ? null : leftGroupBy.map().get(e.getKey());
                joinedGroup.map().put(e.getKey(), new Tuple<>(value1, e.getValue()));
            });
        }
        return joinedGroup;
    }

    //TODO hack for serialisation, generic types not supported in BinaryMapToRefEventStream
    static GroupBy innerJoin(Object leftGroup, Object rightGroup) {
        return Mappers.innerJoin((GroupBy) leftGroup, (GroupBy) rightGroup);
    }

    static GroupBy outerJoin(Object leftGroup, Object rightGroup) {
        return Mappers.outerJoin((GroupBy) leftGroup, (GroupBy) rightGroup);
    }

    static GroupBy leftJoin(Object leftGroup, Object rightGroup) {
        return Mappers.leftJoin((GroupBy) leftGroup, (GroupBy) rightGroup);
    }

    static GroupBy rightJoin(Object leftGroup, Object rightGroup) {
        return Mappers.rightJoin((GroupBy) leftGroup, (GroupBy) rightGroup);
    }
}
