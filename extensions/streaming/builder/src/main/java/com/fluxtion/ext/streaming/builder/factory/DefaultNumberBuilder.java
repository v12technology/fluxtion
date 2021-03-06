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
import org.apache.commons.lang3.ClassUtils;

/**
 * Add Wrapper<Number> nodes to a graph that are controlled by either events {@link NumericSignal} or extracted from a
 * property of a parent node. A default values for the number can be set. A default value can be used in a calculation
 * with requiting an initial event.
 *
 * @author V12 Technology Ltd.
 */
public class DefaultNumberBuilder {

    public static <T> Wrapper<T> defaultVal(T defaultValue, Wrapper<T> supplier) {
        if (defaultValue instanceof Number) {
            Number number = (Number) defaultValue;
            Class<?> primitiveClass = ClassUtils.wrapperToPrimitive(number.getClass());
            if(primitiveClass.getName().equalsIgnoreCase(Integer.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.intValue(), (Wrapper<Number>) supplier);
            }else if(primitiveClass.getName().equalsIgnoreCase(Short.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.intValue(), (Wrapper<Number>) supplier);
            }else if(primitiveClass.getName().equalsIgnoreCase(Byte.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.intValue(), (Wrapper<Number>) supplier);
            }else if(primitiveClass.getName().equalsIgnoreCase(Double.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.doubleValue(), (Wrapper<Number>) supplier);
            }else if(primitiveClass.getName().equalsIgnoreCase(Long.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.longValue(), (Wrapper<Number>) supplier);
            }else if(primitiveClass.getName().equalsIgnoreCase(Float.TYPE.getName())){
                return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.doubleValue(), (Wrapper<Number>) supplier);
            }
            return (Wrapper<T>) DefaultNumberBuilder.defaultVal(number.doubleValue(), (Wrapper<Number>) supplier);
        }
        throw new UnsupportedOperationException("default values for reference types are unsupported");
    }

    public static <T, R> Wrapper<R> defaultVal(T value, SerializableFunction<T, R> supplier) {
        throw new UnsupportedOperationException("default values for reference types are unsupported");
    }

    public static Wrapper<Number> defaultVal(int value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(supplier, value));
    }

    public static Wrapper<Number> defaultVal(double value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(supplier, value));
    }

    public static Wrapper<Number> defaultVal(long value, Wrapper< Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(supplier, value));
    }

    public static <T> Wrapper<Number> defaultVal(int value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultVal(double value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultVal(long value, SerializableFunction<T, Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(select(supplier), value));
    }

    public static <T> Wrapper<Number> defaultVal(int value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultIntWrapper(stream(supplier), value));
    }

    public static <T> Wrapper<Number> defaultVal(double value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultDoubleWrapper(stream(supplier), value));
    }

    public static <T> Wrapper<Number> defaultVal(long value, SerializableSupplier<Number> supplier) {
        return SepContext.service().addOrReuse(new DefaultLongWrapper(stream(supplier), value));
    }

    public static Wrapper<Number> defaultVal(int value, String key) {
        EventMutableNumber num = new EventMutableInt(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultVal(double value, String key) {
        EventMutableNumber num = new EventMutableDouble(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultVal(long value, String key) {
        EventMutableNumber num = new EventMutableLong(value, key);
        return SepContext.service().addOrReuse(num);
    }

    public static Wrapper<Number> defaultVal(String key) {
        EventMutableNumber num = new EventMutableNumber(key);
        return SepContext.service().addOrReuse(num);
    }
}
