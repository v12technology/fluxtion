/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.generator.model.parentlistener;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.NodeWithParentList;
import com.fluxtion.test.event.NodeWithPrivateParentList;
import com.fluxtion.test.event.TestEvent;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ParentCollectionTest extends BaseSepTest {

    @Test
    public void testParentList() {
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(ParentListProcessorSep.class);
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
    public void testParentListNoTYpe() {
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(ParentListNoTypeProcessorSep.class);
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
        //System.out.println("testPrivateListParents");
//        com.fluxtion.generator.model.parentlistener.ParentCollectionTest1497363516748.TestProcessor processor = new com.fluxtion.generator.model.parentlistener.ParentCollectionTest1497363516748.TestProcessor();
        compileCfg.setAssignNonPublicMembers(true);
        com.fluxtion.runtime.lifecycle.EventHandler sep = buildAndInitSep(ParentPrivateListProcessorSep.class);
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

    public static class ParentListProcessorSep extends SEPConfig {

        {
            EventHandlerCb e1 = addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = addNode(new EventHandlerCb("3", 3));
            addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
        }
    }

    public static class ParentListNoTypeProcessorSep extends SEPConfig {

        {
            EventHandlerCb e1 = addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = addNode(new EventHandlerCb("3", 3));
            NodeWithParentList root = addPublicNode(new NodeWithParentList(), "root");
            root.parentsNoType.add(e1);
            root.parentsNoType.add(e2);
            root.parentsNoType.add(e3);
        }
    }

    public static class ParentPrivateListProcessorSep extends SEPConfig {

        {
            EventHandlerCb e1 = addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = addNode(new EventHandlerCb("3", 3));
            NodeWithPrivateParentList root = addPublicNode(new NodeWithPrivateParentList(e1, e2, e3), "root");
            root.intList.add(1);
            root.stringList.add("test val");
            root.stringList.add("another val");
        }
    }
}
