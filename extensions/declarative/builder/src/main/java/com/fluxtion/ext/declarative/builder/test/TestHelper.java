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

import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection;
import com.fluxtion.runtime.event.Event;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Helper functions for building Test nodes in the SEP.
 *
 * @author greg
 */
public class TestHelper {

//    //Event class with filters
//    public static <S extends Event, T extends Test> Test test(
//            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, double... args) {
//        return test(testClass, EventSelect.select(eventClass), accessor, args);
//    }
//    
//    public static <S extends Event, V, T extends Test> Test testOnce(
//            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, double... args) {
//        return testOnce(testClass, EventSelect.select(eventClass), accessor, args);
//    }
//
//    public static <S extends Event, V, T extends Test> Test test(
//            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, String[] filter, double... args) {
//        return test(testClass, EventSelect.select(eventClass, filter), accessor, args);
//    }
//
//    public static <S extends Event, V, T extends Test> Test testOnce(
//            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, String[] filter, double... args) {
//        return testOnce(testClass, EventSelect.select(eventClass, filter), accessor, args);
//    }
//
//    public static <S extends Event, V, T extends Test> Test testOnce(
//            Class<T> testClass, Class<S> eventClass, Function<S, ?> accessor, int[] filter, double... args) {
//        return testOnce(testClass, EventSelect.select(eventClass, filter), accessor, args);
//    }
//
//    //Wrapper with numeric args
//    public static <S, V, T extends Test> Test test(
//            Class<T> testClass, Wrapper<S> handler, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test testOnce(
//            Class<T> testClass, Wrapper<S> handler, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        testBuilder.notifyOnChange(true);
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test test(
//            Class<T> testClass, Wrapper<S>[] handler, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test testOnce(
//            Class<T> testClass, Wrapper<S>[] handler, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, handler, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        testBuilder.notifyOnChange(true);
//        return testBuilder.build();
//    }
//
//    //T with numeric args
//    public static <S, V, T extends Test> Test test(
//            Class<T> testClass, S supplier, LambdaReflection.SerializableSupplier<S, V> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test testOnce(
//            Class<T> testClass, S supplier, LambdaReflection.SerializableSupplier<S, V> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        testBuilder.notifyOnChange(true);
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test test(
//            Class<T> testClass, S[] supplier, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        return testBuilder.build();
//    }
//
//    public static <S, V, T extends Test> Test testOnce(
//            Class<T> testClass, S[] supplier, Function<S, ?> accessor, double... args) {
//        TestBuilder<T, S> testBuilder = TestBuilder.buildTest(testClass, supplier, accessor);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        testBuilder.notifyOnChange(true);
//        return testBuilder.build();
//    }
//
//    //Number
//    public static <T extends Test, N extends Number> Test test(Class<T> testClass, N subject, N[] n) {
//        TestBuilder<T, Number> testBuilder = TestBuilder.buildTest(testClass, subject);
//        testBuilder.arg(n);
//        return testBuilder.build();
//    }
//
//    public static <T extends Test, N extends Number> Test test(Class<T> testClass, N subject, double... args) {
//        TestBuilder<T, Number> testBuilder = TestBuilder.buildTest(testClass, subject);
//        Arrays.stream(args).forEach(i -> testBuilder.arg(i));
//        return testBuilder.build();
//    }
}
