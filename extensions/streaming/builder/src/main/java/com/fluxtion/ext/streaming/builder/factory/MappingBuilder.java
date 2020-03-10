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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableQuadFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.api.partition.LambdaReflection.SerializableTriFunction;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;
import com.fluxtion.ext.streaming.builder.stream.StreamOperatorService;
import com.fluxtion.ext.streaming.builder.util.FunctionArg;
import static com.fluxtion.ext.streaming.builder.util.FunctionArg.arg;

/**
 * Provides helper methods to build mapping functions from nodes and Events.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class MappingBuilder {

    /**
     * Build a mapping function with nary inputs
     *
     * @param <R> The output type of the mapping function
     * @param test the test function to apply
     * @param args the input arguments for the mapping function
     * @return the output of the function as {@link Wrapper} stream
     */
    public static <R> Wrapper<R> map(MethodReferenceReflection test, FunctionArg... args) {
        final Object mapperInstance = test.captured().length == 0 ? null : test.captured()[0];
        StreamFunctionCompiler builder = StreamFunctionCompiler.map(mapperInstance, test.method(), args);
        final Wrapper wrapper = builder.build();
        wrapper.alwaysReset(false);
        return wrapper;
    }

    public static <T, R, S> Wrapper<R> map(SerializableFunction<? super S, R> mapper,
            SerializableFunction<T, ? extends S> supplier) {
        return select(supplier.getContainingClass()).map(mapper, supplier);
    }

    public static <R, S> Wrapper<R> map(SerializableFunction<? super S, R> mapper,
            FunctionArg<? extends S> arg1) {
        return map((MethodReferenceReflection)mapper, arg1);
    }

    public static <T, R, S> Wrapper<R> map(SerializableFunction<? super S, R> mapper,
            SerializableSupplier<? extends S> supplier) {
        return map((MethodReferenceReflection) mapper, arg(supplier));
    }
    
    public static <R, S, U> Wrapper<R> map(SerializableBiFunction<? super U, ? super S, R> mapper,
            FunctionArg<? extends U> arg1,
            FunctionArg<? extends S> arg2) {
        return map((MethodReferenceReflection) mapper, arg1, arg2);
    }

    public static <E1, E2, R, S, U> Wrapper<R> map(SerializableBiFunction<? super U, ? super S, R> mapper,
            SerializableFunction<E1, ? extends U> supplier1,
            SerializableFunction<E2, ? extends S> supplier2) {
        return map((MethodReferenceReflection) mapper, arg(supplier1), arg(supplier2));
    }

    
    public static <R, S, U, T> Wrapper<R> map(SerializableTriFunction<? super U, ? super S,? super T, R> mapper,
            FunctionArg<? extends U> arg1,
            FunctionArg<? extends S> arg2,
            FunctionArg<? extends T> arg3
            ) {
        return map((MethodReferenceReflection) mapper, arg1, arg2, arg3);
    } 
    
    public static <E1, E2, E3, R, S, U, T> Wrapper<R> map(SerializableTriFunction<? super U, ? super S,? super T, R> mapper,
            SerializableFunction<E1, ? extends U> supplier1,
            SerializableFunction<E2, ? extends S> supplier2,
            SerializableFunction<E3, ? extends T> supplier3
    ) {
        return map((MethodReferenceReflection) mapper, arg(supplier1), arg(supplier2), arg(supplier3));
    }
    
    public static <R, S, U, T, V> Wrapper<R> map(SerializableQuadFunction<? super U, ? super S,? super T, ? super V, R> mapper,
            FunctionArg<? extends U> arg1,
            FunctionArg<? extends S> arg2,
            FunctionArg<? extends S> arg3,
            FunctionArg<? extends T> arg4
            ) {
        return map((MethodReferenceReflection) mapper, arg1, arg2, arg3, arg4);
    } 
    
    public static <E1, E2, E3, E4, R, S, U, T, V> Wrapper<R> map(SerializableQuadFunction<? super U, ? super S,? super T, ? super V, R> mapper,
            SerializableFunction<E1, ? extends U> supplier1,
            SerializableFunction<E2, ? extends S> supplier2,
            SerializableFunction<E3, ? extends T> supplier3,
            SerializableFunction<E4, ? extends V> supplier4
    ) {
        return map((MethodReferenceReflection) mapper, arg(supplier1), arg(supplier2), arg(supplier3), arg(supplier4));
    }
    
    /**
     * Maps a set of nodes with a single mapping function. Only nodes that
     * notify a change are processed by the mapping function. The function will
     * consult each node on every update.
     *
     * @param <R>
     * @param <S>
     * @param mapper
     * @param suppliers
     * @return
     */
    public static <R, S> Wrapper<R> mapSet(SerializableFunction<S, R> mapper,
            FunctionArg... suppliers) {
        StreamFunctionCompiler builder = StreamFunctionCompiler.mapSet(mapper.captured()[0], mapper.method(), suppliers);
        final Wrapper wrapper = builder.build();
        wrapper.alwaysReset(true);
        return wrapper;
    }

    /**
     * Maps a set of nodes with a mapping function. Only nodes that notify in
     * the current execution path are included in the mapping function.
     *
     * @param <R>
     * @param <S>
     * @param mapper
     * @param suppliers
     * @return
     */
    public static <R, S> Wrapper<R> mapSetOnUpdate(SerializableFunction<S, R> mapper,
            SerializableSupplier<S>... suppliers) {
        //create instance of function and wrap
        final Object targetInstance = mapper.captured()[0];
        Wrapper<R> stream = (Wrapper<R>) StreamOperatorService.stream(targetInstance);
        for (SerializableSupplier<S> supplier : suppliers) {
            PushBuilder.push(supplier, mapper);
        }
        return stream;
    }
}
