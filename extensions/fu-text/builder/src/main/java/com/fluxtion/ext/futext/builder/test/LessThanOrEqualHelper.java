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
import com.fluxtion.ext.futext.api.filter.BinaryPredicates.LessThanOrEqual;
import com.fluxtion.api.event.Event;
import java.util.function.Function;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface LessThanOrEqualHelper {

    public static < S extends Number, T extends Number> Test lessThanOrEqual(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test lessThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOrEqual(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test lessThanOrEqual(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test lessThanOrEqualOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanOrEqualFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanOrEqualFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test lessThanOrEqual(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test lessThanOrEqualOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanOrEqualFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanOrEqualFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test lessThanOrEqual(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test lessThanOrEqualOnce(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanOrEqualFilter(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanOrEqualFilterOnce(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test lessThanOrEqual(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test lessThanOrEqualOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanOrEqualFilter(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> lessThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOrEqual(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test lessThanOrEqualOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanOrEqualFilter(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> lessThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test lessThanOrEqual(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test lessThanOrEqualOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanOrEqualFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> lessThanOrEqualFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test lessThanOrEqual(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test lessThanOrEqualOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanOrEqualFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> lessThanOrEqualFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test lessThanOrEqual(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test lessThanOrEqualOnce(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanOrEqualFilter(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> lessThanOrEqualFilterOnce(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, Number> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOrEqual(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOrEqualOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanOrEqualFilter(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOrEqual(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOrEqualOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOrEqual(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOrEqualOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanOrEqualFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test lessThanOrEqual(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test lessThanOrEqualOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanOrEqualFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanOrEqualFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test lessThanOrEqual(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test lessThanOrEqualOnce(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanOrEqualFilter(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanOrEqualFilterOnce(S op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOrEqual(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test lessThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOrEqual(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test lessThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOrEqual(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test lessThanOrEqualOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanOrEqualFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test lessThanOrEqual(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test lessThanOrEqualOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanOrEqualFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> lessThanOrEqualFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test lessThanOrEqual(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test lessThanOrEqualOnce(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanOrEqualFilter(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> lessThanOrEqualFilterOnce(S[] op1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test lessThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test lessThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> lessThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test lessThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test lessThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> lessThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test lessThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test lessThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test lessThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test lessThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> lessThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test lessThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test lessThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> lessThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test lessThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test lessThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> lessThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test lessThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test lessThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> lessThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test lessThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test lessThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test lessThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test lessThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> lessThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test lessThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test lessThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> lessThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test lessThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test lessThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> lessThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> lessThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test lessThanOrEqual(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test lessThanOrEqualOnce(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> lessThanOrEqualFilter(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> lessThanOrEqualFilterOnce(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test lessThanOrEqual(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test lessThanOrEqualOnce(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> lessThanOrEqualFilter(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> lessThanOrEqualFilterOnce(S supplier1, SerializableSupplier accessor1, T op2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test lessThanOrEqual(S supplier1, SerializableSupplier accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test lessThanOrEqualOnce(S supplier1, SerializableSupplier accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> lessThanOrEqualFilter(S supplier1, SerializableSupplier accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> lessThanOrEqualFilterOnce(S supplier1, SerializableSupplier accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test lessThanOrEqual(S supplier1, SerializableSupplier accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test lessThanOrEqualOnce(S supplier1, SerializableSupplier accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> lessThanOrEqualFilter(S supplier1, SerializableSupplier accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> lessThanOrEqualFilterOnce(S supplier1, SerializableSupplier accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test lessThanOrEqual(S supplier1, SerializableSupplier accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test lessThanOrEqualOnce(S supplier1, SerializableSupplier accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> lessThanOrEqualFilter(S supplier1, SerializableSupplier accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> lessThanOrEqualFilterOnce(S supplier1, SerializableSupplier accessor1, T supplier2, SerializableSupplier accessor2){
        TestBuilder<LessThanOrEqual, S> testToBuild = TestBuilder.buildTest(LessThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
