/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.generator.model;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 * @author Greg Higgins
 */
public class CbMethodHandle {

    /**
     * The callback method.
     */
    public final Method method;
    /**
     * the instance the method will operate on.
     */
    public final Object instance;
    /**
     * the variable name of the instance in the SEP.
     */
    public final String variableName;
    
    /**
     * the parameter type of the callback - can be null
     */
    public final Class<?> parameterClass;
    
    /**
     * indicates is an eventhandler method
     */
    public final boolean isEventHandler;
    
    public final boolean isPostEventHandler;
    
    public final boolean isInvertedDirtyHandler;

    public CbMethodHandle(Method method, Object instance, String variableName) {
        this(method, instance, variableName, null, false);
    }

    public CbMethodHandle(Method method, Object instance, String variableName, Class<?> parameterClass, boolean isEventHandler) {
        this.method = method;
        this.instance = instance;
        this.variableName = variableName;
        this.parameterClass = parameterClass;
        this.isEventHandler = isEventHandler;
        this.isPostEventHandler = method.getAnnotation(OnEventComplete.class) != null; 
        this.isInvertedDirtyHandler =  method.getAnnotation(OnEvent.class)!=null && !method.getAnnotation(OnEvent.class).dirty();
    }

    @Override
    public String toString() {
        return "CbMethodHandle{" + "method=" + method + ", instance=" + instance + ", variableName=" + variableName + ", parameterClass=" + parameterClass + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.method);
        hash = 23 * hash + Objects.hashCode(this.instance);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CbMethodHandle other = (CbMethodHandle) obj;
        if (!Objects.equals(this.method, other.method)) {
            return false;
        }
        if (!Objects.equals(this.instance, other.instance)) {
            return false;
        }
        return true;
    }    

}
