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
package com.fluxtion.generator.audit;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.TearDown;
import com.fluxtion.api.audit.Auditor;
import com.fluxtion.generator.util.BaseSepInProcessTest;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.NodeWithParentList;
import com.fluxtion.test.event.TestEvent;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class RegistrationListenerTest extends BaseSepInProcessTest {

    @Test
    public void testAudit() {
        sep(c -> {
            EventHandlerCb e1 = c.addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = c.addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = c.addNode(new EventHandlerCb("3", 3));
            NodeWithParentList root = c.addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
            root.parentsNoType.add(c.addNode(new SimpleNode()));
            //audit
            c.addAuditor(new MyNodeAudit(), "myAuditor");
        });

        MyNodeAudit auditNode = getField("myAuditor");
        assertThat(auditNode.registeredNodes.size(), is(5));
        onEvent(new TestEvent(1));
        assertThat(auditNode.invokeCount, is(2));
    }

    @Test
    public void testAuditInline() {
        sep(c -> {
            EventHandlerCb e1 = c.addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = c.addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = c.addNode(new EventHandlerCb("3", 3));
            NodeWithParentList root = c.addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
            root.parentsNoType.add(c.addNode(new SimpleNode()));
            //audit
            c.addAuditor(new MyNodeAudit(), "myAuditor");
            //inline
            c.inlineEventHandling = true;
        });
        MyNodeAudit auditNode = getField("myAuditor");
        assertThat(auditNode.registeredNodes.size(), is(5));
        onEvent(new TestEvent(1));
        assertThat(auditNode.invokeCount, is(2));
    }

    @Test
    public void testNoAuditInline() {
        sep(c -> {
            EventHandlerCb e1 = c.addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = c.addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = c.addNode(new EventHandlerCb("3", 3));
            NodeWithParentList root = c.addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
            root.parentsNoType.add(c.addNode(new SimpleNode()));
            //audit
            c.addAuditor(new MyNodeAudit(), "myAuditor").audit = false;
            //inline
            c.inlineEventHandling = true;
        });
        MyNodeAudit auditNode = getField("myAuditor");
        assertThat(auditNode.registeredNodes.size(), is(5));
        onEvent(new TestEvent(1));
        assertThat(auditNode.invokeCount, is(0));
    }

    public static class MyNodeAudit implements Auditor {

        public HashMap<String, Object> registeredNodes = new HashMap<>();
        public transient int invokeCount;
        public transient boolean audit = true;

        @Override
        public void nodeRegistered(Object node, String nodeName) {
            registeredNodes.put(nodeName, node);
        }

        @Override
        public boolean auditInvocations() {
            return audit;
        }

        @Override
        public void nodeInvoked(Object node, String nodeName, String methodName, Object typedEvent) {
            invokeCount++;
        }

    }

    public static class SimpleNode {

        @OnEvent
        public void event() {

        }

        @Initialise
        public void init() {

        }

        @TearDown
        public void tearDown() {

        }
    }

}
