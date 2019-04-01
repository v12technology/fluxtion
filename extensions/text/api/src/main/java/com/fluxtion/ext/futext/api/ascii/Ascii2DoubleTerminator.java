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
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.filter.AnyCharMatchFilter;

/**
 * Processes an ascii stream of events (CharEvent) to produce a numeric double
 * value. The point of parsing is controlled by two elements:
 *
 * The match string that dictates at what point the parse will start in the
 * stream. A parent notifier that will signal when the filter can be applied
 *
 * The length of the parse buffer can be set statically with the length
 * parameter or with a terminator character for variable length fields.
 *
 * @author Greg Higgins
 */
public class Ascii2DoubleTerminator extends Ascii2Value {

    protected int pointPos, pointIncrement;

    public Ascii2DoubleTerminator(Object notifier, String terminator, String searchFilter) {
        super(notifier, searchFilter);
        this.terminatorChars = terminator;
    }

    public Ascii2DoubleTerminator(Object notifier, String searchFilter) {
        this(notifier, " \n\t", searchFilter);
    }

    public Ascii2DoubleTerminator() {
        this.terminatorChars = " \n\t";
    }

    @EventHandler(filterId = '.')
    public boolean onDecimalPoint(CharEvent event) {
        pointIncrement = 1;
        return false;
    }
    
    @Inject
    @ConfigVariable(field = "terminatorChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter terminatorNotifier;

    private final String terminatorChars;

    @OnParentUpdate("terminatorNotifier")
    public void onTerminator(AnyCharMatchFilter terminatorNotifier) {
        if (processCharForParse) {
            double tmp = intermediateVal / Math.pow(10, pointPos);
            super.resetParse();
            doubleValue = tmp;
            intValue = (int) doubleValue;
            longValue = (long) doubleValue;
            pointPos = 0;
            pointIncrement = 0;
            parseComplete = true;
        }
    }

    @OnEvent
    public boolean onEvent() {
        return parseComplete;
    }

    @OnEventComplete
    public void afterMatchedEvent() {
        parseComplete = false;
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

    private boolean parseComplete;

}
