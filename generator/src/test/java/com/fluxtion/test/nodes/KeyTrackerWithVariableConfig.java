/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.test.nodes;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 *
 * @author Greg Higgins
 */
public class KeyTrackerWithVariableConfig {

    //For testing
    public transient boolean key_a = false;
    public transient boolean key_1 = false;
    public transient boolean key_x = false;
    public transient boolean onEvent = false;

    //This is global config element
    private char cahr_1 = 'x';
    
    private boolean notifyAccum = false;
    
    //global + override by config annotation
    @Inject
    @Config(key = KeyProcessorFactory.KEY_CHAR, value = "1")
    @ConfigVariable(key = KeyProcessorFactory.KEY_NOTIFY_ACCUM, field="notifyAccum")
    public KeyProcessor keyProcessor_1;

    //This is config element for a specific injected field 'keyProcessor_a'
    private char a = 'a';
    
    //global + override by field specific config
    @Inject
    @ConfigVariable(field = "a", key = KeyProcessorFactory.KEY_CHAR)
    public KeyProcessor keyProcessor_a;
    
    //picks up all global config
    @Inject
    @ConfigVariable( key = KeyProcessorFactory.KEY_CHAR, field = "cahr_1")
    public KeyProcessor keyProcessor_x;
    
    @OnParentUpdate("keyProcessor_1")
    public void onKeyPress_1(KeyProcessor processor) {
        key_1 = true;
    }

    @OnParentUpdate("keyProcessor_a")
    public void onKeyPress_a(KeyProcessor processor) {
        key_a = true;
    }

    @OnParentUpdate("keyProcessor_x")
    public void onKeyPress_x(KeyProcessor processor) {
        key_x = true;
    }

    @OnEvent
    public void onEvent() {
        onEvent = true;
    }

    public void resetTestFlags() {
        key_1 = false;
        key_a = false;
        key_x = false;
        onEvent = false;
    }
}
