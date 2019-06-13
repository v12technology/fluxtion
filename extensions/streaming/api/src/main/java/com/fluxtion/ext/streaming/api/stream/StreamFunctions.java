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

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.streaming.api.Stateful;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFunctions {

    public static <T> LambdaReflection.SerializableFunction<T, String> message(String message) {
        return new Message(message)::publishMessage;
    }

    public static <T extends Double> LambdaReflection.SerializableFunction<T, Number> toDouble() {
        return StreamFunctions::asDouble;
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
    
    public static double asDouble(double d){
        return d;
    } 

    public static class IntCount implements Stateful {

        private int count = 0;

        @Override
        public void reset() {
            count = 0;
        }

        public int increment(Object o) {
            return ++count;
        }

    }

    public static class Count implements Stateful {

        private int count;

        public int increment(Object o) {
            ++count;
            return count;
        }

        @Override
        public void reset() {
            count = 0;
        }
    }

    public static class Sum implements Stateful {

        private double sum;

        public double addValue(double val) {
            sum += val;
            return sum;
        }

        @Override
        public void reset() {
            sum = 0;
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
        public void reset() {
            max = 0;
        }
    }

    public static class Min implements Stateful {

        private double min = 0;

        public double min(double val) {
            if (min > val) {
                min = val;
            }
            return min;
        }

        @Override
        public void reset() {
            min = 0;
        }
    }

    public static class Average implements Stateful {

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
        public void reset() {
            sum = 0;
            count = 0;
            average = 0;
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
