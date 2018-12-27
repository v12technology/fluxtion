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

import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.extension.declarative.funclib.api.event.CharEvent;
import com.fluxtion.extension.declarative.funclib.api.filter.AnyCharMatchFilter;

/**
 * Processes an ascii stream of events (CharEvent) to produce a numeric
 * intValue. The point of parsing is controlled by two elements:
 *
 * The match string that dictates at what point the parse will start in the
 * stream. A parent notifier that will signal when the filter can be applied
 *
 * The length of the parse buffer can be set statically with the length
 * parameter or with a terminator character for variable length fields.
 *
 * @author Greg Higgins
 */
public class Ascii2IntTerminator extends Ascii2Value {

    public Ascii2IntTerminator(Object notifier, String terminator, String searchFilter) {
        super(notifier, searchFilter);
        this.terminatorChars = terminator;
    }

    public Ascii2IntTerminator(Object notifier, String searchFilter) {
        this(notifier, " \n\t", searchFilter);
    }

    public Ascii2IntTerminator() {
        this.terminatorChars = " \n\t";
    }
    
    @Inject
    @ConfigVariable(field = "terminatorChars", key = AnyCharMatchFilter.KEY_FILTER_ARRAY)
    public AnyCharMatchFilter terminatorNotifier;
    
    private final String terminatorChars;
    
    @OnParentUpdate("terminatorNotifier")
    public void onTerminator(AnyCharMatchFilter terminatorNotifier){
        if (processCharForParse) {
            resetParse();
            parseComplete = true;
        }
    }
    
    @OnEvent
    public boolean onEvent(){
        return parseComplete;
    }
    
    @OnEventComplete
    public void afterMatchedEvent(){
        parseComplete = false;
    }

    @EventHandler(filterId = '0')
    public boolean on_0(CharEvent e) {
        intermediateVal *= 10;
        return false;
    }

    @EventHandler(filterId = '1')
    public boolean on_1(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 1;
        return false;
    }

    @EventHandler(filterId = '2')
    public boolean on_2(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 2;
        return false;
    }

    @EventHandler(filterId = '3')
    public boolean on_3(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 3;
        return false;
    }

    @EventHandler(filterId = '4')
    public boolean on_4(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 4;
        return false;
    }

    @EventHandler(filterId = '5')
    public boolean on_5(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 5;
        return false;
    }

    @EventHandler(filterId = '6')
    public boolean on_6(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 6;
        return false;
    }

    @EventHandler(filterId = '7')
    public boolean on_7(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 7;
        return false;
    }

    @EventHandler(filterId = '8')
    public boolean on_8(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 8;
        return false;
    }

    @EventHandler(filterId = '9')
    public boolean on_9(CharEvent e) {
        intermediateVal = intermediateVal * 10 + 9;
        return false;
    }

    private boolean parseComplete;
    

}
