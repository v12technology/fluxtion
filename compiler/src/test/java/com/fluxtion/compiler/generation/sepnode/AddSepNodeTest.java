/*
 * Copyright (c) 2020, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.sepnode;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class AddSepNodeTest extends MultipleSepTargetInProcessTest {

    public AddSepNodeTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testAddNodeByAnnotation() {
        sep(cfg -> {
            cfg.addPublicNode(new Counter(new Stringhandler()), "counter");
        });
        Counter processor = getField("counter");
        onEvent("test");
        onEvent("test");
        assertThat(processor.count, is(2));
    }

    @Test
    public void testSepNodeCollection() {
        sep(c -> {
            Stringhandler counter = c.addPublicNode(new Stringhandler(), "stringhandler");
            counter.managedMyThingList.add(new MyThing("A"));
            counter.managedMyThingList.add(new MyThing("B"));
        });
        Stringhandler stringhandler = getField("stringhandler");
        assertThat(stringhandler.managedMyThingList.size(), is(2));
    }

    @Test
    public void testCollectionOfSepNodes() {
        sep(c -> {
            Stringhandler counter = c.addPublicNode(new Stringhandler(), "stringhandler");
            counter.managedMyOtherThingList.add(new MyOtherThing("A"));
            counter.managedMyOtherThingList.add(new MyOtherThing("B"));
        });
        Stringhandler stringhandler = getField("stringhandler");
        assertThat(stringhandler.managedMyOtherThingList.size(), is(2));
    }

    public static class Stringhandler {

        @SepNode
        public List<MyThing> managedMyThingList = new ArrayList<>();
        public List<MyOtherThing> managedMyOtherThingList = new ArrayList<>();

        @OnEventHandler
        public boolean stringUpdate(String s) {
            return true;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyThing {
        public String id;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @SepNode
    public static class MyOtherThing {
        public String id;
    }

    public static class Counter {

        private int count;
        @SepNode
        private final Stringhandler myHandler;

        public Counter(Stringhandler myHandler) {
            this.myHandler = myHandler;
        }

        @OnTrigger
        public boolean increment() {
            count++;
            return true;
        }

    }

}
