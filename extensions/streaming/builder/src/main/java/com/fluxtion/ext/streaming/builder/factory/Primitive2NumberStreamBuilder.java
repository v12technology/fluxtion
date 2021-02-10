/*
 * Copyright (C) 2021 V12 Technology Ltd
 *  All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Server Side Public License, version 1,
 *  as published by MongoDB, Inc.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Server Side Public License for more details.
 *
 *  You should have received a copy of the Server Side Public License
 *  along with this program.  If not, see 
 *  <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.Argument;
import static com.fluxtion.ext.streaming.api.stream.Argument.arg;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author gregp
 */
public interface Primitive2NumberStreamBuilder {

    public static <T extends Integer> LambdaReflection.SerializableFunction<T, Number> int2Num() {
        return StreamFunctions::asInt;
    }

    public static <T extends Long> LambdaReflection.SerializableFunction<T, Number> long2Num() {
        return StreamFunctions::asLong;
    }

    public static <T extends Double> LambdaReflection.SerializableFunction<T, Number> double2Num() {
        return StreamFunctions::asDouble;
    }

    public static <T extends Number> Wrapper<Number> primitive2Num(Argument<T> arg) {
        final Class<?> returnType = arg.getAccessor().getReturnType();
        Class<?> wrapperType = ClassUtils.primitiveToWrapper(returnType);
        if (wrapperType == Integer.class || wrapperType == Short.class || wrapperType == Byte.class) {
            Wrapper<Number> intWrapper = map(int2Num(), arg);
            return intWrapper;
        } else if (wrapperType == Long.class) {
            Wrapper<Number> intWrapper = map(long2Num(), arg);
            return intWrapper;
        }
        return map(double2Num(), arg);
    }

    public static <T, S extends Number> Wrapper<Number> primitive2Num(SerializableFunction<T, S> supplier) {
        return primitive2Num(arg(supplier));
    }

    public static <T extends Number> Wrapper<Number> primitive2Num(SerializableSupplier<T> supplier) {
        return primitive2Num(arg(supplier));
    }

    public static <T, S extends Number> Wrapper<Number> primitive2Num(Wrapper<T> wrapper, SerializableFunction<T, S> supplier) {
        return primitive2Num(arg(wrapper, supplier));
    }

    public static <T extends Number> Wrapper<Number> primitive2Num(Wrapper<T> wrapper) {
        return primitive2Num(arg(wrapper));
    }

}
