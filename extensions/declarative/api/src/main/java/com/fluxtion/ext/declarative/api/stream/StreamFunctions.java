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
package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.declarative.api.Stateful;
import com.fluxtion.ext.declarative.api.numeric.MutableNumber;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFunctions {

    public static <T> LambdaReflection.SerializableFunction<T, String> message(String message) {
        return new Message(message)::publishMessage;
    }

    public static <T extends Number> LambdaReflection.SerializableFunction<T, Number> percentDelta() {
        return new PercentDelta()::value;
    }

    public static <T extends Number> LambdaReflection.SerializableFunction<T, Number> max() {
        return new Max()::max;
    }

    public static class Count implements Stateful {

        private int count;
        private MutableNumber num = new MutableNumber();

        public Number increment(Object o) {
            num.set(++count);
            return num;
        }

        @Override
        public void reset() {
            count = 0;
            num.set(count);
        }
    }

    public static class Sum implements Stateful {

        private double sum;
        private MutableNumber num = new MutableNumber();

        public Number addValue(Number val) {
            sum += val.doubleValue();
            num.set(sum);
            return num;
        }

        @Override
        public void reset() {
            sum = 0;
            num.set(sum);
        }
    }

    public static class Max implements Stateful {

        private MutableNumber max = new MutableNumber();

        public Number max(Number val) {
            if (max.doubleValue() < val.doubleValue()) {
                max.set(val.doubleValue());
            }
            return max;
        }

        @Override
        public void reset() {
            max.set(0);
        }
    }

    public static class Average implements Stateful {

        private double sum;
        private double count;
        private MutableNumber average = new MutableNumber();

        public Number addValue(Number val) {
            sum += val.doubleValue();
            count++;
            average.setDoubleValue(sum / count);
            return average;
        }

        @Override
        public void reset() {
            sum = 0;
            count = 0;
            average.set(0);
        }
    }

    public static class PercentDelta implements Stateful {

        public double previous = Double.NaN;
        private MutableNumber result = new MutableNumber();

        public Number value(Number newVal) {
            result.set(newVal.doubleValue() / previous);
            previous = newVal.doubleValue();
            return result;
        }

        @Override
        public void reset() {
            previous = 0;
            result.set(0);
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
