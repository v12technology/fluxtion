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
package com.fluxtion.ext.declarative.builder.test;

import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection;
import com.fluxtion.api.event.Event;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Helper functions for building Test nodes in the SEP.
 *
 * @author greg
 */
public class FilterHelper {

    //Event class with filters
    public static <S extends Event, V, T extends Test> Wrapper<S> filter(
            Class<S> eventClass, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filter(EventSelect.select(eventClass), accessor, testClass, args);
    }
    
    public static <S extends Event, V, T extends Test> Wrapper<S> filterOnce(
            Class<S> eventClass, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filterOnce(EventSelect.select(eventClass), accessor, testClass, args);
    }

    public static <S extends Event, V, T extends Test> Wrapper<S> filter(
            Class<S> eventClass, String[] filter, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filter(EventSelect.select(eventClass, filter), accessor, testClass, args);
    }

    public static <S extends Event, V, T extends Test> Wrapper<S> filterOnce(
            Class<S> eventClass, String[] filter, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filterOnce(EventSelect.select(eventClass, filter), accessor, testClass, args);
    }

    public static <S extends Event, V, T extends Test> Wrapper<S> filter(
            Class<S> eventClass, int[] filter, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filter(EventSelect.select(eventClass, filter), accessor, testClass, args);
    }

    public static <S extends Event, V, T extends Test> Wrapper<S> filterOnce(
            Class<S> eventClass, int[] filter, Function<S, ?> accessor, Class<T> testClass, double... args) {
        return filterOnce(EventSelect.select(eventClass, filter), accessor, testClass, args);
    }

    //Wrapper with numeric args
    public static <S, V, T extends Test> Wrapper<S> filter(
            Wrapper<S> handler, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filterOnce(
            Wrapper<S> handler, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        testBuilder.notifyOnChange(true);
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filter(
            Wrapper<S>[] handler, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filterOnce(
            Wrapper<S>[] handler, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        testBuilder.notifyOnChange(true);
        return testBuilder.buildFilter();
    }

    //T with numeric args
    public static <S, V, T extends Test> Wrapper<S> filter(
            S supplier, LambdaReflection.SerializableSupplier<S, V> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filterOnce(
            S supplier, LambdaReflection.SerializableSupplier<S, V> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        testBuilder.notifyOnChange(true);
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filter(
            S[] supplier, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        return testBuilder.buildFilter();
    }

    public static <S, V, T extends Test> Wrapper<S> filterOnce(
            S[] supplier, Function<S, ?> accessor, Class<T> testClass, double... args) {
        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        testBuilder.notifyOnChange(true);
        return testBuilder.buildFilter();
    }

    //Number
    public static <T extends Test, N extends Number> Wrapper<Number> filter(N subject, Class<T> testClass, N[] n) {
        TestBuilder<T, Number> testBuilder = TestBuilder.buildTest(testClass, subject);
        testBuilder.arg(n);
        return testBuilder.buildFilter();
    }

    public static <T extends Test, N extends Number> Wrapper<Number> filter(N subject, Class<T> testClass, double... args) {
        TestBuilder<T, Number> testBuilder = TestBuilder.buildTest(testClass, subject);
        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
        return testBuilder.buildFilter();
    }
}
