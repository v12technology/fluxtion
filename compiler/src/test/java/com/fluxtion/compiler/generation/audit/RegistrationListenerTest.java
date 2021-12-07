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
package com.fluxtion.compiler.generation.audit;

import com.fluxtion.runtim.annotations.Initialise;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.TearDown;
import com.fluxtion.runtim.audit.Auditor;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.NodeWithParentList;
import com.fluxtion.test.event.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
@Slf4j
public class RegistrationListenerTest extends MultipleSepTargetInProcessTest {

    public RegistrationListenerTest(boolean compiledSep) {
        super(compiledSep);
    }

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
        if (compiledSep) {
            assertThat(auditNode.invokeCount, is(2));
        } else {
            assertThat(auditNode.invokeCount, is(3));
        }
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
        });
        MyNodeAudit auditNode = getField("myAuditor");
        assertThat(auditNode.registeredNodes.size(), is(5));
        onEvent(new TestEvent(1));
        if (compiledSep) {
            assertThat(auditNode.invokeCount, is(2));
        } else {
            assertThat(auditNode.invokeCount, is(3));
        }
    }

    @Test
    public void testNoInvocationAuditInline() {
        sep(c -> {
            EventHandlerCb e1 = c.addNode(new EventHandlerCb("1", 1));
            EventHandlerCb e2 = c.addNode(new EventHandlerCb("2", 2));
            EventHandlerCb e3 = c.addNode(new EventHandlerCb("3", 3));
            NodeWithParentList root = c.addPublicNode(new NodeWithParentList(e1, e2, e3), "root");
            root.parentsNoType.add(c.addNode(new SimpleNode()));
            //audit
            c.addAuditor(new MyNodeAudit(), "myAuditor").audit = false;
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
            log.debug("node:{} nodeName:{} methodName:{} event:{}", node, nodeName, methodName, typedEvent);
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
