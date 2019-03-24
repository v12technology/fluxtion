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
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.numeric.ConstantNumber;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Represents an argument for use with a function
 *
 * @author V12 Technology Ltd.
 */
public class FunctionArg<T> {

    public Object source;
    public Method accessor;
    public boolean cast;
        
    public static <T, S> FunctionArg<S> arg(SerializableFunction<T, S> supplier){
        final Class containingClass = supplier.getContainingClass();
        if(Event.class.isAssignableFrom(containingClass)){
            return new FunctionArg(select(containingClass), supplier.method(), true);
        }else{
            try {
                return new FunctionArg(containingClass.newInstance(), supplier.method(), true);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("default constructor missing for class:'" + containingClass + "'");
            }
        }        
    }
    
    public static <T extends Number> FunctionArg<T> arg(Double d){
        SerializableFunction<Number, Double> s = Number::doubleValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }
    
    public static <T extends Number> FunctionArg<T> arg(int d){
        SerializableFunction<Number, Integer> s = Number::intValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }
    public static <T extends Number> FunctionArg<T> arg(long d){
        SerializableFunction<Number, Long> s = Number::longValue;
        return new FunctionArg(new ConstantNumber(d), s.method(), true);
    }
    
    public static <T extends Number> FunctionArg<T> arg(Wrapper<T> wrapper){
        return arg(wrapper, Number::doubleValue);
    }
    
    public static <T, S> FunctionArg<T> arg(Wrapper<T> wrapper,SerializableFunction<T, S> supplier){
        return new FunctionArg(wrapper, supplier.method(), true);
    }
    
    public static <T> FunctionArg<T> arg(SerializableSupplier<T> supplier){
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.source);
        hash = 67 * hash + Objects.hashCode(this.accessor);
        hash = 67 * hash + (this.cast ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FunctionArg<?> other = (FunctionArg<?>) obj;
        if (this.cast != other.cast) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.accessor, other.accessor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FunctionArg{" + "source=" + source + ", accessor=" + accessor + ", cast=" + cast + '}';
    }
    
    

}
