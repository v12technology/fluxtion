/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;

/**
 * TODO refactor to combine Wrapper
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T> The wrapped type
 * @param <R> The type of the subclass that extends WrapperBase
 */
public interface WrapperBase<T, R extends WrapperBase<T, R>> {

    /**
     * The wrapped node
     *
     *
     * @return the wrapped node
     */
    //TODO refactor to wrapped
    T event();

    /**
     * The type of the wrapped node
     *
     * @return wrapped node class
     */
    //TODO refactor to wrappedClass
    Class<T> eventClass();

    /**
     *
     * @param <S>
     * @param prefix String prefix for the log message
     * @param supplier
     * @return The current node
     */
    default <S> R log(String prefix, LambdaReflection.SerializableFunction<T, S>... supplier) {
        if(!prefix.contains("{}")){
            prefix += " {}";
        }
       StreamOperator.service().log(self(), prefix, supplier);
        return self();
    }
    
    default <S> R log(){
        return log("");
    }

    default R self() {
        return (R) this;
    }

    default R id(String id) {
         return (R)StreamOperator.service().nodeId(this, id);
    }

}
