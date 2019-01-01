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
import com.fluxtion.ext.futext.api.filter.BinaryPredicates.GreaterThan;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface GreaterThanHelper {

    public static < S extends Number, T extends Number> Test greaterThan(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test greaterThanOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanFilter(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThan(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanFilter(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test greaterThan(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test greaterThanOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test greaterThan(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test greaterThanOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test greaterThan(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test greaterThanOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test greaterThan(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test greaterThanOnce(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanFilter(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanFilterOnce(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThan(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOnce(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanFilter(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanFilterOnce(S op1, T op2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test greaterThan(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test greaterThanOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test greaterThan(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test greaterThanOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test greaterThan(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test greaterThanOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, Number> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThan(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOnce(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanFilter(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanFilterOnce(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThan(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOnce(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanFilter(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(S op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThan(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test greaterThan(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test greaterThanOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test greaterThan(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThan(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanFilter(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThan(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanFilter(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThan(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test greaterThan(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test greaterThanOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test greaterThan(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThan(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test greaterThan(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test greaterThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test greaterThan(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test greaterThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test greaterThan(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test greaterThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test greaterThan(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test greaterThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> greaterThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> greaterThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test greaterThan(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test greaterThanOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> greaterThanFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> greaterThanFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test greaterThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test greaterThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test greaterThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test greaterThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test greaterThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test greaterThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test greaterThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test greaterThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> greaterThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> greaterThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test greaterThan(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test greaterThanOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> greaterThanFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> greaterThanFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test greaterThan(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test greaterThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> greaterThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> greaterThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test greaterThan(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test greaterThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> greaterThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> greaterThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test greaterThan(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test greaterThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> greaterThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> greaterThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test greaterThan(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test greaterThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> greaterThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> greaterThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test greaterThan(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test greaterThanOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> greaterThanFilter(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> greaterThanFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThan, S> testToBuild = TestBuilder.buildTest(GreaterThan.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
