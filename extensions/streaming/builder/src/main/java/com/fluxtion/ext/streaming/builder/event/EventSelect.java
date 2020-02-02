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
import com.fluxtion.ext.streaming.api.ReusableEventHandler;
import com.fluxtion.ext.streaming.api.StringFilterEventHandler;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.builder.Templates;

/**
 * Utility functions for selecting and creating a stream from and incoming {@link Event}
 * @author Greg Higgins
 */
public interface EventSelect {

    String TEMPLATE = Templates.PACKAGE + "/EventSelectTemplate.vsl";

    static <T extends Event> Wrapper<T> select(Class<T> eventClazz) {
        Wrapper<T> handler = new ReusableEventHandler(eventClazz);
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    }

    static <T extends Event, S> Wrapper<S> select(LambdaReflection.SerializableFunction<T, S> supplier) {
        Class<T> eventClazz = supplier.getContainingClass();
        Wrapper<T> handler = new ReusableEventHandler(eventClazz);
        handler = GenerationContext.SINGLETON.addOrUseExistingNode(handler);
        return handler.get(supplier);
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
        Wrapper<T> handler = new ReusableEventHandler(filterId, eventClazz);
        return GenerationContext.SINGLETON.addOrUseExistingNode(handler);
    } 

}
