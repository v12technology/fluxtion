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
package com.fluxtion.ext.futext.builder.test;

import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.futext.api.filter.BinaryPredicates.LessThan;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface LessThanHelper {

    public static < S extends Number, T extends Number> Test lessThan(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test lessThanOnce(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanFilter(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanFilterOnce(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test lessThan(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOnce(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanFilter(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanFilterOnce(S[] op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test lessThan(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test lessThanOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test lessThan(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test lessThanOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test lessThan(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test lessThanOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test lessThan(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test lessThanOnce(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanFilter(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanFilterOnce(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test lessThan(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOnce(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanFilter(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanFilterOnce(S op1, T op2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test lessThan(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test lessThanOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test lessThan(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test lessThanOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test lessThan(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test lessThanOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, Number> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test lessThan(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOnce(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanFilter(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanFilterOnce(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThan(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOnce(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanFilter(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanFilterOnce(S op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test lessThan(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test lessThan(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test lessThanOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test lessThan(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test lessThanOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test lessThan(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOnce(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanFilter(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanFilterOnce(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThan(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOnce(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanFilter(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanFilterOnce(S[] op1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test lessThan(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test lessThan(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test lessThanOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test lessThan(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test lessThanOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test lessThan(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test lessThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> lessThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> lessThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test lessThan(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test lessThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test lessThan(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test lessThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> lessThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> lessThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test lessThan(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test lessThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> lessThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> lessThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test lessThan(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test lessThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> lessThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> lessThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test lessThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test lessThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> lessThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> lessThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test lessThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test lessThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test lessThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test lessThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> lessThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> lessThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test lessThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test lessThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> lessThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> lessThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test lessThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test lessThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> lessThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> lessThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test lessThan(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test lessThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> lessThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> lessThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test lessThan(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test lessThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> lessThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> lessThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test lessThan(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test lessThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> lessThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> lessThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test lessThan(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test lessThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> lessThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> lessThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test lessThan(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test lessThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> lessThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> lessThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<LessThan, S> testToBuild = TestBuilder.buildTest(LessThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
