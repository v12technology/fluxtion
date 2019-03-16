/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.stream.StreamFunctions;
import com.fluxtion.ext.declarative.api.stream.StreamFunctions.Sum;
import static com.fluxtion.ext.declarative.builder.stream.FunctionBuilder.map;

/**
 * Prototype example of using he 
 * @author V12 Technology Ltd.
 */
public class Functions {

    /**
     * Cumsum function for use in a stream.
     * @param <T>
     * @return 
     */
    public static <T extends Number> LambdaReflection.SerializableFunction<T, Number> cumSum() {
        return new StreamFunctions.Sum()::addValue;
    }
    
    public static <T extends Event> Wrapper<Number> cumSum(SerializableFunction<T, Number> supplier){
        return map(new Sum()::addValue, supplier);
    } 
      
    public static <T> Wrapper<Number> cumSum(LambdaReflection.SerializableSupplier<Number> supplier){
        return map(new Sum()::addValue, supplier);
    }
    
}
