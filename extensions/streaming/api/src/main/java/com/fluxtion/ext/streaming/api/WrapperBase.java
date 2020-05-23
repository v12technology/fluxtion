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

import com.fluxtion.api.SepContext;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.ext.streaming.api.stream.StreamOperator;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T> The wrapped type
 * @param <R> The type of the subclass that extends WrapperBase
 */
public interface WrapperBase<T, R extends WrapperBase<T, R>> {

    LongAdder counter = new LongAdder();

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
     * dump this node to console, prefixed with the supplied
     * message.{@link Object#toString()} will be invoked on the node instance.
     *
     * @param <S>
     * @param prefix String prefix for the console message
     * @param supplier
     * @return The current node
     */
    default <S> R console(String prefix, LambdaReflection.SerializableFunction<T, S>... supplier) {
        
        if(!prefix.contains("{}")){
            prefix += " {}";
        }
       StreamOperator.service().log(self(), prefix, supplier);
//        
//        
//        StreamOperator.ConsoleLog consoleLog = new StreamOperator.ConsoleLog(this, prefix);
//        counter.increment();
//        if (supplier.length == 0 && Number.class.isAssignableFrom(eventClass())) {
//            consoleLog.suppliers(Number::doubleValue);
//        } else {
//            consoleLog.suppliers(supplier);
//        }
//        String consoleId = "consoleMsg_" + counter.intValue();
//        SepContext.service().add(consoleLog, consoleId);
        return self();
    }

    default R self() {
        return (R) this;
    }

    default R id(String id) {
         return (R)StreamOperator.service().nodeId(this, id);
    }

}
