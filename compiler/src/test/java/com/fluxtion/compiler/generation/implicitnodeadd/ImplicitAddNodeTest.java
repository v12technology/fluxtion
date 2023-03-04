/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.compiler.generation.implicitnodeadd;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.ExcludeNode;
import lombok.Value;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class ImplicitAddNodeTest extends MultipleSepTargetInProcessTest {

    public ImplicitAddNodeTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testScalarImplicitAdd() {
        sep(cfg -> {
            Counter counter = new Counter(new StringHandler());
            counter.intermediateNode = new IntermediateNode(new DoubleHandler());
            counter.dateHandler = new DateHandler();
            cfg.addPublicNode(counter, "counter");
        });
        Counter processor = getField("counter");
        onEvent("test");
        onEvent("test");
        assertThat(processor.count, is(2));
        //send some doubles
        onEvent(1.08);
        onEvent(1.08);
        onEvent(1.08);
        assertThat(processor.count, is(5));

        onEvent(new Date());
        onEvent(new Date());
        onEvent(new Date());
        assertThat(processor.count, is(5));
    }

    @Test
    public void testCollectionImplicitAdd() {
        sep(cfg -> {
            VectorCounter vectorCounter = new VectorCounter();
            vectorCounter.parents.add(new StringHandler());
            vectorCounter.parents.add(new DoubleHandler());
            cfg.addNode(vectorCounter, "vectorCounter");
        });
        VectorCounter vectorCounter = getField("vectorCounter");
        onEvent("hello");
        assertThat(vectorCounter.counter, is(1));
        onEvent(25.7);
        assertThat(vectorCounter.counter, is(2));
        onEvent(25);
        assertThat(vectorCounter.counter, is(2));
    }

    public static class StringHandler {

        @OnEventHandler
        public boolean stringUpdate(String s) {
            return true;
        }
    }

    public static class DoubleHandler {

        @OnEventHandler
        public boolean doubleUpdate(Double s) {
            return true;
        }
    }

    public static class DateHandler {

        @OnEventHandler
        public boolean doubleUpdate(Date s) {
            return true;
        }

    }

    public static class Counter {

        private int count;
        private final StringHandler myHandler;
        IntermediateNode intermediateNode;
        @ExcludeNode
        DateHandler dateHandler;

        public Counter(StringHandler myHandler) {
            this.myHandler = myHandler;
        }

        @OnTrigger
        public boolean increment() {
            count++;
            return true;
        }

    }

    public static class VectorCounter {
        List<Object> parents = new ArrayList<>();
        int counter;

        @OnTrigger
        public boolean onEvent() {
            counter++;
            return true;
        }
    }

    @Value
    public static class IntermediateNode {
        Object parent;

        @OnTrigger
        public boolean onEvent() {
            return true;
        }
    }

}
