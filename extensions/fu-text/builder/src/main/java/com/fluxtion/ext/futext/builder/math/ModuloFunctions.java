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
package com.fluxtion.ext.futext.builder.math;

import com.fluxtion.ext.futext.api.math.BinaryFunctions.Modulo;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilder;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.api.event.Event;
import java.util.function.Function;

/**
* @author Greg Higgins
*/
public interface ModuloFunctions {


    public static  < S extends Number, T extends Number> NumericValue modulo(S op1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T extends Number> NumericValue modulo(S op1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T extends NumericValue> NumericValue modulo(S op1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T extends NumericValue> NumericValue modulo(S op1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T extends Event> NumericValue modulo(S op1, Class<T> eventClass2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(eventClass2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T extends Event> NumericValue modulo(S op1, Class<T> eventClass2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(eventClass2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T> NumericValue modulo(S op1, Wrapper<T> handler2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(handler2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T> NumericValue modulo(S op1, Wrapper<T> handler2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(handler2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T, V extends Number> NumericValue modulo(S op1, T supplier2, SerializableSupplier<T, V> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(supplier2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Number, T, V extends Number> NumericValue modulo(S op1, T supplier2, SerializableSupplier<T, V> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(supplier2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends Number> NumericValue modulo(S op1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends Number> NumericValue modulo(S op1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends NumericValue> NumericValue modulo(S op1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends NumericValue> NumericValue modulo(S op1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends Event> NumericValue modulo(S op1, Class<T> eventClass2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(eventClass2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T extends Event> NumericValue modulo(S op1, Class<T> eventClass2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(eventClass2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T> NumericValue modulo(S op1, Wrapper<T> handler2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(handler2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T> NumericValue modulo(S op1, Wrapper<T> handler2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(handler2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T, V extends Number> NumericValue modulo(S op1, T supplier2, SerializableSupplier<T, V> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(supplier2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends NumericValue, T, V extends Number> NumericValue modulo(S op1, T supplier2, SerializableSupplier<T, V> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(op1);
        functionBuilder.input(supplier2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends Number> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends Number> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends NumericValue> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends NumericValue> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends Event> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, Class<T> eventClass2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T extends Event> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, Class<T> eventClass2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(handler2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(handler2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T, V extends Number> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T supplier2, SerializableSupplier<T, V> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S extends Event, T, V extends Number> NumericValue modulo(Class<S> eventClass1, Function<S, Number> accessor1, T supplier2, SerializableSupplier<T, V> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(eventClass1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends Number> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends Number> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends NumericValue> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends NumericValue> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends Event> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, Class<T> eventClass2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T extends Event> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, Class<T> eventClass2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(handler2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(handler2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T, V extends Number> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T supplier2, SerializableSupplier<T, V> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, T, V extends Number> NumericValue modulo(Wrapper<S> handler1, Function<S, Number> accessor1, T supplier2, SerializableSupplier<T, V> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(handler1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends Number> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends Number> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends NumericValue> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T op2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(op2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends NumericValue> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T op2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(op2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends Event> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T extends Event> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, Class<T> eventClass2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(eventClass2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(handler2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, Wrapper<T> handler2, Function<T, Number> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(handler2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T, V extends Number> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static  < S, U extends Number, T, V extends Number> NumericValue modulo(S supplier1, SerializableSupplier<S, U> accessor1, T supplier2, SerializableSupplier<T, V> accessor2, Object resetNotifier) throws Exception {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Modulo.class);
        functionBuilder.input(supplier1, accessor1);
        functionBuilder.input(supplier2, accessor2);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }
}
