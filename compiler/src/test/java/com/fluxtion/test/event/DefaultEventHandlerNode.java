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
package com.fluxtion.test.event;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.node.EventHandlerNode;

/**
 * @param <T>
 * @author Greg Higgins
 */
public final class DefaultEventHandlerNode<T> implements EventHandlerNode<T> {

    private int filterId;
    private Class<T> eventClass;
    public T event;

    public DefaultEventHandlerNode(Class<T> eventClass) {
        this.eventClass = eventClass;
        filterId = Event.NO_INT_FILTER;
    }


    public DefaultEventHandlerNode(int filterId, Class<T> eventClass) {
        this.filterId = filterId;
        this.eventClass = eventClass;
    }

    public DefaultEventHandlerNode() {
    }

    @Override
    public int filterId() {
        return filterId;
    }

    @Override
    public <E extends T> boolean onEvent(E e) {
        this.event = e;
        return true;
    }

    @Override
    public Class<T> eventClass() {
        return eventClass;
    }

}
