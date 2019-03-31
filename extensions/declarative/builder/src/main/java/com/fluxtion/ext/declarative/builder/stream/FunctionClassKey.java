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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.stream.Argument;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author gregp
 */
public class FunctionClassKey {

    Class filterClass;
    Method filterMethod;
    Class sourceClass;
    Class wrappedSourceClass;
    Method accessor;
    boolean cast;
    String type;
    boolean multiArg;
    List<Argument> argsList = new ArrayList<>();

    public FunctionClassKey(Object filter, Method filterMethod, Object source, Method accessor, boolean cast, String type) {
        this.filterClass = getClassForInstance(filter);
        this.filterMethod = filterMethod;
        this.sourceClass = getClassForInstance(source);
        this.wrappedSourceClass = getWrappedClass(source);
        this.accessor = accessor;
        this.cast = cast;
        this.type = type;
    }
    
    private static Class getClassForInstance(Object o) {
        if (o == null) {
            return null;
        }
        return o.getClass();
    }
    
    private static Class getWrappedClass(Object o){
        if(o == null){
            return null;
        }
        if(o instanceof Wrapper){
            return ((Wrapper)o).eventClass();
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.filterClass);
        hash = 37 * hash + Objects.hashCode(this.filterMethod);
        hash = 37 * hash + Objects.hashCode(this.sourceClass);
        hash = 37 * hash + Objects.hashCode(this.wrappedSourceClass);
        hash = 37 * hash + Objects.hashCode(this.accessor);
        hash = 37 * hash + (this.cast ? 1 : 0);
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + (this.multiArg ? 1 : 0);
        hash = 37 * hash + Objects.hashCode(this.argsList);
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
        final FunctionClassKey other = (FunctionClassKey) obj;
        if (this.cast != other.cast) {
            return false;
        }
        if (this.multiArg != other.multiArg) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.filterClass, other.filterClass)) {
            return false;
        }
        if (!Objects.equals(this.filterMethod, other.filterMethod)) {
            return false;
        }
        if (!Objects.equals(this.sourceClass, other.sourceClass)) {
            return false;
        }
        if (!Objects.equals(this.wrappedSourceClass, other.wrappedSourceClass)) {
            return false;
        }
        if (!Objects.equals(this.accessor, other.accessor)) {
            return false;
        }
        if (!Objects.equals(this.argsList, other.argsList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FunctionClassKey{" + "filterClass=" + filterClass + ", filterMethod=" + filterMethod + ", sourceClass=" + sourceClass + ", accessor=" + accessor + ", cast=" + cast + ", type=" + type + '}';
    }
    
}
