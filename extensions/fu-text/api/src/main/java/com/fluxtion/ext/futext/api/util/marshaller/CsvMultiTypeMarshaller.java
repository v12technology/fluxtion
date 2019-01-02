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
package com.fluxtion.ext.futext.api.util.marshaller;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.numeric.BufferValue;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.Lifecycle;
import java.util.HashMap;
import java.util.Map;

/**
 * Marshals csv records into instances and can optionally push the instance into
 * a registered EventHandler if the marshalled instance implements the Event
 * interface.
 *
 * The first column of the csv record is the simple class name of the target
 * instance. The registered handler processes CharEvent's and will marshal the
 * record into a wrapped instance. To register a handler use the addMarshaller
 * method providing the EventHandler that will process the CharEvent's and a
 * reference to the wrapped instance that is the target.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class CsvMultiTypeMarshaller {

    public BufferValue type;

    private Map<String, HandlerWrapperPair> type2Wrapper;
    private HandlerWrapperPair pair;
    public com.fluxtion.api.lifecycle.EventHandler sink;
    private char eolChar = '\n';
    private int fieldNumber;

    @OnParentUpdate("type")
    public boolean onTypeUpdated(BufferValue type) {
        pair = type2Wrapper.get(type.asString());
        fieldNumber = 0;
        return false;
    }

    @EventHandler
    public void pushCharToMarshaller(CharEvent charEvent) {
        if (pair != null & fieldNumber>0) {
            pair.handler.onEvent(charEvent);
            if (charEvent.getCharacter() == eolChar) {
                if (sink != null & pair != null && pair.wrapper.event() instanceof Event) {
                    sink.onEvent((Event) pair.wrapper.event());
                }
                pair = null;
            }
        }
        fieldNumber++;
    }

    public void addMarshaller(Wrapper wrapper, com.fluxtion.api.lifecycle.EventHandler handler) {
        if (handler != null && handler instanceof Lifecycle) {
            ((Lifecycle) handler).init();
        }
        type2Wrapper.put(wrapper.eventClass().getSimpleName(), new HandlerWrapperPair(handler, wrapper));
    }

    @Initialise
    public void init() {
        type2Wrapper = new HashMap<>();
        pair = null;
    }

    public char getEolChar() {
        return eolChar;
    }

    public void setEolChar(char eolChar) {
        this.eolChar = eolChar;
    }

    private static class HandlerWrapperPair {

        public HandlerWrapperPair(com.fluxtion.api.lifecycle.EventHandler handler, Wrapper wrapper) {
            this.handler = handler;
            this.wrapper = wrapper;
        }
        com.fluxtion.api.lifecycle.EventHandler handler;
        Wrapper wrapper;
    }
}
