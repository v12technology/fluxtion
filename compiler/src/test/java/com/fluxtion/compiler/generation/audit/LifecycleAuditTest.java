package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.audit.LogRecord;
import com.fluxtion.runtime.event.Event;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifecycleAuditTest extends MultipleSepTargetInProcessTest {
    public LifecycleAuditTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void lifecycleLog() {
        List<LogRecord> logSink = new ArrayList<>();
        addAuditor();
        sep(c -> c.addNode(new MyNode(new Parent())));
        sep.setAuditLogProcessor(logSink::add);
        logSink.clear();
        start();
        Assert.assertEquals(1, logSink.size());
        onEvent("test");
        Assert.assertEquals(2, logSink.size());
        onEvent("test2");
        Assert.assertEquals(3, logSink.size());
        stop();
        Assert.assertEquals(4, logSink.size());
        tearDown();
        Assert.assertEquals(5, logSink.size());
    }

    @Test
    public void replaceLogRecord() {
        List<LogRecord> logSink = new ArrayList<>();
        MyLogRecord myLogRecord = new MyLogRecord();
        addAuditor();
        sep(c -> c.addNode(new MyNode(new Parent())));
        sep.setAuditLogProcessor(logSink::add);
        sep.setAuditLogRecordEncoder(myLogRecord);
        start();
        Assert.assertEquals("started", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals(2, myLogRecord.getTerminateCount());

        onEvent("test");
        Assert.assertEquals("eventHandler", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("test", myLogRecord.logMap.get("message"));
        Assert.assertEquals(3, myLogRecord.getTerminateCount());

        onEvent("test2");
        Assert.assertEquals("eventHandler", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("test2", myLogRecord.logMap.get("message"));
        Assert.assertNull(myLogRecord.logMap.get("debugMessage"));
        Assert.assertEquals(4, myLogRecord.getTerminateCount());

        sep.setAuditLogLevel(EventLogControlEvent.LogLevel.DEBUG);
        Assert.assertEquals(5, myLogRecord.getTerminateCount());

        onEvent("testDebug");
        Assert.assertEquals("eventHandler", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("message"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("debugMessage"));
        Assert.assertEquals(6, myLogRecord.getTerminateCount());

        stop();
        Assert.assertEquals("stop", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("message"));
        Assert.assertEquals(7, myLogRecord.getTerminateCount());

        tearDown();
        Assert.assertEquals("teardown", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("message"));
        Assert.assertEquals(8, myLogRecord.getTerminateCount());
    }

    @Test
    public void noOpWhenLogLevelNone() {
        MyLogRecord myLogRecord = new MyLogRecord();
        addAuditor();
        sep(c -> c.addNode(new MyNode(new Parent())));
        sep.setAuditLogRecordEncoder(myLogRecord);
        start();
        Assert.assertEquals("started", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals(2, myLogRecord.getTerminateCount());

        sep.setAuditLogLevel(EventLogControlEvent.LogLevel.NONE);
        myLogRecord.resetCounts();

        onEvent("test");
        onEvent("test1");
        onEvent("test2");
        Assert.assertTrue(myLogRecord.logMap.isEmpty());
        Assert.assertTrue(myLogRecord.toString().isEmpty());
        Assert.assertEquals(0, myLogRecord.getAddSourceIdCount());
        Assert.assertEquals(0, myLogRecord.getTriggerCount());
        Assert.assertEquals(0, myLogRecord.getTerminateCount());

        sep.setAuditLogLevel(EventLogControlEvent.LogLevel.DEBUG);
        Assert.assertEquals(1, myLogRecord.getTerminateCount());

        onEvent("testDebug");
        Assert.assertEquals("eventHandler", myLogRecord.logMap.get("lifecycle"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("message"));
        Assert.assertEquals("testDebug", myLogRecord.logMap.get("debugMessage"));
        Assert.assertEquals(2, myLogRecord.getTerminateCount());
    }

    public static class Parent extends EventLogNode {
        @Initialise
        public void init() {
            auditLog.info("lifecycle", "init");
        }

        @Start
        public void start() {
            auditLog.info("lifecycle", "started");
        }

        @Stop
        public void stop() {
            auditLog.info("lifecycle", "stop");
        }

        @TearDown
        public void teardown() {
            auditLog.info("lifecycle", "teardown");
        }

        @OnEventHandler
        public boolean eventHandler(String in) {
            auditLog.info("lifecycle", "eventHandler");
            auditLog.info("message", in);
            auditLog.debug("debugMessage", in);
            return false;
        }
    }

    public static class MyNode extends EventLogNode {

        private final Parent parent;

        public MyNode(Parent parent) {
            this.parent = parent;
        }

        @Initialise
        public void init() {
            auditLog.info("lifecycle", "init");
        }

        @Start
        public void start() {
            auditLog.info("lifecycle", "started");
        }

        @Stop
        public void stop() {
            auditLog.info("lifecycle", "stop");
        }

        @TearDown
        public void teardown() {
            auditLog.info("lifecycle", "teardown");
        }

        @OnEventHandler
        public boolean eventHandler(String in) {
            auditLog.info("lifecycle", "eventHandler");
            return false;
        }

    }

    public static class MyLogRecord extends LogRecord {
        @Getter
        public int terminateCount;
        @Getter
        int triggerCount;
        @Getter
        int addSourceIdCount;
        public Map<String, String> logMap = new HashMap<>();

        public MyLogRecord() {
            super(null);
        }

        @Override
        public void addRecord(String sourceId, String propertyKey, CharSequence value) {
            logMap.put(propertyKey, value.toString());
        }

        @Override
        public boolean terminateRecord() {
            if (loggingEnabled()) {
                terminateCount++;
            }
            return super.terminateRecord();
        }

        @Override
        public void triggerEvent(Event event) {
            if (loggingEnabled()) {
                triggerCount++;
            }
            super.triggerEvent(event);
        }

        @Override
        public void triggerObject(Object event) {
            if (loggingEnabled()) {
                triggerCount++;
            }
            super.triggerObject(event);
        }

        @Override
        protected void addSourceId(String sourceId, String propertyKey) {
            if (loggingEnabled()) {
                addSourceIdCount++;
            }
            super.addSourceId(sourceId, propertyKey);
        }

        void resetCounts() {
            terminateCount = 0;
            triggerCount = 0;
            addSourceIdCount = 0;
            logMap.clear();
        }
    }
}
