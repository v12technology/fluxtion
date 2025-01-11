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
import com.fluxtion.runtime.node.BaseNode;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class MultipleInputsSinglePushTest extends MultipleSepTargetInProcessTest {

    public MultipleInputsSinglePushTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void biPushTest() {
//        addAuditor();
        sep(c -> {
            DataFlow.push(
                    c.addNode(new MyPushTarget(), "myPushTarget")::update,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribeToIntSignal("count").box());
        });


        MyPushTarget myPushTarget = getField("myPushTarget");

        publishIntSignal("count", 200);
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
    public void biPushTestClassMethod() {
//        writeSourceFile = true;
//        addAuditor();
        sep(c -> {
            DataFlow.push(
                    MyPushTarget::update,
                    DataFlow.subscribe(String.class),
                    DataFlow.subscribeToIntSignal("count").box());
        });

        publishIntSignal("count", 200);
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertNull(MyPushTarget.stringInput);

        onEvent("stringFlow");
        Assert.assertEquals(200, MyPushTarget.inputCount);
        Assert.assertEquals("stringFlow", MyPushTarget.stringInput);
    }


    @Data
    public static class MyPushTarget extends BaseNode {

        static String stringInput;
        static int inputCount;
        private String instanceString;
        private int instanceCount;

        public void update(String input, int inCount) {
            auditLog.info("stringInput", input)
                    .info("inputCount", inputCount);
            stringInput = input;
            inputCount = inCount;
            //
            instanceString = input;
            instanceCount = inCount;
        }
    }
}
