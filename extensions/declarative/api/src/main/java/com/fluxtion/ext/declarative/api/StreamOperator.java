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
package com.fluxtion.ext.declarative.api;

import com.fluxtion.api.partition.LambdaReflection;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

/**
 * An interface that defines stream operations that that can operate on a node.
 *
 * @author V12 Technology Ltd.
 */
public interface StreamOperator {

    default <S, T> Wrapper<T> filter(LambdaReflection.SerializableFunction<S, Boolean> filter,
            Wrapper<T> source, Method accessor, boolean cast) {
        return source;
    }
    default <T> Wrapper<T> filter(LambdaReflection.SerializableFunction<T, Boolean> filter,
             Wrapper<T> source, boolean cast) {
        return source;
    }

    public static StreamOperator service() {
        //possibly need to use this classloader
        ServiceLoader<StreamOperator> load = ServiceLoader.load(StreamOperator.class);
        if (load.iterator().hasNext()) {
            return load.iterator().next();
        } else {
            return new StreamOperator() {};
        }
    }
}
