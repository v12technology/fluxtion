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
package com.fluxtion.ext.streaming.builder.util;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.ConstantNumber;
import com.fluxtion.ext.streaming.api.stream.Argument;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import java.lang.reflect.Method;

/**
 * Represents an argument for use with a function
 *
 * @author V12 Technology Ltd.
 */
public class FunctionArg<T> extends Argument<T> {

    public static <T, S> FunctionArg<S> arg(SerializableFunction<T, S> supplier) {
        final Class containingClass = supplier.getContainingClass();
        return new FunctionArg(select(containingClass), supplier.method(), true);
    }

    public static <T extends Number> FunctionArg<T> arg(Double d) {
        SerializableFunction<Number, Double> s = Number::doubleValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> FunctionArg<T> arg(int d) {
        SerializableFunction<Number, Integer> s = Number::intValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> FunctionArg<T> arg(long d) {
        SerializableFunction<Number, Long> s = Number::longValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }

    public static <T extends Number> FunctionArg<Number> arg(Wrapper<T> wrapper) {
        return arg(wrapper, Number::doubleValue);
    }

    public static <T, S> FunctionArg<S> arg(Wrapper<T> wrapper, SerializableFunction<T, S> supplier) {
        return new FunctionArg(wrapper, supplier.method(), true);
    }

    public static <T> FunctionArg<T> arg(SerializableSupplier<T> supplier) {
        return new FunctionArg(supplier.captured()[0], supplier.method(), true);
    }

    public static FunctionArg arg(Object supplier) {
        return new FunctionArg(supplier, null, true);
    }

    public FunctionArg(Object source, Method accessor, boolean cast) {
        super(source, accessor, cast);
    }

  
}
