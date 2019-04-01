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
package com.fluxtion.ext.text.api.ascii;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.numeric.BufferValue;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.filter.AnyCharMatchFilter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Greg Higgins
 */
public class Csv2ByteBuffer implements BufferValue {

    private static final int DEFAULT_ARRAY_SIZE = 20;

    /**
     * field number to extract value from, zero indexed
     */
    public int fieldNumber;

    /**
     * Number of headlines to ignore
     */
    public int headerLines;
    /**
     * internal flag, if true the char event should be used for parsing the
     * intValue
     */
    protected transient boolean processCharForParse;

    /**
     * the actual intValue
     */
    private transient byte[] array;
    private transient int currentFieldNumber;
    private transient boolean parseComplete;
    private transient ByteBuffer buffer;
    private transient ByteBuffer duplicateBuffer;
//    private transient ByteBuffer duplicateBufferRo;
    private transient HashMap<ByteBuffer, String> stringCache;
    private String cachedString = "";

    public Csv2ByteBuffer(int fieldNumber, String terminatorChars, int headerLines) {
        this.fieldNumber = fieldNumber;
        this.terminatorChars = terminatorChars;
        this.headerLines = headerLines;
    }

    public Csv2ByteBuffer(int fieldNumber) {
        this(fieldNumber, ",", 0);
    }

    public Csv2ByteBuffer() {
        terminatorChars = ",";
    }

    @Inject
    @Config(key = AnyCharMatchFilter.KEY_FILTER_ARRAY, value = "\n")
    public AnyCharMatchFilter eolNotifier;

//    @EventHandler(filterId = '\n')
//    public boolean onEol(CharEvent e) {
    @OnParentUpdate("eolNotifier")
    public boolean onEol(AnyCharMatchFilter terminatorNotifier) {
        processDelimiter();
        currentFieldNumber = 0;
        headerLines--;
        headerLines = Math.max(0, headerLines);
        return parseComplete;
    }

    @Inject
    @ConfigVariable(field = "terminatorChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter delimiterNotifier;

    private transient final String terminatorChars;

    @OnParentUpdate("delimiterNotifier")
    public boolean onDelimiter(AnyCharMatchFilter terminatorNotifier) {
        processDelimiter();
        currentFieldNumber++;
        return parseComplete;
    }

    private void processDelimiter() {
        parseComplete = false;
        if (processCharForParse & headerLines <= 0) {
            //extract value and reset 
            parseComplete = true;
            duplicateBuffer.position(0);
            duplicateBuffer.limit(buffer.position());
            buffer.clear();
            cachedString = null;
//            System.out.println("flipped:" + buffer + " '" + new String(getBuffer().array()) + "'");
        }

        if (headerLines > 0) {
            buffer.clear();
            duplicateBuffer.clear();
        }
        processCharForParse = false;
    }

    @OnEvent
    public boolean onEvent() {
//        cacheString();
        return parseComplete;
    }

    @OnEventComplete
    public void onEventComplete() {
        processCharForParse = fieldNumber == currentFieldNumber;
        parseComplete = false;
    }

    @EventHandler()
    public boolean appendToBuffer(CharEvent e) {
        if (processCharForParse) {
            if (buffer.hasRemaining() & '\r' != e.getCharacter()) {
                buffer.put((byte) e.getCharacter());
            } else {
                final int length = buffer.position();
                array = Arrays.copyOf(array, array.length * 2);
                buffer = ByteBuffer.wrap(array);
                buffer.position(length);
                buffer.put((byte) e.getCharacter());
                duplicateBuffer = buffer.duplicate();
            }
        }
        return false;
    }

    @Initialise
    public void init() {
        parseComplete = false;
        processCharForParse = fieldNumber == 0;
        array = new byte[DEFAULT_ARRAY_SIZE];
        buffer = ByteBuffer.wrap(array);
        duplicateBuffer = buffer.duplicate();
        stringCache = new HashMap<>();
        cachedString = "uninitialised";
//        duplicateBufferRo = duplicateBuffer.asReadOnlyBuffer();
    }

    @Override
    public ByteBuffer getBuffer() {
        //TODO cache buffer on the 
        return duplicateBuffer;
    }

    @Override
    public ByteBuffer clone() {
        return ByteBuffer.wrap(Arrays.copyOf(array, duplicateBuffer.limit()));
    }

    @Override
    public String toString() {
        return asString();
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
        cachedString = stringCache.get(duplicateBuffer);
        if (cachedString == null) {
            byte[] copyArray = Arrays.copyOf(array, duplicateBuffer.limit());
            ByteBuffer wrappingBuffer = ByteBuffer.wrap(copyArray);
            //wrappingBuffer.clear();
            cachedString = new String(copyArray);
            stringCache.put(wrappingBuffer, cachedString);
        }
        return cachedString;
    }

//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 59 * hash + Objects.hashCode(this.duplicateBuffer);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Csv2ByteBuffer other = (Csv2ByteBuffer) obj;
//        if (!Objects.equals(this.duplicateBuffer, other.duplicateBuffer)) {
//            return false;
//        }
//        return true;
//    }
}
