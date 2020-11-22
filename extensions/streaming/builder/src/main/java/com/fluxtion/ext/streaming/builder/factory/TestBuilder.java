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

import com.fluxtion.api.partition.LambdaReflection.MethodReferenceReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableQuadFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableTriFunction;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.Argument;
import static com.fluxtion.ext.streaming.api.stream.Argument.arg;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TestBuilder {

    /**
     * Build a {@link Test} function with nary inputs
     * @param test the test function to apply
     * @param args
     * @return 
     */
    public static Test test(MethodReferenceReflection test, Argument... args){
        final Object mapperInstance = test.captured().length == 0 ? null : test.captured()[0];
        StreamFunctionCompiler builder = StreamFunctionCompiler.test(mapperInstance, test.method(), args);
        final Wrapper wrapper = builder.build();
        return (Test) wrapper;
    }
    
    public static <R extends Boolean, S> Test test(SerializableFunction<? super S, R> test,
            Argument<? extends S> arg1
    ) {
        return test((MethodReferenceReflection)test, arg1);
    }

    public static <A, R extends Boolean, S> Test test(SerializableFunction<? super S, R> test,
            SerializableFunction<A, ? extends S> arg1
    ) {
        return test(test, arg(arg1) );
    }
    
    public static <T, R extends Boolean, S> Test test(SerializableBiFunction<? super T, ? super S, R> test,
            Argument<? extends T> arg1, 
            Argument<? extends S> arg2
    ) {
        return test((MethodReferenceReflection)test, arg1, arg2);
    }

    public static <A, B, T, R extends Boolean, S> Test test(SerializableBiFunction<? super T, ? super S, R> test,
            SerializableFunction<A, ? extends T> arg1, 
            SerializableFunction<B, ? extends S> arg2 
    ) {
        return test(test, arg(arg1), arg(arg2));
    }
    
    
    public static <X, T, R extends Boolean, S> Test test(SerializableTriFunction<? super X, ? super T, ? super S, R> test,
            Argument<? extends X> arg1, 
            Argument<? extends T> arg2, 
            Argument<? extends S> arg3
    ) {
        return test((MethodReferenceReflection)test, arg1, arg2, arg3);
    }

    public static <A, B, C, X, T, R extends Boolean, S> Test test(SerializableTriFunction<? super X, ? super T, ? super S, R> test,
            SerializableFunction<A, ? extends X> arg1, 
            SerializableFunction<B, ? extends T> arg2, 
            SerializableFunction<C, ? extends S> arg3
    ) {
        return test(test, arg(arg1), arg(arg2), arg(arg3));
    }

    public static <Z, X, T, R, S> Test test(SerializableQuadFunction<? super Z, ? super X, ? super T, ? super S, R> test,
            Argument<? extends Z> arg1, 
            Argument<? extends X> arg2, 
            Argument<? extends T> arg3, 
            Argument<? extends S> arg4
    ) {
        return test((MethodReferenceReflection)test, arg1, arg2, arg3, arg4);
    }
    
    public static <A, B, C, D, Z, X, T, R extends Boolean, S> Test test(SerializableQuadFunction<? super Z, ? super X, ? super T, ? super S, R> test,
            SerializableFunction<A, ? extends X> arg1, 
            SerializableFunction<B, ? extends T> arg2, 
            SerializableFunction<C, ? extends S> arg3,
            SerializableFunction<D, ? extends Z> arg4
    ) {
        return test((MethodReferenceReflection)test, arg(arg1), arg(arg2), arg(arg3), arg(arg4));
    }
    
    
}
