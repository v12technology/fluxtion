/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.generator.dirty;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.audit.RegistrationListenerTest;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.event.Event;
import com.fluxtion.test.event.TestEvent;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class DirtyElseTest extends BaseSepTest {

    @Test
    public void testAudit() {
        com.fluxtion.api.lifecycle.EventHandler handler = buildAndInitSep(DirtyBuilder.class);
        PassTest pass = getField("passCount");
        FailsTest fail = getField("failCount");
        handler.onEvent(new NumberEvent(12));
        assertThat(pass.count, is(1));
        assertThat(fail.count, is(0));
        handler.onEvent(new NumberEvent(3));
        assertThat(pass.count, is(1));
        assertThat(fail.count, is(1));
    }

    public static class NumberEvent extends Event {

        public final int value;

        public NumberEvent(int value) {
            this.value = value;
        }

    }

    public static class GreaterThan {

        public final int barrier;

        public GreaterThan(int barrier) {
            this.barrier = barrier;
        }

        @EventHandler
        public boolean isGreaterThan(NumberEvent number) {
            //System.out.println("number:" + number.value);
            return number.value > barrier;
        }
    }

    public static class PassTest {

        public final GreaterThan greaterThan;
        public int count;

        public PassTest(GreaterThan greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnEvent
        public void publishPass() {
            count++;
            //System.out.println("passed");
        }

    }

    public static class FailsTest {

        public final GreaterThan greaterThan;
        public int count;

        public FailsTest(GreaterThan greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnEvent(dirty = false)
        public void publishFail() {
            count++;
            //System.out.println("failed");
        }

    }

    public static class DirtyBuilder extends SEPConfig {

        @Override
        public void buildConfig() {
            GreaterThan gt_10 = addNode(new GreaterThan(10));
            addPublicNode(new PassTest(gt_10), "passCount");
            addPublicNode(new FailsTest(gt_10), "failCount");
        }

    }

}
