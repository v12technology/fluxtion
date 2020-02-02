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
package com.fluxtion.ext.streaming.builder.event;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.partition.LambdaReflection;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.streaming.api.GenericWrapper;
import com.fluxtion.ext.streaming.api.IntFilterEventHandler;
import com.fluxtion.ext.streaming.api.StringFilterEventHandler;
import com.fluxtion.ext.streaming.api.Wrapper;

/**
 * Utility functions for selecting and creating a stream from incoming
 * {@link Event}
 *
 * @author Greg Higgins
 */
public interface EventSelect {

    static <T> Wrapper<T> select(Class<T> eventClazz) {
        if (Event.class.isAssignableFrom(eventClazz)) {
            Wrapper<T> handler = new IntFilterEventHandler(eventClazz);
            return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        } else {
            final GenericWrapper wrapper = GenerationContext.SINGLETON.addOrUseExistingNode(new GenericWrapper(eventClazz));
            GenerationContext.SINGLETON.addOrUseExistingNode(wrapper.getHandler());
            return wrapper;
        }
    }

    static <T , S> Wrapper<S> select(LambdaReflection.SerializableFunction<T, S> supplier) {
        Class<T> eventClazz = supplier.getContainingClass();
        return select(eventClazz).get(supplier);
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
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    }

    static <T extends Event> Wrapper<T> select(Class<T> eventClazz, int filterId) {
        Wrapper<T> handler = new IntFilterEventHandler(filterId, eventClazz);
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    }

}
