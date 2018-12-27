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
package com.fluxtion.extension.declarative.funclib.api.ascii;


/**
 *
 * @author Greg Higgins
 */
import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.extension.declarative.api.numeric.BufferValue;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;
import com.fluxtion.extension.declarative.funclib.api.filter.AnyCharMatchFilter;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Extract a "word" from a CharEvent stream. Words are broken on delimiters. A
 * set of ignore characters can be optionally provided
 * @author Greg Higgins
 */
public class ByteBufferDelimiter implements BufferValue {

    private static final int DEFAULT_ARRAY_SIZE = 16;

    private transient boolean parseComplete;
    private transient ByteBuffer buffer;
    private transient HashMap<ByteBuffer, String> stringCache;
    private String cachedString = "";
    private final String delimiterChars;
    private final String ignoreChars;
    private char[] ignoreCharArr;

    public ByteBufferDelimiter(String delimiterChars, String ignoreChars) {
        this.delimiterChars = delimiterChars;
        this.ignoreChars = ignoreChars;
        ignoreCharArr = (delimiterChars + ignoreChars).toCharArray();
    }

    public ByteBufferDelimiter() {
        this(" \n", ",.;-_:[]{}()?|&*'\\\"\r");
    }

    @Inject
    @ConfigVariable(field = "delimiterChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter delimiterNotifier;

    @OnParentUpdate("delimiterNotifier")
    public boolean onDelimiter(AnyCharMatchFilter terminatorNotifier) {
        parseComplete = buffer.position() > 0;
        cachedString = null;
        return parseComplete;
    }

    @OnEvent
    public boolean onEvent() {
        return parseComplete;
    }

    @OnEventComplete
    public void onEventComplete() {
        buffer.clear();
        parseComplete = false;
    }

    @EventHandler()
    public boolean appendToBuffer(CharEvent e) {
        char c = e.getCharacter();
        if (ignoreChar(c)) {
            //do nothing
        } else if (buffer.hasRemaining()) {
            buffer.put((byte) c);
        } else {
            buffer.flip();
            buffer = ByteBuffer.allocateDirect(buffer.limit() * 2).put(buffer);
            buffer.put((byte) c);
        }
        return false;
    }

    private boolean ignoreChar(char c) {
        for (int i = 0; i < ignoreCharArr.length; i++) {
            if (c == ignoreCharArr[i]) {
                return true;
            }
        }
        return false;
    }

    @Initialise
    public void init() {
        parseComplete = false;
        buffer = ByteBuffer.allocateDirect(DEFAULT_ARRAY_SIZE);
        stringCache = new HashMap<>();
        cachedString = "uninitialised";
    }

    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public ByteBuffer clone() {
        final int limit = buffer.limit();
        ByteBuffer newBuf = ByteBuffer.allocateDirect(buffer.capacity());
        buffer.flip();
        newBuf.put(buffer);
        buffer.limit(limit);
        return newBuf;
    }

    @Override
    public String toString() {
        return cachedString;//asString();
    }

    @Override
    public String asString() {
        if (cachedString == null) {
            return cacheString();
        } else {
            return cachedString;
        }
    }

    public String cacheString() {
        int lim = buffer.limit();
        int pos = buffer.position();
        buffer.flip();
        cachedString = stringCache.get(buffer);
        buffer.limit(lim);
        buffer.position(pos);
        if (cachedString == null) {
            ByteBuffer wrappingBuffer = clone();
            wrappingBuffer.flip();
            byte[] arr = new byte[wrappingBuffer.limit()];
            wrappingBuffer.get(arr);
            wrappingBuffer.flip();
            cachedString = new String(arr);
            stringCache.put(wrappingBuffer, cachedString);
        }
        return cachedString;
    }

}

