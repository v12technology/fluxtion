package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.audit.LogRecord;
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
        public int terminateCount;
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
            terminateCount++;
            return super.terminateRecord();
        }

        public int getTerminateCount() {
            return terminateCount;
        }
    }
}
