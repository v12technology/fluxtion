package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.EventLogNode;
import org.junit.Assert;
import org.junit.Test;

public class AuditAfterException extends MultipleSepTargetInProcessTest {
    public AuditAfterException(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void captureRecord_with_Exception_in_OnEvent() {
        addAuditor();
        sep(c -> {
            c.addNode(new FailingHandler());
        });
        init();
        sep.setAuditLogProcessor(l -> {
        });
        onEvent("test");
        String lastRecord;
        try {
            onEvent("FAIL");
            lastRecord = "";
        } catch (Throwable t) {
            lastRecord = sep.getLastAuditLogRecord();
        }
        Assert.assertTrue(lastRecord.contains("FAIL"));
    }


    @Test
    public void captureRecordAndAuditValue_with_Exception_in_OnEvent() {
        addAuditor();
        sep(c -> {
            c.addNode(new FailingHandlerWithAuditValues());
        });
        init();
        sep.setAuditLogProcessor(l -> {
        });
        onEvent("test");
        String lastRecord;
        try {
            onEvent("FAIL");
            lastRecord = "";
        } catch (Throwable t) {
            lastRecord = sep.getLastAuditLogRecord();
        }
        Assert.assertTrue(lastRecord.contains("lastMessage: FAIL"));
    }

    public static class FailingHandler {
        @OnEventHandler
        public boolean stringUpdate(String in) {
            if (in.equalsIgnoreCase("FAIL")) {
                throw new RuntimeException("failed");
            }
            return true;
        }
    }

    public static class FailingHandlerWithAuditValues extends EventLogNode {
        @OnEventHandler
        public boolean stringUpdate(String in) {
            auditLog.info("lastMessage", in).info("test", true);
            if (in.equalsIgnoreCase("FAIL")) {
                throw new RuntimeException("failed");
            }
            return true;
        }
    }

}
