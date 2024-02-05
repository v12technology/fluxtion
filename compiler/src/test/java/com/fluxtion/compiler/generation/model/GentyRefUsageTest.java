/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.model;

import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.event.TimeHandlerExtends;
import com.fluxtion.test.event.TimeHandlerImpl;
import com.fluxtion.test.event.TimerHandler2Removed;
import com.googlecode.gentyref.GenericTypeReflector;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;

import static org.junit.Assert.assertEquals;

/**
 * @author Greg Higgins
 */
public class GentyRefUsageTest {

    @Test
    public void testFindEventType() {
        assertEquals(TimeEvent.class, getEventType(new TimeHandlerImpl(2)));
        assertEquals(TimeEvent.class, getEventType(new TimeHandlerExtends(2)));
        assertEquals(TimeEvent.class, getEventType(new TimerHandler2Removed(2)));
    }

    private Class getEventType(EventHandlerNode eh) {
        final ParameterizedType name = (ParameterizedType) GenericTypeReflector.getExactSuperType(eh.getClass(), EventHandlerNode.class);
        return (Class) name.getActualTypeArguments()[0];
    }
}
