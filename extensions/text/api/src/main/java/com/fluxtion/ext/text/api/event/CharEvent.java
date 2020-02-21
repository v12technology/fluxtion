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
package com.fluxtion.ext.text.api.event;

import com.fluxtion.api.event.Event;
import static com.fluxtion.ext.text.api.event.EventId.*;

/**
 * A Fluxtion Event encapsulating a char. CharEvent is mutable allowing updating
 * of the underlying char.
 * 
 * @author Greg Higgins
 */
public class CharEvent extends Event{
    
    private char character;
    
    public CharEvent(char id) {
        super(id);
        filterId = id;
        character = (char)filterId;
    }

    public char getCharacter() {
        return character;
    }

    /**
     * Setting the character will also make the filterId update as well
     * @param character 
     */
    public void setCharacter(char character) {
        filterId = character;
        this.character = character;
    }

    @Override
    public String toString() {
        return "CharEvent{" + getCharacter() + '}';
    }
           
}
