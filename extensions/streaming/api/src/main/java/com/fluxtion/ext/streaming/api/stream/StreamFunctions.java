/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.Stateful.StatefulNumber;
import com.fluxtion.ext.streaming.api.WrappedList;
import java.util.ArrayDeque;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFunctions {

    public static <T> SerializableFunction<T, WrappedList<T>> collect() {
        return new ArrayListWrappedCollection<T>()::addItem;
    }

    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        return a / b;
    }

    public static double asDouble(double d) {
        return d;
    }

    public static int asInt(int d) {
        return d;
    }

    public static long asLong(long d) {
        return d;
    }

    public static <S> S asReference(S d) {
        return d;
    }

    public static class Count implements StatefulNumber<Count> {

        private int count;

        public int increment(Object o) {
            ++count;
            return count;
        }

        @Override
        @Initialise
        public void reset() {
            count = 0;
        }

        @Override
        public Number deduct(Count other, MutableNumber result) {
            this.count += other.count;
            result.set(this.count);
            return result;
        }

        @Override
        public Number combine(Count other, MutableNumber result) {
            this.count -= other.count;
            result.set(this.count);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(count);
            return result;
        }

    }

    public static class Sum implements StatefulNumber<Sum> {

        private double sum;

        public double addValue(Number val) {
            sum += val.doubleValue();
            return sum;
        }

        public int addInt(int val) {
            sum += val;
            return (int) sum;
        }

        public double addDouble(double val) {
            sum += val;
            return sum;
        }

        @Override
        @Initialise
        public void reset() {
            sum = 0;
        }

        @Override
        public Number combine(Sum other, MutableNumber result) {
            result.set(this.addValue(other.sum));
            return result;
        }

        @Override
        public Number deduct(Sum other, MutableNumber result) {
            this.sum -= other.sum;
            result.set(this.sum);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(sum);
            return result;
        }
    }

    public static class Max implements Stateful {

        private double max = 0;

        public double max(double val) {
            if (max < val) {
                max = val;
            }
            return max;
        }

        @Override
        @Initialise
        public void reset() {
            max = 0;
        }
    }

    public static class Min implements StatefulNumber<Min> {

        private double min = 0;
        private transient ArrayDeque<MutableNumber> history;

        public double min(double val) {
            if (min > val) {
                min = val;
            }
            return min;
        }

//        @Override
//        public void combine(Min other, MutableNumber result) {
//            StatefulNumber.super.combine(other, result); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public void deduct(Min other, MutableNumber result) {
//            StatefulNumber.super.deduct(other, result); //To change body of generated methods, choose Tools | Templates.
//        }
        @Override
        @Initialise
        public void reset() {
            min = 0;
        }
    }

    public static class Average implements StatefulNumber<Average> {

        private double sum;
        private double count;
        private double average = 0;

        public double addValue(double val) {
            sum += val;
            count++;
            average = (sum / count);
            return average;
        }

        @Override
        @Initialise
        public void reset() {
            sum = 0;
            count = 0;
            average = Double.NaN;
        }

        @Override
        public Number combine(Average other, MutableNumber result) {
            count += other.count - 1;
            result.set(this.addValue(other.sum));
            return result;
        }

        @Override
        public Number deduct(Average other, MutableNumber result) {
            count -= other.count + 1;
            result.set(this.addValue(-other.sum));
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(average);
            return result;
        }
    }

    public static class PercentDelta implements Stateful {

        public double previous = Double.NaN;
        private double result = 0;

        public double value(double newVal) {
            result = (newVal / previous);
            previous = newVal;
            return result;
        }

        @Override
        @Initialise
        public void reset() {
            previous = 0;
            result = 0;
        }
    }

    public static class Delta implements Stateful {

        public double previous = 0;
        private double result = 0;

        public double value(double newVal) {
            result = (newVal - previous);
            previous = newVal;
            return Double.isNaN(result) ? 0 : result;
        }

        @Override
        @Initialise
        public void reset() {
            previous = 0;
            result = 0;
        }
    }

    public static class Message {

        private final String outputMessage;

        public Message(String outputMessage) {
            this.outputMessage = outputMessage;
        }

        public String publishMessage(Object o) {
            return outputMessage;
        }
    }

}
