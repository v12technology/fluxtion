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
package com.fluxtion.compiler.generation.triggeroverride;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.TriggerEventOverride;
import lombok.Data;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author V12 Technology Ltd.
 */
public class TriggerOverrideTest extends MultipleSepTargetInProcessTest {

    public TriggerOverrideTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void triggerOverride() {
        fixedPkg = true;
        sep((c) -> {
            c.addNode(new TriggerinOverride(new StringHandler(), new NumberHandler()), "strHandler");
        });
        TriggerinOverride trigger = getField("strHandler");
        assertThat(trigger.getCount(), is(0));
        onEvent("hello world");
        assertThat(trigger.getCount(), is(0));
        onEvent(1);
        assertThat(trigger.getCount(), is(1));
    }

    @Test
    public void triggerNoOverride() {
        fixedPkg = true;
        sep((c) -> {
            c.addNode(new TriggerinNoOverride(new StringHandler()), "strHandler");
        });
        TriggerinNoOverride trigger = getField("strHandler");
        assertThat(trigger.getCount(), is(0));
        onEvent("hello world");
        assertThat(trigger.getCount(), is(1));
        onEvent("hello world");
        assertThat(trigger.getCount(), is(2));
        onEvent(1);
        assertThat(trigger.getCount(), is(2));
    }

    public static class StringHandler {

        boolean notified = false;

        @OnEventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

    public static class NumberHandler {

        boolean notified = false;

        @OnEventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class TriggerinOverride {

        private final StringHandler stringHandler;
        @TriggerEventOverride
        private final NumberHandler numberHandler;
        private int count;

        @OnTrigger
        public boolean update() {
            count++;
            return true;
        }

    }

    @Data
    public static class TriggerinNoOverride {

        private final StringHandler stringHandler;
        @TriggerEventOverride
        private NumberHandler numberHandler;
        private int count;

        @OnTrigger
        public boolean update() {
            count++;
            return true;
        }

    }

}
