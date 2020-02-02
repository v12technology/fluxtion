/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;

/**
 * Utility functions for creating filtered streams.
 * 
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FilterBuilder {
    
    public static <T, S> Wrapper<T> filter(SerializableFunction<T, S> supplier, SerializableFunction<? extends S, Boolean> filter) {
        return select(supplier.getContainingClass()).filter(supplier, filter);
    }   
    
    public static <T, S> Wrapper<T> filter(T instance, SerializableFunction<T, S> supplier, SerializableFunction<? extends S, Boolean> filter){
        return  stream(instance).filter(supplier, filter);
    }
    
}
