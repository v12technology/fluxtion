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
import com.fluxtion.ext.futext.api.filter.BinaryPredicates.GreaterThanOrEqual;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface GreaterThanOrEqualHelper {

    public static < S extends Number, T extends Number> Test greaterThanOrEqual(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test greaterThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOrEqual(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test greaterThanOrEqual(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test greaterThanOrEqualOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanOrEqualFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanOrEqualFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test greaterThanOrEqual(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test greaterThanOrEqualOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanOrEqualFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanOrEqualFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test greaterThanOrEqual(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test greaterThanOrEqualOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanOrEqualFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanOrEqualFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test greaterThanOrEqual(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test greaterThanOrEqualOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanOrEqualFilter(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> greaterThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOrEqual(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test greaterThanOrEqualOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanOrEqualFilter(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> greaterThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test greaterThanOrEqual(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test greaterThanOrEqualOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanOrEqualFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> greaterThanOrEqualFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test greaterThanOrEqual(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test greaterThanOrEqualOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanOrEqualFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> greaterThanOrEqualFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test greaterThanOrEqual(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test greaterThanOrEqualOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanOrEqualFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> greaterThanOrEqualFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, Number> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOrEqual(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOrEqualOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanOrEqualFilter(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOrEqual(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOrEqualOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(S op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOrEqual(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOrEqualOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanOrEqualFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test greaterThanOrEqual(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test greaterThanOrEqualOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanOrEqualFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanOrEqualFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOrEqual(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOrEqualOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanOrEqualFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOrEqual(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test greaterThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOrEqual(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test greaterThanOrEqualOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(S[] op1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOrEqual(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test greaterThanOrEqualOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanOrEqualFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test greaterThanOrEqual(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test greaterThanOrEqualOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanOrEqualFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> greaterThanOrEqualFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOrEqual(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test greaterThanOrEqualOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanOrEqualFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqual(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test greaterThanOrEqualOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test greaterThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test greaterThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test greaterThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test greaterThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test greaterThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test greaterThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test greaterThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test greaterThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test greaterThanOrEqual(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test greaterThanOrEqualOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test greaterThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test greaterThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test greaterThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test greaterThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test greaterThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test greaterThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test greaterThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test greaterThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test greaterThanOrEqual(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test greaterThanOrEqualOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> greaterThanOrEqualFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test greaterThanOrEqual(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test greaterThanOrEqualOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> greaterThanOrEqualFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> greaterThanOrEqualFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test greaterThanOrEqual(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test greaterThanOrEqualOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> greaterThanOrEqualFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test greaterThanOrEqual(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test greaterThanOrEqualOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> greaterThanOrEqualFilter(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> greaterThanOrEqualFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test greaterThanOrEqual(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test greaterThanOrEqualOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> greaterThanOrEqualFilter(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> greaterThanOrEqualFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test greaterThanOrEqual(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test greaterThanOrEqualOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> greaterThanOrEqualFilter(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> greaterThanOrEqualFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<GreaterThanOrEqual, S> testToBuild = TestBuilder.buildTest(GreaterThanOrEqual.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
