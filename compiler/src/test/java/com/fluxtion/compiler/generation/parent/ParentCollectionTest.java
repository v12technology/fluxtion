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
package com.fluxtion.compiler.generation.parent;

import com.fluxtion.compiler.generation.model.CallbackMethodModelTest.StringHandler;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.test.event.EventHandlerCbNode;
import com.fluxtion.test.event.NodeWithParentList;
import com.fluxtion.test.event.NodeWithPrivateParentList;
import com.fluxtion.test.event.TestEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ParentCollectionTest extends MultipleSepTargetInProcessTest {

    public ParentCollectionTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void testParentList() {
        sep((c) -> {
            EventHandlerCbNode e1 = c.addNode(new EventHandlerCbNode("1", 1));
            EventHandlerCbNode e2 = c.addNode(new EventHandlerCbNode("2", 2));
            EventHandlerCbNode e3 = c.addNode(new EventHandlerCbNode("3", 3));
            c.addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
        });

        NodeWithParentList root = getField("root");
        assertEquals(3, root.parents.size());
        sep.onEvent(new TestEvent());
        assertEquals(0, root.parentUpdateCount);
        assertEquals(0, root.onEventCount);
        sep.onEvent(new TestEvent(2));
        assertEquals(1, root.parentUpdateCount);
        assertEquals(1, root.onEventCount);
    }

    @Test
    public void testParentListNoType() {
        sep((c) -> {
            EventHandlerCbNode e1 = c.addNode(new EventHandlerCbNode("1", 1));
            EventHandlerCbNode e2 = c.addNode(new EventHandlerCbNode("2", 2));
            EventHandlerCbNode e3 = c.addNode(new EventHandlerCbNode("3", 3));
            NodeWithParentList root = c.addPublicNode(new NodeWithParentList(), "root");
            root.parentsNoType.add(e1);
            root.parentsNoType.add(e2);
            root.parentsNoType.add(e3);
        });
        NodeWithParentList root = getField("root");
        assertEquals(3, root.parentsNoType.size());
        sep.onEvent(new TestEvent());
        assertEquals(0, root.parentUpdateCount);
        assertEquals(0, root.onEventCount);
        sep.onEvent(new TestEvent(2));
        assertEquals(1, root.parentUpdateCount);
        assertEquals(1, root.onEventCount);
    }

    @Test
    public void testPrivateListParents() {
        sep((c) -> {
            EventHandlerCbNode e1 = c.addNode(new EventHandlerCbNode("1", 1));
            EventHandlerCbNode e2 = c.addNode(new EventHandlerCbNode("2", 2));
            EventHandlerCbNode e3 = c.addNode(new EventHandlerCbNode("3", 3));
            NodeWithPrivateParentList root = c.addPublicNode(new NodeWithPrivateParentList(e1, e2, e3), "root");
            root.intList.add(1);
            root.stringList.add("test val");
            root.stringList.add("another val");
            c.setAssignPrivateMembers(true);
        });
        NodeWithPrivateParentList root = getField("root");
        assertEquals(3, root.getParents().size());
        assertEquals(1, root.intList.size());
        assertEquals(2, root.stringList.size());
        sep.onEvent(new TestEvent());
        assertEquals(0, root.parentUpdateCount);
        assertEquals(0, root.onEventCount);
        sep.onEvent(new TestEvent(2));
        assertEquals(1, root.parentUpdateCount);
        assertEquals(1, root.onEventCount);
    }

    @Test
    public void testNamedCollection() {
        sep(c -> {
            c.addNode(new NamedParentCollection(), "test");
        });
        onEvent("g");
        onEvent("g");
        assertEquals(2, getField("test", NamedParentCollection.class).count);
    }

    public static class NamedParentCollection {

        public List<EventHandlerCbNode> eventHandlerCollection = new ArrayList<>();
        public List<StringHandler> stringCollection = new ArrayList<>();

        public int count;
//        public IntegerHandler stringHandler = new IntegerHandler(new StringHandler());

        public NamedParentCollection() {
            eventHandlerCollection.add(new EventHandlerCbNode("a", 1));
            eventHandlerCollection.add(new EventHandlerCbNode("b", 2));
            eventHandlerCollection.add(new EventHandlerCbNode("c", 3));
            stringCollection.add(new StringHandler());
        }

        @OnParentUpdate("stringCollection")
        public void update(Object stringHandler) {
            count++;
        }

    }

}
