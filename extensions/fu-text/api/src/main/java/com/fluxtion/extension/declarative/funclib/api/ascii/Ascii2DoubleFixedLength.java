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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;

/**
 * Processes an ascii stream of events (CharEvent) to produce a numeric double
 * value. The point of parsing is controlled by two elements:
 * <ul>
 * <li>The match string that dictates at what point the parse will start in the
 * stream. A parent notifier that will signal when the filter can be applied
 *
 * <li>The length of the parse buffer can be set statically with the length
 * parameter or with a terminator character for variable length fields.
 * </ul>
 * @author Greg Higgins
 */
public class Ascii2DoubleFixedLength extends Ascii2IntFixedLength {

    public Ascii2DoubleFixedLength(Object notifier, byte length, String searchFilter) {
        super(notifier, length, searchFilter);
    }

    public Ascii2DoubleFixedLength() {
    }

    private int pointPos, pointIncrement;

    @EventHandler(filterId = '.')
    public boolean onDecimalPoint(CharEvent event) {
        pointIncrement = 1;
        return false;
    }
    
    @Override
    protected boolean checkLength(int n){
        boolean parseComplete = false;
        if (processCharForParse ) {
            fieldCharCount++;
            intermediateVal = intermediateVal * 10 + n;
            pointPos += pointIncrement;
            if (fieldCharCount >= length) {
                int tmp = intermediateVal;
                resetParse();
                doubleValue = tmp / Math.pow(10, pointPos);
                intValue = (int) doubleValue;
                longValue = (long) doubleValue;
                pointPos = 0;
                pointIncrement = 0;
                parseComplete = true;                
            }
        }
        return parseComplete;
    }

}
