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

package com.fluxtion.compiler.generation.callback;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.callback.AbstractCallbackNode;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import lombok.Value;
import org.junit.Assert;
import org.junit.Test;

public class CallbackNodeExtendAbstractTest extends MultipleSepTargetInProcessTest {
    public CallbackNodeExtendAbstractTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void directInvokeTest() {
//        writeOutputsToFile(true);
        sep(c -> {
            c.addNode(new Child(new ExternalCallback("callback1")), "child1");
            c.addNode(new Child(new ExternalCallback("callback2")), "child2");
            c.addNode(new Child_A(new ExternalCallback_A("callback3")), "child3");
        });

        Child child1 = getField("child1");
        Child child2 = getField("child2");
        Child_A child3A = getField("child3");

        ExternalCallback callback1 = getField("callback1");
        ExternalCallback callback2 = getField("callback2");
        ExternalCallback_A callback_A = getField("callback3");

        //update 1
        callback1.doubleEvent(new MyEvent<>(32.4));
        Assert.assertEquals(32.4, (Double) child1.getResult(), 0.0001);
        Assert.assertNull(child2.getResult());
        Assert.assertNull(child3A.getResult());

        //update 1
        callback1.stringEvent(new MyEvent<>("jjjj"));
        Assert.assertEquals("jjjj", child1.getResult());
        Assert.assertNull(child2.getResult());
        Assert.assertNull(child3A.getResult());

        //update 2
        child1.setResult(null);
        child2.setResult(null);

        callback2.doubleEvent(new MyEvent<>(32.4));
        Assert.assertNull(child1.getResult());
        Assert.assertEquals(32.4, (Double) child2.getResult(), 0.0001);

        callback2.stringEvent(new MyEvent<>("jjjj"));
        Assert.assertNull(child1.getResult());
        Assert.assertEquals("jjjj", child2.getResult());
        Assert.assertNull(child3A.getResult());

        //update 3
        child1.setResult(null);
        child2.setResult(null);

        callback_A.doubleEvent(new MyEvent<>(32.4));
        Assert.assertNull(child1.getResult());
        Assert.assertNull(child2.getResult());
        Assert.assertEquals(32.4, (Double) child3A.getResult(), 0.0001);

        callback_A.stringEvent(new MyEvent<>("jjjj"));
        Assert.assertNull(child1.getResult());
        Assert.assertNull(child2.getResult());
        Assert.assertEquals("jjjj", child3A.getResult());
    }


    public static class ExternalCallback extends AbstractCallbackNode<Object> implements NamedNode {

        private final String name;

        public ExternalCallback(String name) {
            super();
            this.name = name;
        }

        public ExternalCallback(int filterId, String name) {
            super(filterId);
            this.name = name;
        }

        public void stringEvent(MyEvent<String> myEvent) {
            fireNewEventCycle(myEvent.getData());
        }

        public void doubleEvent(MyEvent<Double> myEvent) {
            fireNewEventCycle(myEvent.getData());
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class ExternalCallback_A extends AbstractCallbackNode<Object> implements NamedNode {

        private final String name;

        public ExternalCallback_A(String name) {
            super();
            this.name = name;
        }

        public ExternalCallback_A(int filterId, String name) {
            super(filterId);
            this.name = name;
        }

        public void stringEvent(MyEvent<String> myEvent) {
            fireNewEventCycle(myEvent.getData());
        }

        public void doubleEvent(MyEvent<Double> myEvent) {
            fireNewEventCycle(myEvent.getData());
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Data
    public static class Child {
        private final ExternalCallback externalCallback;
        private Object result;

        @OnTrigger
        public boolean triggered() {
            result = externalCallback.get();
            return true;
        }
    }

    @Data
    public static class Child_A {
        private final ExternalCallback_A externalCallback;
        private Object result;

        @OnTrigger
        public boolean triggered() {
            result = externalCallback.get();
            return true;
        }
    }

    @Value
    public static class MyEvent<T> {
        T data;
    }
}
