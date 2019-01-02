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
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.futext.api.math.UnaryFunctions.Abs;
import com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilder;
import com.fluxtion.api.event.Event;
import java.util.function.Function;

/**
 *
 * @author greg higgins
 */
public interface AbsFunctions {

    /**
     * 
     * @param numeric
     * @return
     */
    public static NumericValue abs(NumericValue numeric) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Abs.class);
        functionBuilder.input(numeric);
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
    public static <T extends Event>NumericValue abs(
        Class<T> eventClass, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Abs.class);
        functionBuilder.input(eventClass, getter, true);
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
    public static <T extends Event>NumericValue abs(
        EventWrapper<T> eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Abs.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }

    public static <T>NumericValue abs(
        Wrapper<T> functionWrapper, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Abs.class);
        functionBuilder.input(functionWrapper, getter, true);
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
    public static <T>NumericValue abs(
        T eventHandler, 
        Function<T, ? super Number> getter
    ) {
        NumericFunctionBuilder functionBuilder = NumericFunctionBuilder.function(Abs.class);
        functionBuilder.input(eventHandler, getter, true);
        NumericValue function = functionBuilder.build();
        return function;
    }
 
}
