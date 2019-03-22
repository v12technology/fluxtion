/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.util;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import java.lang.reflect.Method;

/**
 * Represents an argument for use with a function
 *
 * @author V12 Technology Ltd.
 */
public class FunctionArg {

    public Object source;
    public Method accessor;
    public boolean cast;
    
    public static <T, S> FunctionArg argI(SerializableFunction<T, S> supplier){
        
        return new FunctionArg(select(supplier.getContainingClass()), supplier.method(), true);
    }
    
    public static <T extends Event, S> FunctionArg arg(SerializableFunction<T, S> supplier){
        return new FunctionArg(select(supplier.getContainingClass()), supplier.method(), true);
    }
    
    public static FunctionArg arg(SerializableSupplier supplier){
        return new FunctionArg(supplier.captured()[0], supplier.method(), true);
    }
    
    public static FunctionArg arg(Object supplier){
        return new FunctionArg(supplier, null, true);
    }

    public FunctionArg(Object source, Method accessor, boolean cast) {
        this.source = source;
        this.accessor = accessor;
        this.cast = cast;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Method getAccessor() {
        return accessor;
    }

    public void setAccessor(Method accessor) {
        this.accessor = accessor;
    }

    public boolean isCast() {
        return cast;
    }

    public void setCast(boolean cast) {
        this.cast = cast;
    }

}
