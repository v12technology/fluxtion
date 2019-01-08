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
package com.fluxtion.ext.futext.builder.ascii;

import com.fluxtion.ext.futext.api.ascii.Csv2Value;
import com.fluxtion.ext.futext.api.ascii.Ascii2DoubleTerminator;
import com.fluxtion.ext.futext.api.ascii.Ascii2IntFixedLength;
import com.fluxtion.ext.futext.api.ascii.Csv2Double;
import com.fluxtion.ext.futext.api.ascii.Csv2ByteBuffer;
import com.fluxtion.ext.futext.api.ascii.Ascii2DoubleFixedLength;
import com.fluxtion.ext.futext.api.ascii.Ascii2IntTerminator;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.numeric.BufferValue;
import com.fluxtion.ext.futext.api.ascii.ByteBufferDelimiter;
import com.fluxtion.ext.futext.api.ascii.Csv2ByteBufferStringBuilder;

/**
 *
 * @author Greg Higgins
 */
public interface AsciiHelper {

    /**
     * parses text as an integer, the length is determined by a terminator(s).
     * Matching of search filter will only occur when the notifier signals a
     * change. For a new parse the notifier will need to signal again.
     *
     * @param searchFilter The preceding text to match, succeeding chars are
     * used to parse the integer value.
     * @param terminator the set of terminators that indicate field is
     * terminated.
     * @param notifier Notification instance used to signal a match/parse cycle
     * can begin
     * @return
     */
    public static Ascii2IntTerminator readInt(String searchFilter, String terminator, Object notifier) {
        final Ascii2IntTerminator ascii2Int = new Ascii2IntTerminator(notifier, terminator, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    public static Ascii2DoubleTerminator readDouble(String searchFilter, String terminator, Object notifier) {
        final Ascii2DoubleTerminator ascii2Int = new Ascii2DoubleTerminator(notifier, terminator, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    /**
     * split a stream of CharEvent's into ByteBufferDelimiters, using standard
     * delimiters and ignored chars.
     * 
     * @return ByteBufferDelimiter
     */
    public static ByteBufferDelimiter wordSplitter() {
        final ByteBufferDelimiter word = new ByteBufferDelimiter();
        GenerationContext.SINGLETON.getNodeList().add(word);
        return word;
    }

    /**
     * split a stream of CharEvent's into ByteBufferDelimiters, using specified
     * delimiters and ignored chars.
     * @param delimiterChars - word boundary chars
     * @param ignoredChars - ignored chars
     * @return ByteBufferDelimiter
     */
    public static ByteBufferDelimiter wordSplitter(String delimiterChars, String ignoredChars) {
        final ByteBufferDelimiter word = new ByteBufferDelimiter(delimiterChars, ignoredChars);
        GenerationContext.SINGLETON.getNodeList().add(word);
        return word;
    }

    /**
     * parses text as an integer, the length is determined by a terminator(s)
     *
     * @param searchFilter The preceding text to match, succeeding chars are
     * used to parse the integer value.
     * @param terminator the set of terminators that indicate field is
     * terminated.
     * @return
     */
    public static Ascii2IntTerminator readInt(String searchFilter, String terminator) {
        return readInt(searchFilter, terminator, null);
    }

    public static Ascii2DoubleTerminator readDouble(String searchFilter, String terminator) {
        return readDouble(searchFilter, terminator, null);
    }

    /**
     * parses text as an integer, the length is determined by default
     * terminators space \n and \t
     *
     * @param searchFilter The preceding text to match, succeeding chars are
     * used to parse the integer value.
     * @return
     */
    public static Ascii2IntTerminator readInt(String searchFilter) {
        final Ascii2IntTerminator ascii2Int = new Ascii2IntTerminator(null, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    public static Ascii2DoubleTerminator readDouble(String searchFilter) {
        final Ascii2DoubleTerminator ascii2Int = new Ascii2DoubleTerminator(null, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    //CSV METHODS
    public static Csv2Value readIntCsv(int fieldId) {
//        Csv2Value csvIntReader = new Csv2Value(fieldId);
//        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
//        return csvIntReader;
        return readIntDelimited(fieldId, ",", 0);
    }

    public static Csv2Value readDoubleCsv(int fieldId) {
//        Csv2Double csvIntReader = new Csv2Double(fieldId);
//        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
//        return csvIntReader;
        return readDoubleDelimited(fieldId, ",", 0);
    }

    public static BufferValue readBytesCsv(int fieldId) {
        return readBytesDelimited(fieldId, ",", 0);
    }

    public static BufferValue readCharSeqCsv(int fieldId) {
        return readCharSeqDelimited(fieldId, ",", 0);
    }
    
    public static Csv2Value readIntCsv(int fieldId, int headerLines) {
        return readIntDelimited(fieldId, ",", headerLines);
    }

    public static Csv2Value readDoubleCsv(int fieldId, int headerLines) {
        return readDoubleDelimited(fieldId, ",", headerLines);
    }

    public static BufferValue readBytesCsv(int fieldId, int headerLines) {
        return readBytesDelimited(fieldId, ",", headerLines);
    }
    
    public static BufferValue readCharSeqCsv(int fieldId, int headerLines) {
        return readCharSeqDelimited(fieldId, ",", headerLines);
    }

    //GENERIC DELIMITED METHODS
    public static Csv2Value readIntDelimited(int fieldId, String delimiters) {
//        Csv2Value csvIntReader = new Csv2Value(fieldId, delimiters, 0);
//        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
        return readIntDelimited(fieldId, delimiters, 0);
    }

    public static Csv2Value readDoubleDelimited(int fieldId, String delimiters) {
//        Csv2Value csvIntReader = new Csv2Value(fieldId, delimiters, 0);
//        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
        return readDoubleDelimited(fieldId, delimiters, 0);
    }

    public static BufferValue readBytesDelimited(int fieldId, String delimiters) {
        return readBytesDelimited(fieldId, delimiters, 0);
    }

    public static Csv2Value readIntDelimited(int fieldId, String delimiters, int headerLines) {
        Csv2Value csvIntReader = new Csv2Value(fieldId, delimiters, headerLines);
        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
        return csvIntReader;
    }

    public static Csv2Value readDoubleDelimited(int fieldId, String delimiters, int headerLines) {
        Csv2Double csvIntReader = new Csv2Double(fieldId, delimiters, headerLines);
        GenerationContext.SINGLETON.getNodeList().add(csvIntReader);
        return csvIntReader;
    }

    public static BufferValue readBytesDelimited(int fieldId, String delimiters, int headerLines) {
        Csv2ByteBuffer csvByteArrayReader = new Csv2ByteBuffer(fieldId, delimiters, headerLines);
        GenerationContext.SINGLETON.getNodeList().add(csvByteArrayReader);
        return csvByteArrayReader;
    }

    public static BufferValue readCharSeqDelimited(int fieldId, String delimiters, int headerLines) {
        Csv2ByteBufferStringBuilder csvByteArrayReader = new Csv2ByteBufferStringBuilder(fieldId, delimiters, headerLines);
        GenerationContext.SINGLETON.getNodeList().add(csvByteArrayReader);
        return csvByteArrayReader;
    }

    /**
     * parses text as an integer from a fixed length field. Matching of search
     * filter will only occur when the notifier signals a change. For a new
     * parse the notifier will need to signal again.
     *
     * @param searchFilter The preceding text to match, succeeding chars are
     * used to parse the integer value.
     * @param length length of field.
     * @param notifier Notification instance used to signal a match/parse cycle
     * @return
     */
    public static Ascii2IntFixedLength readIntFixedLength(String searchFilter, int length, Object notifier) {
        final Ascii2IntFixedLength ascii2Int = new Ascii2IntFixedLength(notifier, (byte) length, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    /**
     * parses text as an integer from a fixed length field.
     *
     *
     * @param searchFilter The preceding text to match, succeeding chars are
     * used to parse the integer value.
     * @param length length of field.
     * @return
     */
    public static Ascii2IntFixedLength readIntFixedLength(String searchFilter, int length) {
        return readIntFixedLength(searchFilter, length, null);
    }

    public static Ascii2DoubleFixedLength readDoubleFixedLength(String searchFilter, int length, Object notifier) {
        final Ascii2DoubleFixedLength ascii2Int = new Ascii2DoubleFixedLength(notifier, (byte) length, searchFilter);
        GenerationContext.SINGLETON.getNodeList().add(ascii2Int);
        return ascii2Int;
    }

    public static Ascii2DoubleFixedLength readDoubleFixedLength(String searchFilter, int length) {
        return AsciiHelper.readDoubleFixedLength(searchFilter, length, null);
    }

}
