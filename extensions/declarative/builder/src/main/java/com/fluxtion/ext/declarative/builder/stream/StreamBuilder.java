/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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

import com.fluxtion.api.partition.LambdaReflection.SerializableConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.declarative.api.stream.StreamOperator;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.google.auto.service.AutoService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author V12 Technology Ltd.
 */
@AutoService(StreamOperator.class)
public class StreamBuilder implements StreamOperator {

    @Override
    public <S, T> Wrapper<T> filter(SerializableFunction<S, Boolean> filter, Wrapper<T> source, Method accessor, boolean cast) {
        Method filterMethod = filter.method();
        FilterBuilder builder = null;
        if (Modifier.isStatic(filterMethod.getModifiers())) {
            builder = FilterBuilder.filter(filterMethod, source, accessor, cast);
        } else {
            builder = FilterBuilder.filter(filter.captured()[0], filterMethod, source, accessor, cast);
        }
        return builder.build();
    }

    @Override
    public <T> Wrapper<T> filter(SerializableFunction<T, Boolean> filter, Wrapper<T> source, boolean cast) {
        Method filterMethod = filter.method();
        FilterBuilder builder = null;
        if (Modifier.isStatic(filterMethod.getModifiers())) {
            builder = FilterBuilder.filter(filterMethod, source);
        } else {
            builder = FilterBuilder.filter(filter.captured()[0], filterMethod, source);
        }
        return builder.build();
    }

    @Override
    public <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, boolean cast) {
        Method mappingMethod = mapper.method();
        FilterBuilder builder = null;
        if (Modifier.isStatic(mappingMethod.getModifiers())) {
            builder = FilterBuilder.map(null, mappingMethod, source, null, true);
        } else {
            builder = FilterBuilder.map(mapper.captured()[0], mappingMethod, source, null, true);
        }
        return builder.build();
    }

    @Override
    public <T, R> Wrapper<R> map(SerializableFunction<T, R> mapper, Wrapper<T> source, Method accessor, boolean cast) {
        Method mappingMethod = mapper.method();
        FilterBuilder builder = null;
        if (Modifier.isStatic(mappingMethod.getModifiers())) {
            builder = FilterBuilder.map(null, mappingMethod, source, accessor, true);
        } else {
            builder = FilterBuilder.map(mapper.captured()[0], mappingMethod, source, accessor, true);
        }
        return builder.build();
    }

    @Override
    public <T, S extends T> Wrapper<T> forEach(SerializableConsumer<S> consumer, Wrapper<T> source) {
        Method consumerMethod = consumer.method();
        FilterBuilder builder = null;
        if (Modifier.isStatic(consumerMethod.getModifiers())) {
            builder = FilterBuilder.consume(null, consumerMethod, source);
        } else {
            builder = FilterBuilder.consume(consumer.captured()[0], consumerMethod, source);
        }
        builder.build();
        return source;
    }

}
