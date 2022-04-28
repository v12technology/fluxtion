package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.stream.Stateful;
import lombok.ToString;
import lombok.Value;

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

    static SerializableToIntFunction count(){
        return new CountInt()::increment;
    }

    /**
     * more efficient version of count if counting stream of ints
     * @return
     */
    static SerializableIntUnaryOperator countInt(){
        return new CountInt()::increment;
    }

    static SerializableLongUnaryOperator countLong(){
        return new CountInt()::increment;
    }

    static SerializableDoubleUnaryOperator countDouble(){
        return new CountInt()::increment;
    }

    static SerializableIntUnaryOperator cumSumInt(){
        return new SumInt()::add;
    }

    static SerializableDoubleUnaryOperator cumSumDouble(){
        return new SumDouble()::add;
    }

    static SerializableLongUnaryOperator cumSumLong(){
        return new SumLong()::add;
    }

    static SerializableIntUnaryOperator minimumInt(){
        return new Min()::minInt;
    }

    static SerializableDoubleUnaryOperator minimumDouble(){
        return new Min()::minDouble;
    }

    static SerializableLongUnaryOperator minimumLong(){
        return new Min()::minLong;
    }

    static SerializableIntUnaryOperator maximumInt(){
        return new Max()::maxInt;
    }

    static SerializableDoubleUnaryOperator maximumDouble(){
        return new Max()::maxDouble;
    }

    static SerializableLongUnaryOperator maximumLong(){
        return new Max()::maxLong;
    }

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
    class SumInt implements Stateful<Integer> {
        int sum;

        public int add(int add) {
            sum += add;
            return sum;
        }

        public int getSum() {
            return sum;
        }

        @Override
        public Integer reset() {
            sum = 0;
            return sum;
        }
    }

    @ToString
    class SumDouble implements Stateful<Double> {
        double sum;

        public double add(double add) {
            sum += add;
            return sum;
        }

        @Override
        public Double reset() {
            sum = 0;
            return sum;
        }
    }

    @ToString
    class SumLong implements Stateful<Long>{
        long sum;

        public long add(long add) {
            sum += add;
            return sum;
        }

        @Override
        public Long reset() {
            sum = 0;
            return sum;
        }
    }

    class CountInt implements Stateful<Integer>{

        int count;

        public int increment(int i){
            count++;
            return count;
        }

        public int increment(long i){
            count++;
            return count;
        }


        public int increment(double i){
            count++;
            return count;
        }

        public <T> int increment(T add) {
            count++;
            return count;
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

    @ToString
    class Count implements Stateful<Integer>{
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

    class Max implements Stateful<Number>{
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


    class Min implements Stateful<Number>{
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
