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
package com.fluxtion.ext.futext.api.util;

import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;

/**
 * Utility to publish a String as a set of CharEvent's into a SEP.
 * 
 * @author Greg Higgins
 */
public class StringDriver {

    public static void streamChars(String testString, EventHandler sep) {
        streamChars(testString, sep, true);
    }
    
    public static void initSep(EventHandler sep){
        if ( sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
    }
    
    public static void tearDownSep(EventHandler sep){
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).tearDown();
        }
    }
    
    public static void streamChars(String testString, EventHandler sep, boolean callLifecycle) {
        if (callLifecycle) {
            initSep(sep);
        }
        //send char events
        char[] chars = testString.toCharArray();
        CharEvent charEvent = new CharEvent(' ');
        for (char aByte : chars) {
            charEvent.setCharacter(aByte);
            sep.onEvent(charEvent);
        }
        sep.onEvent(EofEvent.EOF);
        if(callLifecycle){
            tearDownSep(sep);
        }
    }
}
