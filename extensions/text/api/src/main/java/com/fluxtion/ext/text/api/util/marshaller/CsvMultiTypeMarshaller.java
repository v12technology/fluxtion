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
package com.fluxtion.ext.text.api.util.marshaller;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.streaming.api.numeric.BufferValue;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;
import com.fluxtion.ext.text.api.event.RegisterEventHandler;
import java.nio.ByteBuffer;
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

    private Map<ByteBuffer, com.fluxtion.api.lifecycle.StaticEventProcessor> type2Marshaller;
    private com.fluxtion.api.lifecycle.StaticEventProcessor marshaller;
    public com.fluxtion.api.lifecycle.StaticEventProcessor sink;
    private ByteBuffer buffer;
    private byte[] array;
    private static final int DEFAULT_SIZE = 256;
    private int fieldNumber;

    @OnParentUpdate("type")
    public boolean onTypeUpdated(BufferValue type) {
        CharSequence name = type.asCharSequence();
        buffer.clear();
        for (int j = 0; j < name.length(); j++) {
            array[j] = (byte) name.charAt(j);
        }
        buffer.position(0);
        buffer.limit(name.length());
        marshaller = type2Marshaller.get(buffer);
        fieldNumber = 0;
        return false;
    }

    @EventHandler
    public void pushCharToMarshaller(CharEvent charEvent) {
        if (marshaller != null & fieldNumber > 0) {
            marshaller.onEvent(charEvent);
        }
        if (charEvent.getCharacter() == '\n') {
            marshaller = null;
            fieldNumber = 0;
        } else {
            fieldNumber++;
        }
    }

    public void setSink(com.fluxtion.api.lifecycle.StaticEventProcessor sink) {
        this.sink = sink;
        if (sink != null) {
            type2Marshaller.values().forEach(h -> h.onEvent(new RegisterEventHandler(sink)));
        }
    }

    public void addMarshaller(Class wrapper, com.fluxtion.api.lifecycle.StaticEventProcessor handler) {
        if (handler != null && handler instanceof Lifecycle) {
            ((Lifecycle) handler).init();
        }
        char[] chars = wrapper.getSimpleName().toCharArray(); 
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte)chars[i];
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        type2Marshaller.put(buffer, handler);
        if (sink != null) {
            handler.onEvent(new RegisterEventHandler(sink));
        }
    }

    @Initialise
    public void init() {
        type2Marshaller = new HashMap<>();
        array = new byte[DEFAULT_SIZE];
        buffer = ByteBuffer.wrap(array);
        marshaller = null;
    }
    
    @TearDown
    public void tearDown() {
        type2Marshaller.forEach((k, v) -> v.onEvent(EofEvent.EOF));
        sink.onEvent(EofEvent.EOF);
    }

}
