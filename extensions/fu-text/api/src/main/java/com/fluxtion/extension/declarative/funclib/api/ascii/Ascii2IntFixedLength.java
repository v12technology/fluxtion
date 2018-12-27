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
 * Processes an ascii stream of events (CharEvent) to produce a numeric intValue.
 * The point of parsing is controlled by two elements:
 *
 * The match string that dictates at what point the parse will start in the
 * stream. A parent notifier that will signal when the filter can be applied
 *
 * The length of the parse buffer can be set statically with the length
 * parameter or with a terminator character for variable length fields.
 *
 * @author Greg Higgins
 */
public class Ascii2IntFixedLength extends Ascii2Value {


    public Ascii2IntFixedLength(Object notifier, byte length, String searchFilter) {
        super(notifier, length, searchFilter);
    }

    public Ascii2IntFixedLength() {
    }
    
    protected boolean checkLength(int n){
        boolean parseComplete = false;
        if (processCharForParse ) {
            fieldCharCount++;
            intermediateVal = intermediateVal * 10 + n;
            if (fieldCharCount >= length) {
                resetParse();
                parseComplete = true;                
            }
        }
        return parseComplete;
    }

    @EventHandler(filterId = '0')
    public boolean on_0(CharEvent e) {
        return checkLength(0);
    }

    @EventHandler(filterId = '1')
    public boolean on_1(CharEvent e) {
        return checkLength(1);
    }

    @EventHandler(filterId = '2')
    public boolean on_2(CharEvent e) {
        return checkLength(2);
    }

    @EventHandler(filterId = '3')
    public boolean on_3(CharEvent e) {
        return checkLength(3);
    }

    @EventHandler(filterId = '4')
    public boolean on_4(CharEvent e) {
        return checkLength(4);
    }

    @EventHandler(filterId = '5')
    public boolean on_5(CharEvent e) {
        return checkLength(5);
    }

    @EventHandler(filterId = '6')
    public boolean on_6(CharEvent e) {
        return checkLength(6);
    }

    @EventHandler(filterId = '7')
    public boolean on_7(CharEvent e) {
        return checkLength(7);
    }

    @EventHandler(filterId = '8')
    public boolean on_8(CharEvent e) {
        return checkLength(8);
    }

    @EventHandler(filterId = '9')
    public boolean on_9(CharEvent e) {
        return checkLength(9);
    }
    
}
