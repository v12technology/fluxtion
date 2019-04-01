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
package com.fluxtion.ext.text.api.filter;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.ext.text.api.event.CharEvent;

/**
 * Char notifiers match on a configured single char
 *  
 * @author Greg Higgins
 */
public class SingleCharMatcher implements AnyCharMatchFilter{

    private final char matchedChar;

    public SingleCharMatcher(char matchedChar) {
        this.matchedChar = matchedChar;
    }
    
    @EventHandler(filterVariable = "matchedChar") 
    public final boolean onChar (CharEvent event) {
        return event.getCharacter()==matchedChar;
    }

    @Override
    public char matchedChar() {
        return matchedChar;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.matchedChar;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SingleCharMatcher other = (SingleCharMatcher) obj;
        if (this.matchedChar != other.matchedChar) {
            return false;
        }
        return true;
    }
    
}
