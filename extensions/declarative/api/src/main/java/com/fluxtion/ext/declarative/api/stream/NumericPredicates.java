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
package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;

/**
 *
 * @author gregp
 */
public class NumericPredicates {

    public static SerializableFunction<Number, Boolean> gt(double test) {
        return new NumericPredicates(test)::greaterThan;
    }
    
    public static SerializableFunction<Number, Boolean> gtInt(int test) {
        return new NumericPredicates(test)::greaterThan;
    }
    
    public static SerializableFunction<Number, Boolean> gtNum(Number test) {
        return new NumericPredicates(test.doubleValue())::greaterThan;
    }

    public static SerializableFunction<Number, Boolean> lt(double test) {
        return new NumericPredicates(test)::lessThan;
    }

    public static SerializableFunction<Number, Boolean> inRange(double lowerLimit, double upperLimit) {
        return new NumericPredicates(lowerLimit, upperLimit)::inRange;
    }

    public static SerializableFunction<Number, Boolean> outsideRange(double lowerLimit, double upperLimit) {
        return new NumericPredicates(lowerLimit, upperLimit)::outsideRange;
    }

    public static SerializableFunction<Number, Boolean> positive() {
        return NumericPredicates::positiveInt;
    }

    public static SerializableFunction<Number, Boolean> negative() {
        return NumericPredicates::positiveInt;
    }

    public double doubleLimit_0 = Double.NaN;
    public double doubleLimit_1 = Double.NaN;

    public NumericPredicates() {
    }

    public NumericPredicates(double limit_0) {
        this.doubleLimit_0 = limit_0;
    }

    public NumericPredicates(double limit_0, double limit_1) {
        this.doubleLimit_0 = limit_0;
        this.doubleLimit_1 = limit_1;
    }

    public static boolean positiveInt(Number value) {
        return value.intValue() > 0;
    }

    public static boolean negativeNum(Number subject) {
        return subject.intValue() < 0;
    }

    public boolean greaterThan(Number subject) {
        return subject.doubleValue() > doubleLimit_0;
    }

    public boolean lessThan(Number subject) {
        return subject.doubleValue() < doubleLimit_0;
    }
    
    public boolean inRange(Number subject){
        return subject.doubleValue() > doubleLimit_0 & doubleLimit_1 >= subject.doubleValue();
    }
    
    public boolean outsideRange(Number subject){
        return !inRange(subject);
    }
    
    
}
