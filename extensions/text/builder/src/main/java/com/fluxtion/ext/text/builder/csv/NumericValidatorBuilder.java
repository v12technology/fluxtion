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
package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.text.api.csv.NumberValidator;

/**
 *
 * @author V12 Technology Ltd.
 */


public class NumericValidatorBuilder {

    public static NumberValidator limit(double limit) {
        return new NumberValidator(limit);
    }
    
    public static NumberValidator range(double limit1, double limit2) {
        return new NumberValidator(limit1, limit2);
    }

    public static LambdaReflection.SerializableConsumer<Double> gt(double limit) {
        return limit(limit)::greaterThan;
    }

    public static LambdaReflection.SerializableConsumer<Double> lt(double limit) {
        return limit(limit)::lessThan;
    }

    public static LambdaReflection.SerializableConsumer<Double> eq(int limit) {
        return limit(limit)::equal;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> zero() {
        return limit(0)::equal;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> positive() {
        return limit(0)::greaterThan;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> negative() {
        return limit(0)::lessThan;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> NaN() {
        return limit(0)::isNan;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> finite() {
        return limit(0)::isFinite;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> infinte() {
        return limit(0)::isInfinite;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> withinRange(double lowerBound, double upperBound) {
        return range(lowerBound, upperBound)::withinRange;
    }
    
    public static LambdaReflection.SerializableConsumer<Double> outsideRange(double lowerBound, double upperBound) {
        return range(lowerBound, upperBound)::outsideRange;
    }
}
