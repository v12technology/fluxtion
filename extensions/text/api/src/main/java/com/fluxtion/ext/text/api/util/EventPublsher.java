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
package com.fluxtion.ext.text.api.util;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.event.Event;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A node in a SEP that publishes an {@link Event} to a registered
 * {@link EventHandler}.
 *
 * @author V12 Technology Ltd.
 */
public class EventPublsher<T extends Event> {

    private Wrapper<T>[] wrapperSource;
    private Wrapper<T>[] validatedSource;
    private T[] nodeSource;
    private EventHandler[] handlers;
    public boolean publishOnValidate = false;

    public EventPublsher() {
        init();
    }

    @OnParentUpdate("validatedSource")
    public void validatedUpdate(Wrapper<T> source) {
        if (publishOnValidate) {
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].onEvent(source.event());
            }
        }
    }

    @OnParentUpdate("wrapperSource")
    public void wrapperUpdate(Wrapper<T> source) {
        if (!publishOnValidate) {
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].onEvent(source.event());
            }
        }
    }

    @OnParentUpdate("nodeSource")
    public void nodeUpdate(T source) {
        if (!publishOnValidate) {
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].onEvent(source);
            }
        }
    }

    public void addEventSource(T node) {
        ArrayList<T> nodes = new ArrayList<>();
        if (nodeSource != null) {
            new ArrayList<>(Arrays.asList(nodeSource));
        }
        nodes.add(node);
        nodeSource = nodes.toArray(nodeSource);
    }

    public void addEventSource(Wrapper<T> node) {
        ArrayList<Wrapper<T>> nodes = new ArrayList<>(Arrays.asList(wrapperSource));
        nodes.add(node);
        wrapperSource = nodes.toArray(wrapperSource);
    }

    public void addValidatedSource(Wrapper<T> node) {
        ArrayList<Wrapper<T>> nodes = new ArrayList<>(Arrays.asList(validatedSource));
        nodes.add(node);
        validatedSource = nodes.toArray(validatedSource);
        publishOnValidate = true;
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
        if (wrapperSource == null) {
            wrapperSource = new Wrapper[0];
        }
        if (validatedSource == null) {
            validatedSource = new Wrapper[0];
        }
        if (handlers == null) {
            handlers = new EventHandler[0];
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Arrays.deepHashCode(this.wrapperSource);
        hash = 59 * hash + Arrays.deepHashCode(this.nodeSource);
        hash = 59 * hash + Arrays.deepHashCode(this.handlers);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventPublsher<?> other = (EventPublsher<?>) obj;
        if (!Arrays.deepEquals(this.wrapperSource, other.wrapperSource)) {
            return false;
        }
        if (!Arrays.deepEquals(this.nodeSource, other.nodeSource)) {
            return false;
        }
        if (!Arrays.deepEquals(this.handlers, other.handlers)) {
            return false;
        }
        return true;
    }

}
