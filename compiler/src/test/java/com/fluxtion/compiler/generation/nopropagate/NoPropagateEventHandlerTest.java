/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.compiler.generation.nopropagate;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NoPropagateEventHandlerTest extends MultipleSepTargetInProcessTest {

    public NoPropagateEventHandlerTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noPropagateFromEventHandler() {
        sep(c -> {
            c.addPublicNode(new CountingNode(new StringHandler()), "countingNode");
        });
        CountingNode countingNode = getField("countingNode");
        onEvent("ignore me");
        assertThat(countingNode.getCount(), is(0));
    }

    @Test
    public void partialPropagationFromEventHandler() {
        sep(c -> {
            c.addPublicNode(new CountingNode(new MultiHandler()), "countingNode");
        });
        CountingNode countingNode = getField("countingNode");
        onEvent("ignore me");
        assertThat(countingNode.getCount(), is(0));

        onEvent(111);
        assertThat(countingNode.getCount(), is(1));

    }

    public static class StringHandler {

        boolean notified = false;

        @OnEventHandler(propagate = false)
        public void newString(String s) {
            notified = true;
        }
    }

    public static class MultiHandler {

        boolean notified = false;

        @OnEventHandler(propagate = false)
        public void newString(String s) {
            notified = true;
        }

        @OnEventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class CountingNode {

        final Object parent;
        int count;

        @OnTrigger
        public boolean onEvent() {
            count++;
            return true;
        }
    }
}
