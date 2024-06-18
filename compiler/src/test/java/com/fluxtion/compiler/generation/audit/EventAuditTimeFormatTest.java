package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.audit.EventLogNode;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EventAuditTimeFormatTest extends MultipleSepTargetInProcessTest {
    public EventAuditTimeFormatTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void customTimeFormatter() {
        addAuditor();
        sep(new MyHandler());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
        setTime(0);
        eventProcessor().setAuditTimeFormatter((a, l) -> {
            dtf.formatTo(LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault()), a);
        });

        StringBuilder sb = new StringBuilder();

        sep.setAuditLogProcessor(l -> {
            sb.append(l.asCharSequence());
        });

        onEvent("TEST");

        Assert.assertTrue(sb.toString().contains("01-01-1970 01:00:00.000"));
    }

    public static class MyHandler extends EventLogNode {
        @OnEventHandler
        public boolean stringHandler(String in) {
            auditLog.info("in", in);
            return true;
        }
    }
}
