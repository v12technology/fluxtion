/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.group;

import com.fluxtion.ext.streaming.api.Stateful.StatefulNumber;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;

/**
 * Math functions for use with GroupBy.
 *
 * @author Greg Higgins
 */
public class AggregateFunctions {

    public static double calcSum(double newValue, double oldSum) {
        return newValue + oldSum;
    }

    public static double set(double newValue, double oldSum) {
        return newValue;
    }

    public static int count(Object newValue, int oldValue) {
        oldValue++;
        return oldValue;
    }

    public static double minimum(double newValue, double oldValue) {
        return Math.min(newValue, oldValue);
    }

    public static double maximum(double newValue, double oldValue) {
        return Math.max(newValue, oldValue);
    }

    public static class AggregateAverage implements StatefulNumber<AggregateAverage> {

        private int count;
        private double sum;
        private double currentValue;

        public double calcAverage(double newValue, double oldAverage) {
            count++;
            sum += newValue;
            currentValue = sum / count;
            return currentValue;
        }

        @Override
        public void reset() {
            count = 0;
            sum = 0;
            currentValue = Double.NaN;
        }

        @Override
        public Number combine(AggregateAverage other, MutableNumber result) {
            count += other.count;
            sum += other.sum;
            currentValue = sum / count;
            result.set(currentValue);
            return result;
        }

        @Override
        public Number deduct(AggregateAverage other, MutableNumber result) {
            count -= other.count;
            sum -= other.sum;
            currentValue = sum / count;
            result.set(currentValue);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(currentValue);
            return result;
        }
    }

    public static class AggregateSum implements StatefulNumber<AggregateSum> {

        private double sum;

        public double calcCumSum(double newValue, double oldAverage) {
            sum += newValue;
            return sum;
        }

        @Override
        public void reset() {
            sum = 0;
        }

        @Override
        public Number combine(AggregateSum other, MutableNumber result) {
            sum += other.sum;
            result.set(sum);
            return result;
        }

        @Override
        public Number deduct(AggregateSum other, MutableNumber result) {
            sum -= other.sum;
            result.set(sum);
            return result;
        }

        @Override
        public Number currentValue(MutableNumber result) {
            result.set(sum);
            return result;
        }

    }
}
