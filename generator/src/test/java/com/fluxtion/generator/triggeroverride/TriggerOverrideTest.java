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
package com.fluxtion.generator.triggeroverride;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.api.annotations.TriggerEventOverride;
import com.fluxtion.builder.annotation.SepInstance;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TriggerOverrideTest extends BaseSepInprocessTest {

    @Test
    public void testCombined() {
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

    public static class StringHandler {

        boolean notified = false;

        @EventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

    public static class NumberHandler {

        boolean notified = false;

        @EventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class TriggerinOverride {

        @SepNode
        private final StringHandler stringHandler;
        @SepNode
        @TriggerEventOverride
        private final NumberHandler numberHandler;
        private int count;

        @OnEvent
        public void update() {
            count++;
        }

    }

}
