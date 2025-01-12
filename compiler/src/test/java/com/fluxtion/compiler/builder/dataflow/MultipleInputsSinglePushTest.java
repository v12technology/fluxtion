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

package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.node.BaseNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MultipleInputsSinglePushTest extends MultipleSepTargetInProcessTest {

    public MultipleInputsSinglePushTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Before
    public void setup() {
        MyPushTarget.update3Called = false;
        MyPushTarget.update4Called = false;
    }

    @Test
    public void biPushTest() {
        sep(c -> {
            MyPushTarget myPushTarget = c.addNode(new MyPushTarget(), "myPushTarget");
            c.addNode(new PushTriggerMonitor(myPushTarget), "monitor");
            DataFlow.push(
                    myPushTarget::update,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribeToIntSignal("count"));
        });

        MyPushTarget myPushTarget = getField("myPushTarget");

        PushTriggerMonitor monitor = getField("monitor");
        Assert.assertFalse(monitor.isTriggered());

        publishIntSignal("count", 200);
        Assert.assertTrue(monitor.isTriggered());
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertEquals(200, myPushTarget.getInstanceCount());
        Assert.assertNull(MyPushTarget.stringInput);
        Assert.assertNull(myPushTarget.getInstanceString());

        onEvent("stringFlow");
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertEquals(200, myPushTarget.getInstanceCount());
        Assert.assertEquals("stringFlow", MyPushTarget.stringInput);
        Assert.assertEquals("stringFlow", myPushTarget.getInstanceString());
    }

    @Test
    public void triPushTest() {
        sep(c -> {
            MyPushTarget myPushTarget = c.addNode(new MyPushTarget(), "myPushTarget");
            c.addNode(new PushTriggerMonitor(myPushTarget), "monitor");
            DataFlow.push(
                    myPushTarget::update3,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class));
        });

        MyPushTarget myPushTarget = getField("myPushTarget");

        PushTriggerMonitor monitor = getField("monitor");
        Assert.assertFalse(monitor.isTriggered());

        onEvent("stringFlow");
        Assert.assertTrue(monitor.isTriggered());
        Assert.assertEquals("stringFlow", myPushTarget.getInput1());
        Assert.assertEquals("stringFlow", myPushTarget.getInput2());
        Assert.assertEquals("stringFlow", myPushTarget.getInput3());
    }

    @Test
    public void quadPushTest() {
        sep(c -> {
            MyPushTarget myPushTarget = c.addNode(new MyPushTarget(), "myPushTarget");
            c.addNode(new PushTriggerMonitor(myPushTarget), "monitor");
            DataFlow.push(
                    myPushTarget::update4,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class));
        });

        MyPushTarget myPushTarget = getField("myPushTarget");

        PushTriggerMonitor monitor = getField("monitor");
        Assert.assertFalse(monitor.isTriggered());

        onEvent("stringFlow");
        Assert.assertTrue(monitor.isTriggered());
        Assert.assertEquals("stringFlow", myPushTarget.getInput1());
        Assert.assertEquals("stringFlow", myPushTarget.getInput2());
        Assert.assertEquals("stringFlow", myPushTarget.getInput3());
        Assert.assertEquals("stringFlow", myPushTarget.getInput4());
    }

    @Test
    public void biPushTestClassMethod() {
        sep(c -> {
            DataFlow.push(
                    MyPushTarget::update,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribeToIntSignal("count"));

            DataFlow.push(
                    MyPushTarget::update3,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class));

            DataFlow.push(
                    MyPushTarget::update4,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribe(String.class));
        });

        Assert.assertFalse(MyPushTarget.update3Called);
        Assert.assertFalse(MyPushTarget.update4Called);


        publishIntSignal("count", 200);
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertNull(MyPushTarget.stringInput);

        onEvent("stringFlow");
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertEquals("stringFlow", MyPushTarget.stringInput);

        Assert.assertTrue(MyPushTarget.update3Called);
        Assert.assertTrue(MyPushTarget.update4Called);
    }


    @Data
    public static class MyPushTarget extends BaseNode {

        static String stringInput;
        static int inputCount;
        private String instanceString;
        private int instanceCount;
        private String input1;
        private String input2;
        private String input3;
        private String input4;
        static boolean update3Called = false;
        static boolean update4Called = false;

        public void update(String input, int inCount) {
            auditLog.info("stringInput", input)
                    .info("inputCount", inputCount);
            stringInput = input;
            inputCount = inCount;
            //
            instanceString = input;
            instanceCount = inCount;
        }

        public void update3(String input1, String input2, String input3) {
            this.input1 = input1;
            this.input2 = input2;
            this.input3 = input3;
            update3Called = true;
        }

        public void update4(String input1, String input2, String input3, String input4) {
            this.input1 = input1;
            this.input2 = input2;
            this.input3 = input3;
            this.input4 = input4;
            update4Called = true;
        }

        @OnTrigger
        public boolean onTrigger() {
            return true;
        }
    }

    @Data
    public static class PushTriggerMonitor extends BaseNode {
        private final MyPushTarget myPushTarget;
        private boolean triggered;

        @OnTrigger
        public boolean onTrigger() {
            auditLog.info("myPushTarget", myPushTarget);
            triggered = true;
            return true;
        }
    }
}
