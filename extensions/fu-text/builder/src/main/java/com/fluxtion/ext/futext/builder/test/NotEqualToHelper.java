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
import com.fluxtion.ext.futext.api.filter.BinaryPredicates.NotEqualTo;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.test.TestBuilder;

/**
* @author Greg Higgins
*/
public interface NotEqualToHelper {

    public static < S extends Number, T extends Number> Test notEqualTo(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test notEqualToOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> notEqualToFilter(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> notEqualToFilterOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test notEqualTo(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test notEqualToOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> notEqualToFilter(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> notEqualToFilterOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test notEqualTo(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test notEqualToOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> notEqualToFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> notEqualToFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test notEqualTo(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test notEqualToOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> notEqualToFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> notEqualToFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test notEqualTo(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test notEqualToOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> notEqualToFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> notEqualToFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Test notEqualTo(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Test notEqualToOnce(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> notEqualToFilter(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Number> Wrapper<Number> notEqualToFilterOnce(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Test notEqualTo(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Test notEqualToOnce(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> notEqualToFilter(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends NumericValue> Wrapper<Number> notEqualToFilterOnce(S op1, T op2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Test notEqualTo(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Test notEqualToOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> notEqualToFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T extends Event> Wrapper<Number> notEqualToFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Test notEqualTo(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T> Test notEqualToOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T> Wrapper<Number> notEqualToFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T> Wrapper<Number> notEqualToFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Test notEqualTo(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Test notEqualToOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Number, T, V> Wrapper<Number> notEqualToFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Number, T, V> Wrapper<Number> notEqualToFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, Number> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test notEqualTo(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test notEqualToOnce(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> notEqualToFilter(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> notEqualToFilterOnce(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test notEqualTo(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test notEqualToOnce(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> notEqualToFilter(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(S op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test notEqualTo(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test notEqualToOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> notEqualToFilter(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> notEqualToFilterOnce(S op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test notEqualTo(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test notEqualToOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> notEqualToFilter(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> notEqualToFilterOnce(S op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test notEqualTo(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test notEqualToOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> notEqualToFilter(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> notEqualToFilterOnce(S op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Test notEqualTo(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Test notEqualToOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> notEqualToFilter(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Number> Wrapper<S> notEqualToFilterOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Test notEqualTo(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Test notEqualToOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> notEqualToFilter(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(S[] op1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Test notEqualTo(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Test notEqualToOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> notEqualToFilter(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T extends Event> Wrapper<S> notEqualToFilterOnce(S[] op1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Test notEqualTo(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Test notEqualToOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T> Wrapper<S> notEqualToFilter(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T> Wrapper<S> notEqualToFilterOnce(S[] op1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Test notEqualTo(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Test notEqualToOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> notEqualToFilter(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends NumericValue, T, V> Wrapper<S> notEqualToFilterOnce(S[] op1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  op1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, String[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Number> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T extends Event> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Test notEqualTo(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Test notEqualToOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilter(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S extends Event, T, V> Wrapper<S> notEqualToFilterOnce(Class<S> eventClass1, Function<S, ?> accessor1, int[] filters, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  eventClass1, accessor1, filters);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test notEqualTo(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test notEqualToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> notEqualToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> notEqualToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test notEqualTo(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test notEqualToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> notEqualToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test notEqualTo(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test notEqualToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> notEqualToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> notEqualToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test notEqualTo(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test notEqualToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> notEqualToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> notEqualToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test notEqualTo(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test notEqualToOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> notEqualToFilter(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> notEqualToFilterOnce(Wrapper<S> handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Test notEqualTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends Number> Test notEqualToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Number> Wrapper<S> notEqualToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Number> Wrapper<S> notEqualToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Test notEqualTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Test notEqualToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends NumericValue> Wrapper<S> notEqualToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Test notEqualTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, T extends Event> Test notEqualToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T extends Event> Wrapper<S> notEqualToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T extends Event> Wrapper<S> notEqualToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T> Test notEqualTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, T> Test notEqualToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T> Wrapper<S> notEqualToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T> Wrapper<S> notEqualToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Test notEqualTo(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, T, V> Test notEqualToOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, T, V> Wrapper<S> notEqualToFilter(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, T, V> Wrapper<S> notEqualToFilterOnce(Wrapper<S>[] handler1, Function<S, ?> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  handler1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Test notEqualTo(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Test notEqualToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Number> Wrapper<S> notEqualToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Number> Wrapper<S> notEqualToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Test notEqualTo(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Test notEqualToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> notEqualToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends NumericValue> Wrapper<S> notEqualToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T op2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(op2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Test notEqualTo(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Test notEqualToOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T extends Event> Wrapper<S> notEqualToFilter(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T extends Event> Wrapper<S> notEqualToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(eventClass2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Test notEqualTo(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T> Test notEqualToOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T> Wrapper<S> notEqualToFilter(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T> Wrapper<S> notEqualToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, ?> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(handler2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Test notEqualTo(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.build();
    }

    public static < S, U, T, V> Test notEqualToOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.build();
    }

    public static < S, U, T, V> Wrapper<S> notEqualToFilter(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        return testToBuild.buildFilter();
    }

    public static < S, U, T, V> Wrapper<S> notEqualToFilterOnce(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2){
        TestBuilder<NotEqualTo, S> testToBuild = TestBuilder.buildTest(NotEqualTo.class,  supplier1, accessor1);
        testToBuild.arg(supplier2, accessor2);
        testToBuild.notifyOnChange(true);
        return testToBuild.buildFilter();
    }

}
