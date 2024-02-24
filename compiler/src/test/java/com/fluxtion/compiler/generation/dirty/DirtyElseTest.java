/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
import com.fluxtion.runtime.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
@Slf4j
public class DirtyElseTest extends MultipleSepTargetInProcessTest {

    public DirtyElseTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void simpleDirtyInvert() {
        sep((c) -> {
            GreaterThan gt_10 = new GreaterThan(10);
            c.addPublicNode(new PassTest(gt_10), "passCount");
            c.addPublicNode(new FailsTest(gt_10), "invertCount");
        });
        PassTest pass = getField("passCount");
        FailsTest invert = getField("invertCount");

        sep.onEvent(new NumberEvent(12));
        assertThat(pass.count, is(1));
        assertThat(invert.count, is(0));

        sep.onEvent(new NumberEvent(3));
        assertThat(pass.count, is(1));
        assertThat(invert.count, is(1));
    }

    @Test
    public void intermediateDirtyDoesNotFireUnlessParentHasTriggerTest() {
        sep((c) -> {
            GreaterThan gt_10 = new GreaterThan(10);
            IntermediateNode intermediate = new IntermediateNode(gt_10);

            c.addPublicNode(new PassTest(gt_10), "passCount");
            c.addPublicNode(new FailsTest(gt_10), "failCount");
            c.addPublicNode(new PassTest(intermediate), "intermedaitePassCount");
            c.addPublicNode(new FailsTest(intermediate), "intermedaiteFailCount");
        });

        PassTest pass = getField("passCount");
        FailsTest fail = getField("failCount");
        PassTest passInt = getField("intermedaitePassCount");
        FailsTest failInt = getField("intermedaiteFailCount");

        sep.onEvent(new NumberEvent(12));
        assertThat(pass.count, is(1));
        assertThat(fail.count, is(0));
        assertThat(passInt.count, is(1));
        assertThat(failInt.count, is(0));

        sep.onEvent(new NumberEvent(3));
        assertThat(pass.count, is(1));
        assertThat(fail.count, is(1));
        assertThat(passInt.count, is(1));
        assertThat(failInt.count, is(0));
    }

    public static class NumberEvent implements Event {

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

        @OnEventHandler
        public boolean isGreaterThan(NumberEvent number) {
            return number.value > barrier;
        }
    }

    public static class IntermediateNode {
        public final Object tracked;

        public IntermediateNode(Object tracked) {
            this.tracked = tracked;
        }

        @OnTrigger
        public boolean notifyChildren() {
            return true;
        }
    }

    public static class PassTest {

        public final Object greaterThan;
        public int count;

        public PassTest(Object greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnTrigger
        public boolean publishPass() {
            count++;
            return true;
        }

    }

    public static class FailsTest {

        public final Object greaterThan;
        public int count;

        public FailsTest(Object greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnTrigger(dirty = false)
        public boolean publishFail() {
            count++;
            return true;
        }

    }

}
