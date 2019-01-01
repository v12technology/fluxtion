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
package com.fluxtion.ext.futext.api.ascii;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterType;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.ext.declarative.api.numeric.BufferValue;
import com.fluxtion.ext.futext.api.event.CharEvent;
import java.nio.ByteBuffer;

/**
 * Stores bytes in a StringBuilder.
 * 
 * @author Greg Higgins
 */
public class Csv2ByteBufferTemp implements BufferValue {

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
    private String cachedString = "";

    public Csv2ByteBufferTemp(int fieldNumber, String terminatorChars, int headerLines) {
        this.fieldNumber = fieldNumber;
        this.headerLines = headerLines;
    }

    public Csv2ByteBufferTemp(int fieldNumber) {
        this(fieldNumber, ",", 0);
    }

    public Csv2ByteBufferTemp() {
    }


    @EventHandler(filterId = '\n')
    public boolean onEol(CharEvent e) {
        processDelimiter();
        currentFieldNumber = 0;
        headerLines--;
        headerLines = Math.max(0, headerLines);
        return parseComplete;
    }


    @EventHandler(filterId = ',')
    public boolean onDelimiter(CharEvent e) {
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

//    @OnEvent
    public boolean onEvent() {
        return parseComplete;
    }

    @AfterEvent
    public void onEventComplete() {
        processCharForParse = fieldNumber == currentFieldNumber;
        parseComplete = false;
        if (!processCharForParse) {
            sb.setLength(0);
        }
    }

    @EventHandler(propagate = false, value = FilterType.unmatched)
    public boolean appendToBuffer(CharEvent e) {
        if (processCharForParse ) {
            sb.append(e.getCharacter());
        }
        return false;
    }

    @Initialise
    public void init() {
        parseComplete = false;
        processCharForParse = fieldNumber == 0;
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
            cachedString = sb.toString();
        }
        return cachedString;
    }
   public CharSequence asCharSequence() {
        return sb;
    }

}
