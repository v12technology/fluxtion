/*
 * Copyright (C) 2020 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.graphcombinations;

import com.fluxtion.compiler.generation.model.CbMethodHandle;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.compiler.generation.model.TopologicallySortedDependencyGraph;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import lombok.Data;
import org.junit.Test;

import java.util.stream.Collectors;

import static com.fluxtion.runtime.partition.LambdaReflection.getMethod;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author 2024 gregory higgins.
 */
public class HandlerAsDependencyTest extends MultipleSepTargetInProcessTest {

    public HandlerAsDependencyTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testModel() throws Exception {
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(
                new ChildNoEventHandler(new StringHandler()));
        SimpleEventProcessorModel sep = new SimpleEventProcessorModel(graph);
        sep.generateMetaModel();
        assertThat(
                sep.getDispatchMapForGraph().stream()
                        .map(CbMethodHandle::getMethod)
                        .collect(Collectors.toList()),
                hasItems(
                        getMethod(StringHandler::newString),
                        getMethod(ChildNoEventHandler::updated)
                )
        );
    }

    @Test
    public void noHandlerAsDependency() {
//        writeSourceFile = true;
        sep((c) -> c.addNode(new ChildNoEventHandler(new StringHandler()), "intHandler"));
        //order unimportant
        ChildNoEventHandler intHandler = getField("intHandler");
        assertFalse(intHandler.notified);
        //int
        onEvent(1);
        assertFalse(intHandler.notified);
        assertFalse(intHandler.parent.isNotified());
        //string
        onEvent("hello world");
        assertTrue(intHandler.notified);
        assertTrue(intHandler.parent.isNotified());

        if (simpleEventProcessorModel != null) {
            assertThat(
                    simpleEventProcessorModel.getDispatchMapForGraph().stream()
                            .map(CbMethodHandle::getMethod)
                            .collect(Collectors.toList()),
                    hasItems(
                            getMethod(StringHandler::newString),
                            getMethod(ChildNoEventHandler::updated)
                    )
            );
        }

    }


    @Test
    public void handlerAsDependency() {
        sep((c) -> c.addNode(new IntegerHandler(new StringHandler()), "intHandler"));
        IntegerHandler intHandler = getField("intHandler");
        assertFalse(intHandler.notified);
        //int
        onEvent(1);
        assertTrue(intHandler.isIntEventNotified());
        assertFalse(intHandler.isNotified());
        assertFalse(intHandler.parent.isNotified());
        //reset
        intHandler.setIntEventNotified(false);
        intHandler.setNotified(false);
        //string
        onEvent("hello world");
        assertFalse(intHandler.isIntEventNotified());
        assertTrue(intHandler.isNotified());
        assertTrue(intHandler.parent.isNotified());
    }


    @Test
    public void mappedAndUnMappedEventTypes() {
        sep((c) -> c.addNode(new StringHandler(), "strHandler"));
        StringHandler strHandler = getField("strHandler");
        assertFalse(strHandler.notified);
        onEvent("hello world");
        assertTrue(strHandler.notified);
        strHandler.notified = false;
        onEvent(111);
        assertFalse(strHandler.notified);
    }

    @Data
    public static class StringHandler {

        boolean notified = false;

        @OnEventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class IntegerHandler {
        final StringHandler parent;
        boolean notified = false;
        boolean intEventNotified = false;

        @OnEventHandler
        public boolean newInt(Integer s) {
            intEventNotified = true;
            return true;
        }

        @OnTrigger
        public boolean updated() {
            notified = true;
            return true;
        }

    }

    @Data
    public static class ChildNoEventHandler {
        final StringHandler parent;
        boolean notified = false;

        @OnTrigger
        public boolean updated() {
            notified = true;
            return true;
        }

        public void run() {

        }
    }

}
