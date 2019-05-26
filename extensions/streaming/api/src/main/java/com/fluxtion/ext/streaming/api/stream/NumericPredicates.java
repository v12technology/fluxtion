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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;

/**
 *
 * @author gregp
 */
public class NumericPredicates {

    public static <T extends Double> SerializableFunction<T, Boolean> gt(double test) {
        return new NumericPredicates(test)::greaterThan;
    }
    
    public static <T extends Double> SerializableFunction<T, Boolean> deltaGt(double test) {
        return new NumericPredicates(test)::deltaGreaterThan;
    }

    public static <T extends Double> SerializableFunction<T, Boolean> lt(double test) {
        return new NumericPredicates(test)::lessThan;
    }
    
    public static <T extends Double> SerializableFunction<T, Boolean> deltaLt(double test) {
        return new NumericPredicates(test)::deltaLessThan;
    }

    public static <T extends Double> SerializableFunction<T, Boolean> inBand(double lowerLimit, double upperLimit) {
        return new NumericPredicates(lowerLimit, upperLimit)::inRange;
    }

    public static <T extends Double> SerializableFunction<T, Boolean> outsideBand(double lowerLimit, double upperLimit) {
        return new NumericPredicates(lowerLimit, upperLimit)::outsideRange;
    }

    public static <T extends Double> SerializableFunction<T, Boolean> positive() {
        return NumericPredicates::positiveInt;
    }

    public static <T extends Double> SerializableFunction<T, Boolean> negative() {
        return NumericPredicates::negativeNum;
    }
    
    public static <T extends Double> SerializableFunction<T, Boolean> trendUp(int minCount) {
        return new TrendingPredicates(minCount, true)::trending;
    }
    
    public static <T extends Double> SerializableFunction<T, Boolean> trendDown(int minCount) {
        return new TrendingPredicates(minCount, false)::trending;
    }

    public double doubleLimit_0 = Double.NaN;
    public double doubleLimit_1 = Double.NaN;
    private double previous = Double.NaN;
    private MutableNumber result = new MutableNumber();

    public NumericPredicates() {
    }

    public NumericPredicates(double limit_0) {
        this.doubleLimit_0 = limit_0;
    }

    public NumericPredicates(double limit_0, double limit_1) {
        this.doubleLimit_0 = limit_0;
        this.doubleLimit_1 = limit_1;
    }

    public static boolean positiveInt(double value) {
        return value > 0;
    }

    public static boolean negativeNum(double subject) {
        return subject < 0;
    }

    public boolean greaterThan(double subject) {
        return subject > doubleLimit_0;
    }

    public boolean lessThan(double subject) {
        return subject < doubleLimit_0;
    }

    public boolean inRange(double subject) {
        return subject > doubleLimit_0 & doubleLimit_1 >= subject;
    }

    public boolean outsideRange(double subject) {
        return !inRange(subject);
    }

    public boolean deltaGreaterThan(double newVal) {
        return delta(newVal).doubleValue > doubleLimit_0;
    }

    public boolean deltaLessThan(double newVal) {
        return delta(newVal).doubleValue < doubleLimit_0;
    }
    
    private MutableNumber delta(double val){
        result.setDoubleValue(val - previous);
        previous = val;
        return result;
    }
    
    
    public static class TrendingPredicates implements Stateful{

        private final int minTrendCount;
        private final boolean up;
        private double prev;
        private int count;

        public TrendingPredicates(int minTrendCount, boolean up) {
            this.minTrendCount = minTrendCount;
            this.up = up;
        }
        
        public boolean trending(double val){
            if((up & val > prev) | (!up & val < prev)){
                count++;
            }else{
                count = 0;
            }
            this.prev = val;
            return count >= minTrendCount;
        }
        
        @Override
        public void reset() {
            prev = Double.NaN;
            count = 0;
        }
        
    }
}
