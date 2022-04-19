/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.compiler.generation.anyobjectasevent;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TestAnyObjectAsEvent extends MultipleSepTargetInProcessTest {

    public TestAnyObjectAsEvent(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void onlyMappedEventTypes() {
        sep((c) -> c.addNode(new StringHandler(), "strHandler"));
        StringHandler strHandler = getField("strHandler");
        assertFalse(strHandler.notified);
        onEvent("hello world");
        assertTrue(strHandler.notified);
    }


    @Test
    public void mappedAndUnMappedEventTypes() {
        sep((c) -> c.addNode(new StringHandler(), "strHandler"));
        StringHandler strHandler = getField("strHandler");
        assertFalse(strHandler.notified);
        onEvent("hello world");
        assertTrue(strHandler.notified);
        strHandler.notified = false;
        onEvent(111);
        assertFalse(strHandler.notified);
    }

    public static class StringHandler {

        boolean notified = false;

        @OnEventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

}
