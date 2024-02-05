/*
 * Copyright (C) 2020 2024 gregory higgins.
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

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author 2024 gregory higgins.
 */
public class TestAnyObjectAsEvent extends MultipleSepTargetInProcessTest {

    public TestAnyObjectAsEvent(SepTestConfig compiledSep) {
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

    @Test
    public void defaultHandlerStatic() {
        StaticEventHandler.count = 0;
        sep(c -> {
            c.addNode(new StringHandler(), "strHandler");
            c.addNode(new StaticEventHandler(), "defaultHandler");
        });

        MatcherAssert.assertThat(StaticEventHandler.count, is(0));
        onEvent("test");
        onEvent(new Object());
        onEvent(new Date());
        MatcherAssert.assertThat(StaticEventHandler.count, is(3));
    }


    @Test
    public void defaultHandler() {
        sep(c -> {
            c.addNode(new StringHandler(), "strHandler");
            c.addNode(new ObjectHandler(), "defaultHandler");
        });

        MatcherAssert.assertThat(getField("defaultHandler", ObjectHandler.class).count, is(0));
        onEvent("test");
        onEvent(new Object());
        onEvent(new Date());
        MatcherAssert.assertThat(getField("defaultHandler", ObjectHandler.class).count, is(3));
    }

    public static class StringHandler {

        boolean notified = false;

        @OnEventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

    public static class ObjectHandler {

        int count;

        @OnEventHandler
        public boolean defaultHandler(Object object) {
            count++;
            return true;
        }
    }

    public static class StaticEventHandler {
        static int count;

        @OnEventHandler
        public static boolean defaultHandler(Object object) {
            count++;
            return true;
        }
    }

}
