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
package com.fluxtion.generator.logging;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import nl.altindag.log.LogCaptor;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TestGenerateInjectedLogMessages extends BaseSepInProcessTest {
    
    @Test
    public void testCombined() {
        sep((c) -> {
            c.setGenerateLogging(true);
            c.addNode(new StringHandler(), "strHandler");
        });
        LogCaptor logCaptor = LogCaptor.forClass(sep.getClass());
        logCaptor.setLogLevelToTrace();
        onEvent("hello world");
        assertTrue(logCaptor.getLogs().size() > 1);
        logCaptor.clearLogs();
    }

    public static class StringHandler {

        boolean notified = false;

        @EventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

}
