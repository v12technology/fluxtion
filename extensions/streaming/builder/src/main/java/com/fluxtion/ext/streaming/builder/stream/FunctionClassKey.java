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
package com.fluxtion.ext.streaming.builder.stream;

import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.Argument;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author gregp
 */
@Data
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

}
