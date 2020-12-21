/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.util;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import java.util.Comparator;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FunctionComparator implements Comparator {

    SerializableFunction comparingFunction;

    public FunctionComparator() {
    }

    public FunctionComparator(SerializableFunction comparingFunction) {
        this.comparingFunction = comparingFunction;
    }
    
    public SerializableFunction getComparingFunction() {
        return comparingFunction;
    }

    public <S, R> void setComparingFunction(SerializableFunction<S, R> comparingFunction) {
        this.comparingFunction = comparingFunction;
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        return ((Comparable) comparingFunction.apply(o1)).compareTo(comparingFunction.apply(o2));
    }

}
