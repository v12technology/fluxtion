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
package com.fluxtion.ext.futext.api.util.marshaller;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.filter.AnyCharMatchFilter;

/**
 * Char notifiers match = ','
 *  
 * @author Greg Higgins
 */
public class AsciiAnyCharMatcher_0 implements AnyCharMatchFilter{

    private transient char matchedChar;

    @EventHandler(filterId = ',') 
    public final boolean onChar_44 (CharEvent event) {
        matchedChar = ',';
        return true;
    }

    @Override
    public char matchedChar() {
        return matchedChar;
    }
}
