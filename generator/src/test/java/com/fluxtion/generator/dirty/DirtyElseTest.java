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
package com.fluxtion.generator.dirty;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.event.Event;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class DirtyElseTest extends BaseSepInprocessTest {

    @Test
    public void testAudit() {
//        com.fluxtion.api.lifecycle.EventHandler handler = buildAndInitSep(DirtyBuilder.class);
//        fixedPkg = true;
        
        sep((c) -> {
            GreaterThan gt_10 = c.addNode(new GreaterThan(10));
            c.addPublicNode(new PassTest(gt_10), "passCount");
            c.addPublicNode(new FailsTest(gt_10), "failCount");
            IntermediateNode intermediate = c.addPublicNode(new IntermediateNode(gt_10), "intermediate");
            c.addPublicNode(new PassTest(intermediate), "intermedaitePassCount");
            c.addPublicNode(new FailsTest(intermediate), "intermedaiteFailCount");
            c.formatSource = true;
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

        @EventHandler
        public boolean isGreaterThan(NumberEvent number) {
            //System.out.println("number:" + number.value);
            return number.value > barrier;
        }
    }
    
    public static class IntermediateNode{
        public final Object tracked;

        public IntermediateNode(Object tracked) {
            this.tracked = tracked;
        }
        
        @OnEvent
        public boolean notifyChildren(){
            return true;
        }
    }

    public static class PassTest {

        public final Object greaterThan;
        public int count;

        public PassTest(Object greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnEvent
        public void publishPass() {
            count++;
            //System.out.println("passed");
        }

    }

    public static class FailsTest {

        public final Object greaterThan;
        public int count;

        public FailsTest(Object greaterThan) {
            this.greaterThan = greaterThan;
        }

        @OnEvent(dirty = false)
        public void publishFail() {
            count++;
            //System.out.println("failed");
        }

    }

}
