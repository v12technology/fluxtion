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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.ext.declarative.builder.util.LambdaReflection;
import com.fluxtion.ext.futext.api.csv.NumberValidators;

/**
 *
 * @author V12 Technology Ltd.
 */


public class NumericValidatorBuilder {

    public static NumberValidators limit(double limit) {
        return new NumberValidators(limit);
    }

    public static LambdaReflection.SerializableConsumer<Double> gt(double limit) {
        return limit(limit)::greaterThan;
    }

    public static LambdaReflection.SerializableConsumer<Double> lt(double limit) {
        return limit(limit)::lessThan;
    }

    public static LambdaReflection.SerializableConsumer<Integer> eq(int limit) {
        return limit(limit)::equal;
    }
    
    public static LambdaReflection.SerializableConsumer<Integer> zero() {
        return limit(0)::equal;
    }
    
    public static LambdaReflection.SerializableConsumer<Integer> positive() {
        return limit(0)::greaterThan;
    }
    
    public static LambdaReflection.SerializableConsumer<Integer> negative() {
        return limit(0)::lessThan;
    }
}
