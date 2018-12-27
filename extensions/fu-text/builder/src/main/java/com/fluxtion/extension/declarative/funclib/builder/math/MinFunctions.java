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
package com.fluxtion.extension.declarative.funclib.builder.math;

import com.fluxtion.extension.declarative.funclib.api.math.UnaryFunctions.Min;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.api.EventWrapper;
import com.fluxtion.extension.declarative.builder.function.NumericFunctionBuilder;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.runtime.event.Event;
import java.util.function.Function;

/**
 *
 * @author greg higgins
 */
public interface MinFunctions {

    /**
     * 
     * @param numeric
     * @return
     */
    public static NumericValue min(NumericValue numeric) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(numeric);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static NumericValue min(
        NumericValue numeric, 
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
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
    public static <T extends Event>NumericValue min(
        Class<T> eventClass, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(eventClass, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue min(
        Class<T> eventClass, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
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
    public static <T extends Event>NumericValue min(
        EventWrapper<T> eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T extends Event>NumericValue min(
        EventWrapper<T> eventHandler, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(eventHandler, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue min(
        Wrapper<T> functionWrapper, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(functionWrapper, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }
 
    public static <T>NumericValue min(
        Wrapper<T> functionWrapper, 
        Function<T, ? super Number> getter,
        Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
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
    public static <T>NumericValue min(
        T eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue min(T eventHandler, 
       Function<T, ? super Number> getter,
       Object resetNotifier
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Min.class);
        functionBuilder.input(eventHandler, getter, true);
        functionBuilder.resetNotifier(resetNotifier);
        NumericValue function = functionBuilder.build();
        return function;
    }
  
}
