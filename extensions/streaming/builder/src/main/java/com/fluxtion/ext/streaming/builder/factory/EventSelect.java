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
package com.fluxtion.ext.streaming.builder.factory;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.IntFilterEventHandler;
import com.fluxtion.ext.streaming.api.StringFilterEventHandler;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.builder.factory.Primitive2NumberStreamBuilder.primitive2Num;

/**
 * Utility functions for selecting and creating a stream from incoming
 * {@link Event}'s
 *
 * @author Greg Higgins
 */
public interface EventSelect {

    static <T> Wrapper<T> select(Class<T> eventClazz) {
            Wrapper<T> handler = new IntFilterEventHandler(eventClazz);
            return SepContext.service().addOrReuse(handler);
    }

    static <T , S> Wrapper<S> select(SerializableFunction<T, S> supplier) {
        Class<T> eventClazz = (Class<T>) supplier.getContainingClass();
        return select(eventClazz).get(supplier);
    }

    static <T extends Number> Wrapper<Number> selectNumber(Class<T> eventClaz) {
        return primitive2Num(select(eventClaz));
    }

    static <T , S extends Number> Wrapper<Number> selectNumber(SerializableFunction<T, S> supplier) {
        return primitive2Num(supplier);
    }

    static <T extends Event> Wrapper<T>[] select(Class<T> eventClazz, int... filterId) {
        Wrapper[] result = new Wrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(eventClazz, filterId[i]);
        }
        return result;
    }

    static <T extends Event> Wrapper<T>[] select(Class<T> eventClazz, String... filterId) {
        Wrapper[] result = new Wrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(eventClazz, filterId[i]);
        }
        return result;
    }

    static <T extends Event> Wrapper<T> select(Class<T> eventClazz, String filterId) {
        Wrapper<T> handler = new StringFilterEventHandler(filterId, eventClazz);
        return SepContext.service().addOrReuse(handler);
    }

    static <T extends Event> Wrapper<T> select(Class<T> eventClazz, int filterId) {
        Wrapper<T> handler = new IntFilterEventHandler(filterId, eventClazz);
        return SepContext.service().addOrReuse(handler);
    }
    
    static <T extends Event, S> Wrapper<S> select(SerializableFunction<T, S> supplier, String filterId) {
        Class eventClazz = supplier.getContainingClass();
        return select( eventClazz, filterId).get(supplier);
    }
    
    static <T extends Event, S> Wrapper<S> select(SerializableFunction<T, S> supplier, int filterId) {
        Class eventClazz = supplier.getContainingClass();
        return select( eventClazz, filterId).get(supplier);
    }

    
    static <T extends Event, S> Wrapper<T>[] select(SerializableFunction<T, S> supplier, int... filterId) {
        Wrapper[] result = new Wrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(supplier, filterId[i]);
        }
        return result;
    }

    static <T extends Event, S> Wrapper<T>[] select(SerializableFunction<T, S> supplier, String... filterId) {
        Wrapper[] result = new Wrapper[filterId.length];
        for (int i = 0; i < filterId.length; i++) {
            result[i] = select(supplier, filterId[i]);
        }
        return result;
    }
}
