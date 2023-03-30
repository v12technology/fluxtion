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

import com.fluxtion.runtime.annotations.builder.Config;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * @author Greg Higgins
 */
public class KeyTracker {

    //For testing
    public transient boolean key_a = false;
    public transient boolean key_1 = false;
    public transient boolean onEvent = false;

    @Inject
    @Config(key = KeyProcessorFactory.KEY_CHAR, value = "1")
    @Config(key = KeyProcessorFactory.KEY_NOTIFY_ACCUM, value = "false")
    public KeyProcessor keyProcessor_1;

    @Inject
    @Config(key = KeyProcessorFactory.KEY_CHAR, value = "a")
    @Config(key = KeyProcessorFactory.KEY_NOTIFY_ACCUM, value = "false")
    public KeyProcessor keyProcessor_a;

    @OnParentUpdate("keyProcessor_1")
    public void onKeyPress_1(KeyProcessor processor) {
        key_1 = true;
    }

    @OnParentUpdate("keyProcessor_a")
    public void onKeyPress_a(KeyProcessor processor) {
        key_a = true;
    }

    @OnTrigger
    public boolean onEvent() {
        onEvent = true;
        return true;
    }

    public void resetTestFlags() {
        key_a = false;
        key_1 = false;
        onEvent = false;
    }
}
