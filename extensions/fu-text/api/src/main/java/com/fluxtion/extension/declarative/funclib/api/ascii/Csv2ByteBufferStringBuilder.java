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

import com.fluxtion.api.annotations.Config;
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
 *
 * @author Greg Higgins
 */
public class Csv2ByteBufferStringBuilder implements BufferValue {

    private final StringBuilder sb = new StringBuilder(32);

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
    private transient int currentFieldNumber;
    private transient boolean parseComplete;
    private transient HashMap<ByteBuffer, String> stringCache;
    private String cachedString = "";
    private transient final String delimiterChars;

    public Csv2ByteBufferStringBuilder(int fieldNumber, String terminatorChars, int headerLines) {
        this.fieldNumber = fieldNumber;
        this.delimiterChars = terminatorChars;
        this.headerLines = headerLines;
    }

    public Csv2ByteBufferStringBuilder(int fieldNumber) {
        this(fieldNumber, ",", 0);
    }

    public Csv2ByteBufferStringBuilder() {
        delimiterChars = ",";
    }

    @Inject
    @Config(key = AnyCharMatchFilter.KEY_FILTER_ARRAY, value = "\n")
    public AnyCharMatchFilter eolNotifier;

    @OnParentUpdate("eolNotifier")
    public boolean onEol(AnyCharMatchFilter terminatorNotifier) {
        processDelimiter();
        currentFieldNumber = 0;
        headerLines--;
        headerLines = Math.max(0, headerLines);
        return parseComplete;
    }

    @Inject
    @ConfigVariable(field = "delimiterChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter delimiterNotifier;

    @OnParentUpdate("delimiterNotifier")
    public boolean onDelimiter(AnyCharMatchFilter terminatorNotifier) {
        processDelimiter();
        currentFieldNumber++;
        return parseComplete;
    }

    private void processDelimiter() {
        parseComplete = false;
        if (processCharForParse & headerLines <= 0) {
            parseComplete = true;
            cachedString = null;
        }
        if (headerLines > 0) {
            sb.setLength(0);
        }
        processCharForParse = false;
    }

    @OnEvent
    public boolean onEvent() {
        return parseComplete;
    }

    @OnEventComplete
    public void onEventComplete() {
        processCharForParse = fieldNumber == currentFieldNumber;
        parseComplete = false;
        if (processCharForParse) {
            sb.setLength(0);
        }
    }

    @EventHandler()
    public boolean appendToBuffer(CharEvent e) {
        final char character = e.getCharacter();
        if (processCharForParse ) {
//        if (processCharForParse & '\r' != character) {
            sb.append(character);
        }
        return false;
    }

    @Initialise
    public void init() {
        parseComplete = false;
        processCharForParse = fieldNumber == 0;
        stringCache = new HashMap<>();
        cachedString = "uninitialised";
    }

    @Override
    public ByteBuffer getBuffer() {
        return null;
    }

    @Override
    public ByteBuffer clone() {
        return null;
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

    public CharSequence asCharSequence() {
        return sb;
    }

    public String cacheString() {
//        cachedString = stringCache.get(duplicateBuffer);
//        if (cachedString == null) {
//            byte[] copyArray = Arrays.copyOf(array, duplicateBuffer.limit());
//            ByteBuffer wrappingBuffer = ByteBuffer.wrap(copyArray);
//            //wrappingBuffer.clear();
//            cachedString = new String(copyArray);
//            stringCache.put(wrappingBuffer, cachedString);
//        }
//        return cachedString;
        if (cachedString == null) {
            cachedString = sb.toString();
        }
        return cachedString;
    }

}
