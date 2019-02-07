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
package com.fluxtion.ext.declarative.api;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.api.partition.LambdaReflection.SerializableSupplier;

/**
 * A wrapper for a wrapped node in the SEP.
 * 
 * @author Greg Higgins
 */
public interface Wrapper<T> {

    /**
     *  The wrapped node
     * @return  the wrapped node
     */
    T event();

    /**
     * The type of the wrapped node
     * @return wrapped node class
     */
    Class<T> eventClass();
    
    /**
     * 
     * @param <R>
     * @param filter
     * @param supplier
     * @return 
     */
    default <R> Wrapper<T>  filter(SerializableFunction<T, Boolean> filter, SerializableFunction<T, R> supplier){
        return StreamOperator.service().filter(filter, this, supplier.method(), true);
    }
    

}
