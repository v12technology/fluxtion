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

import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.filter.AnyCharMatchFilter;


/**
 * Extracts a csv field into a numeric value, this class supports integer values
 * only. 
 * 
 * @author Greg Higgins
 */
public class Csv2Value extends Number  {

    /**
     * field number to extract value from, zero indexed
     */
    public int fieldNumber;
    /**
     * Number of headlines to ignore
     */
    public int headerLines;
    /**
     * decimal point tracking
     */
    protected transient int pointPos, pointIncrement;
    /**
     * internal flag, if true the char event should be used for parsing the
     * intValue
     */
    protected transient boolean processCharForParse;
    /**
     * the actual intValue
     */
    protected transient int intValue;
    protected transient double doubleValue;
    protected transient long longValue;
    protected transient int sign;
    protected transient long intermediateVal;
    private transient boolean parseComplete;
    private transient int currentFieldNumber;


    public Csv2Value(int fieldNumber, String terminatorChars, int headerLines) {
        this.fieldNumber = fieldNumber;
        this.terminatorChars = terminatorChars;
        this.headerLines = headerLines;
    }

    public Csv2Value(int fieldNumber) {
        this(fieldNumber, ",", 0);
    }

    public Csv2Value() {
        terminatorChars = ",";
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @EventHandler(filterId = '\n')
    public boolean onEol(CharEvent e) {
        final boolean parsed = parseValue();
        currentFieldNumber = 0;
        headerLines--;
        headerLines = Math.max(0, headerLines);
        processCharForParse = fieldNumber == 0;
        return parsed;
    }

    @Inject
    @ConfigVariable(field = "terminatorChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter delimiterNotifier;

    private transient final String terminatorChars;

    @OnParentUpdate("delimiterNotifier")
    public boolean onDelimiter(AnyCharMatchFilter terminatorNotifier) {
        parseComplete = parseValue();
        currentFieldNumber++;
        processCharForParse = fieldNumber == currentFieldNumber;
        return parseComplete;
    }

    @OnEvent
    public boolean onEvent() {
        return parseComplete;
    }

    @OnEventComplete
    public void onEventComplete() {
        parseComplete = false;
    }

//    @EventHandler(filterId = ',')
    public boolean onComma(CharEvent e) {
        final boolean parsed = parseValue();
        currentFieldNumber++;
        processCharForParse = fieldNumber == currentFieldNumber;
        return parsed;
    }

    private boolean parseValue() {
        if (processCharForParse & headerLines <= 0) {
            //extract value and reset 
            calculateValues();
            processCharForParse = false;
            intermediateVal = 0;
            pointPos = 0;
            pointIncrement = 0;
            sign = 1;
            return true;
        }
        pointPos = 0;
        pointIncrement = 0;
        intermediateVal = 0;
        return false;
    }

    protected void calculateValues() {
        intValue = (int)intermediateVal * sign;
        doubleValue = intValue;
        longValue = intValue;
    }

    @EventHandler(filterId = '-')
    public boolean onSign(CharEvent e) {
        if (processCharForParse) {
            sign = -1;
        }
        return false;
    }

    @EventHandler(filterId = '0')
    public boolean on_0(CharEvent e) {
        intermediateVal *= 10;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '1')
    public boolean on_1(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 1;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '2')
    public boolean on_2(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 2;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '3')
    public boolean on_3(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 3;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '4')
    public boolean on_4(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 4;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '5')
    public boolean on_5(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 5;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '6')
    public boolean on_6(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 6;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '7')
    public boolean on_7(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 7;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '8')
    public boolean on_8(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 8;
        pointPos += pointIncrement;
        return false;
    }

    @EventHandler(filterId = '9')
    public boolean on_9(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 9;
        pointPos += pointIncrement;
        return false;
    }

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    @Initialise
    public void init() {
        sign = 1;
        parseComplete = false;
        processCharForParse = fieldNumber == 0;
    }

}
