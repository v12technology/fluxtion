package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.forkjoin.ForkJoinTest.MyConverter;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.audit.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class SimpleForkJoinTest extends MultipleSepTargetInProcessTest {

    public SimpleForkJoinTest(SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void buildInMemory() {
        writeSourceFile = true;
        sep(c -> {
            c.addEventAudit(LogLevel.INFO);
            DataFlow.subscribeToSignal("async_1")
                    .map(MyConverter::toUpperStatic)
                    .parallel()
                    .flowSupplier();

        });
        sep.setAuditLogProcessor(this::log);
        sep.setAuditLogLevel(LogLevel.DEBUG);
        publishSignal("async_1");
    }

    public void log(LogRecord logRecord) {
        log.info(logRecord.toString());
    }
}
