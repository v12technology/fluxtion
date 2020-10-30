/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */
package com.fluxtion.ext.text.api.util;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;

/**
 * Utility to publish a String as a set of CharEvent's into a SEP.
 * 
 * @author Greg Higgins
 */
public class StringDriver {

    public static void streamChars(String testString, StaticEventProcessor sep) {
        streamChars(testString, sep, true);
    }
    
    public static void initSep(StaticEventProcessor sep){
        if ( sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
    }
    
    public static void tearDownSep(StaticEventProcessor sep){
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).tearDown();
        }
    }
    
    public static void streamChars(String testString, StaticEventProcessor sep, boolean callLifecycle) {
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
