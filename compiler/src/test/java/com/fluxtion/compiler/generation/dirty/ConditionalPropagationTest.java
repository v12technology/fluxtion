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
package com.fluxtion.compiler.generation.dirty;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ConditionalPropagationTest extends MultipleSepTargetInProcessTest {


    public ConditionalPropagationTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testCombined() {
        sep((c) -> {
            c.addPublicNode(new CountingNode(new StringHandler("matchme")), "stringCounter");
            c.addPublicNode(new CountingNode(new IntHandler(100)), "intCounter");
        });
        CountingNode stringCountingNode = getField("stringCounter");
        CountingNode intCountingNode = getField("intCounter");

        onEvent(1);
        assertThat(stringCountingNode.getCount(), is(0));
        assertThat(intCountingNode.getCount(), is(0));

        onEvent(100);
        assertThat(stringCountingNode.getCount(), is(0));
        assertThat(intCountingNode.getCount(), is(1));

        onEvent("no match");
        assertThat(stringCountingNode.getCount(), is(0));
        assertThat(intCountingNode.getCount(), is(1));

        onEvent("matchme");
        assertThat(stringCountingNode.getCount(), is(1));
        assertThat(intCountingNode.getCount(), is(1));
    }


    @Data
    public static class StringHandler {

        final String match;
        boolean notified = false;

        @OnEventHandler
        public boolean newString(String s) {
            notified = true;
            return s.equals(getMatch());
        }
    }

    @Data
    public static class IntHandler {

        final int match;
        boolean notified = false;

        @OnEventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return s == (getMatch());
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
