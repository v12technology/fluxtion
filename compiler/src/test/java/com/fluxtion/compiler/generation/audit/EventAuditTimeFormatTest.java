package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.EventLogNode;
import org.junit.Assert;
import org.junit.Test;

public class EventAuditTimeFormatTest extends MultipleSepTargetInProcessTest {
    public EventAuditTimeFormatTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void customTimeFormatter() {
        addAuditor();
        sep(new MyHandler());

        setTime(0);
        eventProcessor().setAuditTimeFormatter((a, l) -> {
            a.append("XXX000XXX");
        });

        StringBuilder sb = new StringBuilder();

        sep.setAuditLogProcessor(l -> {
            sb.append(l.asCharSequence());
        });

        onEvent("TEST");

        Assert.assertTrue(sb.toString().contains("XXX000XXX"));
    }

    public static class MyHandler extends EventLogNode {
        @OnEventHandler
        public boolean stringHandler(String in) {
            auditLog.info("in", in);
            return true;
        }
    }
}
