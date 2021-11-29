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
package com.fluxtion.generator.graphcombinations;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.generator.model.CbMethodHandle;
import com.fluxtion.generator.model.SimpleEventProcessorModel;
import com.fluxtion.generator.model.TopologicallySortedDependencyGraph;
import com.fluxtion.generator.util.MultipleSepTargetInProcessTest;
import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.fluxtion.api.partition.LambdaReflection.getMethod;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author V12 Technology Ltd.
 */
public class HandlerAsDependency extends MultipleSepTargetInProcessTest {

    public HandlerAsDependency(boolean compiledSep) {
        super(compiledSep);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(false);
    }


    @Test
    public void testModel() throws Exception {
        TopologicallySortedDependencyGraph graph = new TopologicallySortedDependencyGraph(
                Arrays.asList(new ChildNoEventHandler(new StringHandler()))
        );
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
        sep((c) -> c.addNode(new ChildNoEventHandler(new StringHandler()), "intHandler"));
        assertThat(
                simpleEventProcessorModel.getDispatchMapForGraph().stream()
                        .map(CbMethodHandle::getMethod)
                        .collect(Collectors.toList()),
                is(Arrays.asList(
                        getMethod(StringHandler::newString),
                        getMethod(ChildNoEventHandler::updated))
                )
        );

        //order unimportant
        assertThat(
                simpleEventProcessorModel.getDispatchMapForGraph().stream()
                        .map(CbMethodHandle::getMethod)
                        .collect(Collectors.toList()),
               hasItems(
                        getMethod(StringHandler::newString),
                        getMethod(ChildNoEventHandler::updated)
                )
        );


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
    }


    @Test
    public void handlerAsDependency() {
        sep((c) -> c.addNode(new IntegerHandler(new StringHandler()), "intHandler"));
        IntegerHandler intHandler = getField("intHandler");
        assertFalse(intHandler.notified);
        //int
        onEvent(1);
        assertTrue(intHandler.notified);
        assertFalse(intHandler.parent.isNotified());
        //string
        onEvent("hello world");
        assertTrue(intHandler.notified);
        assertTrue(intHandler.parent.isNotified());
    }


    @Test
    @Ignore
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

        @EventHandler
        public boolean newString(String s) {
            notified = true;
            return true;
        }
    }

    @Data
    public static class IntegerHandler {
        final StringHandler parent;
        boolean notified = false;

        @EventHandler
        public boolean newInt(Integer s) {
            notified = true;
            return true;
        }

        @OnEvent
        public boolean updated() {
            notified = true;
            return true;
        }

    }

    @Data
    public static class ChildNoEventHandler {
        final StringHandler parent;
        boolean notified = false;

        @OnEvent
        public boolean updated() {
            notified = true;
            return true;
        }

        public void run() {

        }
    }

}
