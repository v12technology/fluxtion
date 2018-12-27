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
package com.fluxtion.extension.declarative.funclib.builder.test;

import com.fluxtion.extension.declarative.api.Test;
import com.fluxtion.extension.declarative.funclib.api.filter.BinaryPredicates.EqualTo;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface EqualToHelper {

    public static < S extends Number, T extends Number> Test equalTo(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test equalToOnce(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> equalToFilter(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> equalToFilterOnce(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test equalTo(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test equalToOnce(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> equalToFilter(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> equalToFilterOnce(S[] op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test equalTo(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test equalToOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> equalToFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> equalToFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test equalTo(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test equalToOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> equalToFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> equalToFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test equalTo(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test equalToOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> equalToFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> equalToFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test equalTo(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test equalToOnce(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> equalToFilter(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> equalToFilterOnce(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test equalTo(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test equalToOnce(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> equalToFilter(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> equalToFilterOnce(S op1, T op2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test equalTo(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test equalToOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> equalToFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> equalToFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test equalTo(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test equalToOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> equalToFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> equalToFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test equalTo(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test equalToOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> equalToFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> equalToFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, Number> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test equalTo(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test equalToOnce(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> equalToFilter(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> equalToFilterOnce(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test equalTo(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test equalToOnce(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> equalToFilter(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> equalToFilterOnce(S op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test equalTo(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test equalToOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> equalToFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> equalToFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test equalTo(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test equalToOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> equalToFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> equalToFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test equalTo(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test equalToOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> equalToFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> equalToFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test equalTo(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test equalToOnce(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> equalToFilter(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> equalToFilterOnce(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test equalTo(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test equalToOnce(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> equalToFilter(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> equalToFilterOnce(S[] op1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test equalTo(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test equalToOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> equalToFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> equalToFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test equalTo(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test equalToOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> equalToFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> equalToFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test equalTo(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test equalToOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> equalToFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> equalToFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test equalTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test equalToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> equalToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test equalTo(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test equalToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> equalToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> equalToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test equalTo(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test equalToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> equalToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> equalToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test equalTo(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test equalToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> equalToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> equalToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test equalTo(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test equalToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> equalToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> equalToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test equalTo(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test equalToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> equalToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> equalToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test equalTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test equalToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> equalToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> equalToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test equalTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test equalToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> equalToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> equalToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test equalTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test equalToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> equalToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> equalToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test equalTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test equalToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> equalToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> equalToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test equalTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test equalToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> equalToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> equalToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test equalTo(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test equalToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> equalToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> equalToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test equalTo(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test equalToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> equalToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> equalToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test equalTo(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test equalToOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> equalToFilter(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> equalToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test equalTo(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test equalToOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> equalToFilter(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> equalToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test equalTo(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test equalToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> equalToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> equalToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<EqualTo, S> testToBuild = TestBuilder.buildTest(EqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
