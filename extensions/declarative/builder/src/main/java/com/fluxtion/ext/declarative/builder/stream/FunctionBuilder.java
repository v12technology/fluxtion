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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.stream.StreamFunctions.Average;
import com.fluxtion.ext.declarative.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.declarative.api.stream.StreamFunctions.Sum;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FunctionBuilder {
    
    public static <T extends Event> Wrapper<Number> count(Class<T> eventClass){
        return select(eventClass).map(new Count()::increment);
    }
    
    public static <T extends Event> Wrapper<Number> sum(SerializableFunction<T, Number> supplier){
        return map(new Sum()::addValue, supplier);
    }
    
    public static <T extends Event> Wrapper<Number> avg(SerializableFunction<T, Number> supplier){
        return map(new Average()::addValue, supplier);
    }
    
    public static  <T extends Event, R, S> Wrapper<R> map(SerializableFunction<S, R> mapper, SerializableFunction<T, R> supplier){
        return select(supplier.getContainingClass()).map(mapper, supplier);
    }
}
