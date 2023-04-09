package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.builder.dataflow.DataFlow;
import com.fluxtion.compiler.generation.forkjoin.ForkJoinTest.MyConverter;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import org.junit.Test;

public class InMemoryForkJoinTest {

    @Test
    public void buildInMemory() {
        EventProcessor eventProcessor = Fluxtion.interpret(c -> {
            c.addEventAudit(LogLevel.INFO);
            DataFlow.subscribeToSignal("async_1")
                    .map(MyConverter::toUpperStatic)
                    .parallel()
                    .flowSupplier();
        });
        eventProcessor.init();
        eventProcessor.publishSignal("async_1");
    }
}
