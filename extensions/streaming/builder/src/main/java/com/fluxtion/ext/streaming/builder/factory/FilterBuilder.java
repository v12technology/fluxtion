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

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.test.TestFilter;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.ext.streaming.builder.stream.StreamFunctionCompiler;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;

/**
 * Utility functions for creating filtered streams.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FilterBuilder {

    /**
     * Applies a filter function on a property of an incoming event stream, and
     * only produces an output if the function returns true. Creates a
     * subscription for the input stream type
     *
     * @param <T> The input stream type
     * @param <S> The type of the property to extract and apply a filer function
     * to
     * @param supplier The instance stream to filter
     * @param filter The filter function applied to the property of the incoming
     * stream
     * @return The filtered stream
     */
    public static <T, S> FilterWrapper<T> filter(SerializableFunction<T, S> supplier, SerializableFunction<? extends S, Boolean> filter) {
        return select(supplier.getContainingClass()).filter(supplier, filter);
    }

    /**
     * Applies a filter function on a property of an incoming event stream, and
     * only produces an output if the function returns true. Streams the
     * supplied instance to the filter function
     *
     * @param <T> The input stream type
     * @param <S> The type of the property to extract and apply a filer function
     * to
     * @param supplier The instance stream to filter
     * @param filter The filter function applied to the property of the incoming
     * stream
     * @return The filtered stream
     */
    public static <T, S> FilterWrapper<T> filter(SerializableSupplier<S> supplier, SerializableFunction<? extends S, Boolean> filter) {
        return (FilterWrapper<T>) StreamFunctionCompiler.filter(filter, stream(supplier)).build();
    }

    /**
     * Applies a filter function to an incoming event stream, and only produces
     * an output if the function returns true. Creates a subscription for the
     * input stream type
     *
     * @param <T> The input stream type
     * @param clazz the class to create a subscription for
     * @param filter The filter function applied to the property of the incoming
     * stream
     * @return The filtered stream
     */
    public static <T> FilterWrapper<T> filter(Class<T> clazz, SerializableFunction<? extends T, Boolean> filter) {
        return select(clazz).filter(filter);
    }

    /**
     * Applies a filter function to an incoming event stream, and only produces
     * an output if the function returns true. Creates a subscription for the
     * input stream type
     *
     * @param <T> The input stream type
     * @param filter The filter function applied to the property of the incoming
     * stream
     * @return The filtered stream
     */
    public static <T> FilterWrapper<T> filter(SerializableFunction<? extends T, Boolean> filter) {
        Class<T> clazz = (Class<T>) filter.getContainingClass();
        return select(clazz).filter(filter);
    }

    /**
     * Applies a test function before streaming the incoming event. The test
     * function can be disconnected to the input stream to filter. The test
     * function is applied whenever the input stream updates and not when inputs
     * to the test may change. For example a stream of prices may only publish
     * during certain hours, the test passes or fails depending upon the time.
     *
     * @param <T> The output type of the filtered stream
     * @param clazz The input class type to create an input strem from
     * @param test The test to apply, only a passed test
     * @return The filtered stream
     */
    public static <T> FilterWrapper<T> filter(Class<T> clazz, Test test) {
        return SepContext.service().add(new TestFilter<>(select(clazz), test));
    }

    /**
     * Applies a test function before streaming the incoming event. The test
     * function can be disconnected to the input stream to filter. The test
     * function is applied whenever the input stream updates and not when inputs
     * to the test may change. For example a stream of prices may only publish
     * during certain hours, the test passes or fails depending upon the time.
     *
     * @param <T> The output type of the filtered stream
     * @param instance The input instance to create a filtered stream from
     * @param test The test to apply, only a passed test
     * @return The filtered stream
     */
    public static <T> FilterWrapper<T> filter(T instance, Test test) {
        return SepContext.service().add(new TestFilter<>(stream(instance), test));
    }

}
