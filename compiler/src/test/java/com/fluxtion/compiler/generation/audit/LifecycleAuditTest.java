package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.Start;
import com.fluxtion.runtime.annotations.Stop;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.audit.LogRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
}
