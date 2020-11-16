/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.DefaultNumberWrapper.DefaultDoubleWrapper;
import com.fluxtion.ext.streaming.api.numeric.DefaultNumberWrapper.DefaultIntWrapper;
import com.fluxtion.ext.streaming.api.numeric.DefaultNumberWrapper.DefaultLongWrapper;
import com.fluxtion.ext.streaming.api.numeric.EventMutableNumber;
import com.fluxtion.ext.streaming.api.numeric.EventMutableNumber.EventMutableDouble;
import com.fluxtion.ext.streaming.api.numeric.EventMutableNumber.EventMutableInt;
import com.fluxtion.ext.streaming.api.numeric.EventMutableNumber.EventMutableLong;
import com.fluxtion.ext.streaming.api.numeric.NumericSignal;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;

/**
 * Add Wrapper<Number> nodes to a graph that are controlled by either events {@link NumericSignal} or extracted from
 * a property of a parent node. A default values for the number can be set. A default value can be used in a calculation
 * with requiting an initial event.
 *
 * @author V12 Technology Ltd.
 */
public class DefaultNumberBuilder {

    public static <T> Wrapper<Number> defaultNum(int value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(supplier, value));
    }

    public static <T> Wrapper<Number> defaultNum(double value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(supplier, value));
    }

    public static <T> Wrapper<Number> defaultNum(long value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(supplier, value));
    }

    public static <T> Wrapper<Number> defaultNum(int value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultNum(double value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultNum(long value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultNum(int value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(stream(supplier), value));
    }

    public static <T> Wrapper<Number> defaultNum(double value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(stream(supplier), value));
    }

    public static <T> Wrapper<Number> defaultNum(long value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(stream(supplier), value));
    }
    
    public static Wrapper<Number> defaultNum(int value, String key) {
        EventMutableNumber num = new EventMutableInt(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultNum(double value, String key) {
        EventMutableNumber num = new EventMutableDouble(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultNum(long value, String key) {
        EventMutableNumber num = new EventMutableLong(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultNum(String key) {
        EventMutableNumber num = new EventMutableNumber(key);
        return SepContext.service().addOrReuse(num);
    }
}
