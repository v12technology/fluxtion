/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.api.event;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.EqualsAndHashCode;

/**
 * A node in a SEP that publishes an {@link Event} to a registered
 * {@link EventHandler}. Listens to {@link RegisterEventHandler} events to 
 * register and de-register {@link EventHandler} as sinks.
 *
 * @author V12 Technology Ltd.
 */
@EqualsAndHashCode
public class EventPublisher<T extends Event> {

    public Event[] nodeSource = new Event[0];
    private EventHandler<T>[] handlers;

    public EventPublisher() {
        init();
    }
    
    @OnParentUpdate("nodeSource")
    public void nodeUpdate(T source) {
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].onEvent(source);
        }
    }

    public EventPublisher<T> addEventSource(T node) {
        ArrayList<Event> nodes = new ArrayList<>(Arrays.asList(nodeSource));
        nodes.add(node);
        nodeSource = nodes.toArray(nodeSource);
        return this;
    }

    @com.fluxtion.api.annotations.EventHandler
    public void registerEventHandler(RegisterEventHandler registration) {
        ArrayList<EventHandler> nodes = new ArrayList<>(Arrays.asList(handlers));
        if (registration.isRegister()) {
            nodes.add(registration.getHandler());
        } else {
            nodes.remove(registration.getHandler());
        }
        handlers = nodes.toArray(handlers);
    }

    @Initialise
    public final void init() {
        if (handlers == null) {
            handlers = new EventHandler[0];
        }
//        if (nodeSource == null) {
//            nodeSource = new Event[0];
//        }
    }

}
