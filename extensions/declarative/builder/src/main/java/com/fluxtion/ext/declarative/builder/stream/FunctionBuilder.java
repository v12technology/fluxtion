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
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.builder.factory.PushBuilder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FunctionBuilder {

    public static <T extends Event, R, S> Wrapper<R> map(SerializableFunction<S, R> mapper, SerializableFunction<T, S> supplier) {
        return select(supplier.getContainingClass()).map(mapper, supplier);
    }

    public static <T, R, S> Wrapper<R> map(SerializableFunction<S, R> mapper,
            LambdaReflection.SerializableSupplier<S> supplier) {
        Method m = mapper.method();
        Object captured = null;
        if(!Modifier.isStatic(m.getModifiers())){
            captured = mapper.captured()[0];
        }
        FilterBuilder builder = FilterBuilder.map(captured, m,
                StreamBuilder.stream(supplier.captured()[0]), supplier.method(), true);
        return builder.build();
    }

    public static <T, R, S> Wrapper<R> map(SerializableFunction<S, R> mapper,
            SerializableSupplier<S>... suppliers) {
        //create instance of function and wrap
        final Object targetInstance = mapper.captured()[0];
        Wrapper<R> stream = (Wrapper<R>) StreamBuilder.stream(targetInstance);
        for (SerializableSupplier<S> supplier : suppliers) {
            PushBuilder.push(supplier, mapper);
        }
        return stream;
    }
}
