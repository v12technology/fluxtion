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
package com.fluxtion.ext.futext.builder.util;

import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.text.api.util.StringDriver;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import net.vidageek.mirror.dsl.Mirror;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TextInProcessTest extends BaseSepInProcessTest {

    protected <T> T getWrappedField(String name) {
        Wrapper<T> wrapped = (Wrapper<T>) new Mirror().on(sep).get().field(name);
        return wrapped.event();
    }

    protected void stream(String s) {
        StringDriver.streamChars(s, sep, false);
    }
}
