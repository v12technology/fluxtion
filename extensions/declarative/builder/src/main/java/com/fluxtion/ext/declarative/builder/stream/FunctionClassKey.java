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

import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 * @author gregp
 */
public class FunctionClassKey {

    Class filterClass;
    Method filterMethod;
    Class sourceClass;
    Method accessor;
    boolean cast;
    String type;


    public FunctionClassKey(Class filterClass, Method filterMethod, Class sourceClass, Method accessor, boolean cast, String type) {
        this.filterClass = filterClass;
        this.filterMethod = filterMethod;
        this.sourceClass = sourceClass;
        this.accessor = accessor;
        this.cast = cast;
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.filterClass);
        hash = 97 * hash + Objects.hashCode(this.filterMethod);
        hash = 97 * hash + Objects.hashCode(this.sourceClass);
        hash = 97 * hash + Objects.hashCode(this.accessor);
        hash = 97 * hash + (this.cast ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.type);
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
        if (!Objects.equals(this.accessor, other.accessor)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FunctionClassKey{" + "filterClass=" + filterClass + ", filterMethod=" + filterMethod + ", sourceClass=" + sourceClass + ", accessor=" + accessor + ", cast=" + cast + ", type=" + type + '}';
    }
    
}
