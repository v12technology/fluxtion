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
package com.fluxtion.generator.propagation;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PropagationTest extends MultipleSepTargetInProcessTest {

    public PropagationTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void onlyMappedEventTypes() {
        sep((c) -> c.addNode(new Child(new MyHandler()), "child"));
        Child child = getField("child");
        assertFalse(child.notified);
        onEvent("hello world");
        assertTrue(child.notified);
    }

    @Test
    public void mappedAndUnMappedEventTypes() {
        sep((c) -> c.addNode(new MyHandler(), "strHandler"));
        MyHandler strHandler = getField("strHandler");
        assertFalse(strHandler.notified);
        onEvent("hello world");
        assertTrue(strHandler.notified);
        strHandler.notified = false;
        onEvent(111);
        assertFalse(strHandler.notified);
    }

    public static class MyHandler {

        boolean notified = false;

        @EventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }

        @EventHandler
        public boolean handleInt(int input){
            return input > 0;
        }

        @EventHandler
        public void handleDouble(double input){

        }
    }

    @Data
    public static class Child{
        final MyHandler myHandler;
        boolean notified;

        @OnEvent
        public void onEvent(){
            notified = true;
        }
    }

}
