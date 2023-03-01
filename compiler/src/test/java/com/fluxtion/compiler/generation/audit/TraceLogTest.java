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
package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.compiler.generation.util.YamlLogRecordListener;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.audit.EventLogManager;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.audit.StructuredLogRecord;
import com.fluxtion.test.event.CharEvent;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TraceLogTest extends MultipleSepTargetInProcessTest {

    public TraceLogTest(boolean compiledSep) {
        super(compiledSep);
    }


    public static class NoAuditNode {
        @OnEventHandler
        public void charEvent(CharEvent event) {

        }
    }

    @Test
    public void noTraceWhenBelowMinimumLoggerLevel() {
        LongAdder count = new LongAdder();
        sep(c -> {
            c.addNode(new NoAuditNode(), "greatGrandChild");
            c.addEventAudit(LogLevel.DEBUG);
        });
        YamlLogRecordListener yamlRecord = new YamlLogRecordListener();
        onEvent(new EventLogControlEvent(logRecord -> count.increment()));
        onEvent(new CharEvent('a'));
        assertThat(count.intValue(), is(0));
        sep.setAuditLogLevel(LogLevel.DEBUG);
        onEvent(new CharEvent('a'));
        assertThat(count.intValue(), is(2));
    }

    @Test
    public void testNoTrace() {
        fixedPkg = true;
        YamlLogRecordListener yamlRecord = new YamlLogRecordListener();
        sep(c -> {
            ParentNode parent = c.addNode(new ParentNode(), "parent");
            ChildNode child = c.addNode(new ChildNode(parent), "child");
            GrandChildNode grandChild = c.addNode(new GrandChildNode(child), "grandChild");
            c.addNode(new GreatGrandChildNode(grandChild), "greatGrandChild");
            c.addAuditor(new EventLogManager(), "sampleLogger");
        });
        onEvent(new EventLogControlEvent(yamlRecord));
//        JULLogRecordListener julRecord = new JULLogRecordListener();
//        onEvent(new EventLogControlEvent(julRecord));
        onEvent(new CharEvent('a'));

        List<StructuredLogRecord> eventList = yamlRecord.getEventList();
        assertThat(eventList.size(), is(1));
        assertThat("CharEvent", is(eventList.get(0).getEventType()));
        final List<StructuredLogRecord.AuditRecord> auditLogs = eventList.get(0).getAuditLogs();
        assertThat(2, is(auditLogs.size()));
        //check first is parent then child
        assertThat("parent", is(auditLogs.get(0).getNodeId()));
        assertThat("child", is(auditLogs.get(1).getNodeId()));
        //check parent has right char
        assertThat("a", is(auditLogs.get(0).getPropertyMap().get("char")));
    }

    @Test
    public void testWithTrace() {
        fixedPkg = true;
        YamlLogRecordListener yamlRecord = new YamlLogRecordListener();
        sep(c -> {
            ParentNode parent = c.addNode(new ParentNode(), "parent");
            ChildNode child = c.addNode(new ChildNode(parent), "child");
            GrandChildNode grandChild = c.addNode(new GrandChildNode(child), "grandChild");
            c.addNode(new GreatGrandChildNode(grandChild), "greatGrandChild");
            c.addAuditor(new EventLogManager().tracingOn(LogLevel.INFO).printEventToString(false), "sampleLogger");
        });
        onEvent(new EventLogControlEvent(yamlRecord));
        onEvent(new CharEvent('b'));

        List<StructuredLogRecord> eventList = yamlRecord.getEventList();
        assertThat(2, is(eventList.size()));
        assertThat("EventLogControlEvent", is(eventList.get(0).getEventType()));
        assertThat("CharEvent", is(eventList.get(1).getEventType()));
        final List<StructuredLogRecord.AuditRecord> auditLogs = eventList.get(1).getAuditLogs();
        assertThat(auditLogs.size(), is(4));
        //check first is parent then child
        assertThat("parent", is(auditLogs.get(0).getNodeId()));
        assertThat("child", is(auditLogs.get(1).getNodeId()));
        assertThat("grandChild", is(auditLogs.get(2).getNodeId()));
        assertThat("greatGrandChild", is(auditLogs.get(3).getNodeId()));
        //check parent has right char
        assertThat("b", is(auditLogs.get(0).getPropertyMap().get("char")));

    }

    @Test
    public void testWithTraceFinestLevel() {
        fixedPkg = true;
        YamlLogRecordListener yamlRecord = new YamlLogRecordListener();
        sep(c -> {
            ParentNode parent = c.addNode(new ParentNode(), "parent");
            ChildNode child = c.addNode(new ChildNode(parent), "child");
            GrandChildNode grandChild = c.addNode(new GrandChildNode(child), "grandChild");
            c.addNode(new GreatGrandChildNode(grandChild), "greatGrandChild");
            c.addAuditor(new EventLogManager().tracingOn(LogLevel.TRACE).printEventToString(false), "sampleLogger");
        });
        onEvent(new EventLogControlEvent(yamlRecord));
        onEvent(new CharEvent('c'));//2 audit
        onEvent(new EventLogControlEvent(LogLevel.TRACE));//0 audit
        onEvent(new CharEvent('d'));//4 audit

        List<StructuredLogRecord> eventList = yamlRecord.getEventList();
        assertThat(3, is(eventList.size()));
        assertThat("CharEvent", is(eventList.get(0).getEventType()));
        List<StructuredLogRecord.AuditRecord> auditLogs = eventList.get(0).getAuditLogs();
        assertThat(2, is(auditLogs.size()));
        //check first is parent then child
        assertThat("parent", is(auditLogs.get(0).getNodeId()));
        assertThat("child", is(auditLogs.get(1).getNodeId()));
        //check parent has right char
        assertThat("c", is(auditLogs.get(0).getPropertyMap().get("char")));
        //check control events
//        assertThat("EventLogControlEvent", is(eventList.get(0).getEventType()));
        assertThat("EventLogControlEvent", is(eventList.get(1).getEventType()));
        //now should be on trace
        auditLogs = eventList.get(2).getAuditLogs();
        assertThat(auditLogs.size(), is(4));
        //check first is parent then child
        assertThat("parent", is(auditLogs.get(0).getNodeId()));
        assertThat("child", is(auditLogs.get(1).getNodeId()));
        assertThat("grandChild", is(auditLogs.get(2).getNodeId()));
        assertThat("greatGrandChild", is(auditLogs.get(3).getNodeId()));
        //check parent has right char
        assertThat("d", is(auditLogs.get(0).getPropertyMap().get("char")));
    }

    public static class ParentNode extends EventLogNode {

        @Inject
        public MyNode myNode;

        @OnEventHandler
        public void charEvent(CharEvent event) {
            auditLog.info("char", event.getChar());
        }

    }

    public static class ChildNode extends EventLogNode {

        private final ParentNode parent;

        public ChildNode(ParentNode parent) {
            this.parent = parent;
        }

        @OnTrigger
        public void onEvent() {
            auditLog.info("child", true);
        }
    }

    public static class GrandChildNode extends EventLogNode {

        private final ChildNode parent;

        public GrandChildNode(ChildNode parent) {
            this.parent = parent;
        }

        @OnTrigger
        public void onEvent() {
        }
    }

    public static class GreatGrandChildNode {

        private final GrandChildNode parent;

        public GreatGrandChildNode(GrandChildNode parent) {
            this.parent = parent;
        }

        @OnTrigger
        public void onEvent() {
        }
    }

    public static class MyNode {

        public boolean registerCalled = false;

    }

}
