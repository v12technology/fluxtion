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

import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.futext.api.math.UnaryFunctions.CumSum;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.function.NumericFunctionBuilder;
import com.fluxtion.extension.declarative.builder.function.NumericArrayFunctionBuilder;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;

/**
 *
 * @author greg higgins
 */
public interface CumSumFunctions {

    /**
     * 
     * @param numeric
     * @return
     */
    public static NumericValue cumSum(NumericValue numeric) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(numeric);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static NumericValue cumSum(
        NumericValue numeric, 
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(numeric);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }
    
    /**
     * 
     * @param <T>
     * @param eventClass
     * @param getter
     * @return
     */
    public static <T extends Event>NumericValue cumSum(
        Class<T> eventClass, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventClass, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue cumSum(
        Class<T> eventClass, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventClass, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }
    
    /**
     * 
     * @param <T>
     * @param eventHandler
     * @param getter
     * @return
     */
    public static <T extends Event>NumericValue cumSum(
        EventWrapper<T> eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue cumSum(
        EventWrapper<T> eventHandler, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventHandler, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue cumSum(
        Wrapper<T> functionWrapper, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(functionWrapper, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }
 
    public static <T>NumericValue cumSum(
        Wrapper<T> functionWrapper, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(functionWrapper, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    /**
     * 
     * @param <T>
     * @param eventHandler
     * @param getter
     * @return
     */
    public static <T>NumericValue cumSum(
        T eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue cumSum(T eventHandler, 
       Function<T, ? super Number> getter,
       Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(CumSum.class);
        functionBuilder.input(eventHandler, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }
  
//ARRAY SUPPORT
    public static NumericValue cumSum(NumericValue... numeric) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input(numeric);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static NumericValue cumSum(
            Object resetNotifier, 
            NumericValue... numeric
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input(numeric);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    /**
     * 
     * @param <T>
     * @param eventHandler
     * @param getter
     * @return
     */
    public static <T extends Event>NumericValue cumSum(
        Function<T, ? super Number> getter,
        EventWrapper<T>... eventHandler 
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input( getter, eventHandler);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue cumSum(
        Object resetNotifier,
        Function<T, ? super Number> getter,
        EventWrapper<T>... eventHandler 
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input( getter, eventHandler);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }


    public static <T>NumericValue cumSum(
        Function<T, ? super Number> getter,
        Wrapper<T>... functionWrapper 
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input( getter, functionWrapper);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue cumSum(
        Object resetNotifier,
        Function<T, ? super Number> getter,
        Wrapper<T>... functionWrapper 
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input( getter, functionWrapper);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }


    /**
     * 
     * @param <T>
     * @param eventHandler
     * @param getter
     * @return
     */
    public static <T>NumericValue cumSum(
        Function<T, ? super Number> getter,
        T... eventHandler 
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input(getter, eventHandler);
        NumericValue function = functionBuilder.build();
        return function;
    }
    
    public static <T>NumericValue cumSum(
            Function<T, ? super Number> getter,
            Object resetNotifier,
            T... eventHandler
    ) {
        NumericArrayFunctionBuilder functionBuilder = NumericArrayFunctionBuilder.function(CumSum.class);
        functionBuilder.input(getter, eventHandler);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue cumSum(
            Class<T> eventClass,
            Function<T, ? super Number> sourceFunction,
            String... filterString
    ) {
        return NumericArrayFunctionBuilder.buildFunction(CumSum.class, 
                eventClass, 
                sourceFunction, 
                filterString);
    }
    
    public static <T extends Event>NumericValue cumSum(
            Class<T> eventClass,
            Function<T, ? super Number> sourceFunction,
            int... filterString
    ) {
        return NumericArrayFunctionBuilder.buildFunction(CumSum.class, 
                eventClass, 
                sourceFunction, 
                filterString);
    }
}
