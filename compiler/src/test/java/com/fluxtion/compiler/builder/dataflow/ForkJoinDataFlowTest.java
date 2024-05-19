package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.audit.LogRecord;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.node.NamedNode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ForkJoinDataFlowTest extends MultipleSepTargetInProcessTest {
    public ForkJoinDataFlowTest(SepTestConfig compile) {
        super(compile);
    }

    @SneakyThrows
    public static String toUpper(Object in) {
        Thread.sleep(25);
        String upperCase = in.toString().toUpperCase();
        return upperCase;
    }

    @Test
    public void testSimple() {
        sep(c -> {
            AsyncProcess asynch1 = new AsyncProcess("asynch_1", 200);
            AsyncProcess asynch2 = new AsyncProcess("asynch_1", 100);
            AsyncProcess asynch3 = new AsyncProcess("asynch_1", 85);
//            c.addNode(new SyncCollector("collector", asynch1));
            c.addNode(SyncCollectorMulti.builder().name("multiCollector")
                    .parent(asynch1)
                    .parent(asynch2)
                    .parent(asynch3)
                    .build());
        });
        publishSignal("asynch_1");
//        publishSignal("asynch_1");
//        publishSignal("asynch_1");
    }

    @Test
    public void testSimple2() {
//        writeOutputsToFile(true);
        sep(c -> {
            c.addNode(SyncCollectorMulti.builder().name("multiCollector")
                    .parent(new AsyncProcess("asynch_1", 45))
                    .parent(new AsyncProcess("asynch_2", 110))
                    .parent(new AsyncProcess("asynch_2", 25))
                    .parent(new AsyncProcess("asynch_4", 60)).build());
        });
//        publishSignal("asynch_1");
        publishSignal("asynch_2");
//        publishSignal("asynch_4");
//        publishSignal("asynch_1");
    }

    public void log(LogRecord logRecord) {
        log.info(logRecord.toString());
    }

    @Test
    public void parallelMap() {
//        writeOutputsToFile(true);
//        addAuditor();
        sep(c -> {
            c.addNode(SyncCollectorMulti.builder().name("multiCollector")
                    .parent(
                            DataFlow.subscribeToSignal("async_1")
                                    .map(MyConverter::toUpperStatic)
                                    .parallel()
                                    .flowSupplier()
                    )
                    .parent(
                            DataFlow.subscribeToSignal("async_1")
                                    .map(new MyConverter()::toUpper)
                                    .parallel()
                                    .flowSupplier()
                    )
                    .build()

            );
//            c.addEventAudit(LogLevel.INFO);
        });
//        sep.setAuditLogProcessor(this::log);
//        sep.setAuditLogLevel(LogLevel.DEBUG);
        publishSignal("async_1");
    }

    @Data
    @Slf4j
    @AllArgsConstructor
    public static class AsyncProcess implements NamedNode {
        private final String name;
        private final int waitMillis;
        private final Object parent;

        public AsyncProcess(String name, int wait) {
            this(
                    name + "_" + wait,
                    wait,
                    DataFlow.subscribeToSignal(name)
                            .map(new MyConverter()::toUpper)
                            .flowSupplier());
        }

        @SneakyThrows
        @OnTrigger(parallelExecution = true)
        public boolean trigger() {
            log.info("trigger::start {}", toString());
            Thread.sleep(waitMillis);
            log.info("trigger::complete {}", toString());
            return true;
        }

        @Override
        public String toString() {
            return "AsyncProcess{" +
                    "name='" + name + '\'' +
                    '}';
        }

    }

    @Slf4j
    public static class MyConverter {

        @SneakyThrows
        public static String toUpperStatic(Object in) {
            log.info("converting to upper");
            Thread.sleep(25);
            String upperCase = in.toString().toUpperCase();
            log.info("converted:{}", upperCase);
            return upperCase;
        }


        @SneakyThrows
        public String toUpper(Object in) {
            log.info("converting to upper");
            Thread.sleep(25);
            String upperCase = in.toString().toUpperCase();
            log.info("converted:{}", upperCase);
            return upperCase;
        }
    }

    @Data
    @Slf4j
    public static class SyncCollector implements NamedNode {
        private final String name;
        private final Object parent;

        @OnTrigger
        public boolean trigger() {
            log.info("trigger");
            return true;
        }

        @OnParentUpdate
        public void parentUpdated(Object parent) {
            log.info("parentUpdated");
        }
    }

    @Data
    @Slf4j
    @Builder
    @AllArgsConstructor
    public static class SyncCollectorMulti implements NamedNode {
        private final String name;
        @Singular("parent")
        private final List<Object> parent;

        @OnParentUpdate
        public void parentUpdated(Object parent) {
            log.info("parentUpdated:{}", parent instanceof FlowSupplier ? ((FlowSupplier) parent).get() : "--");
        }

        @OnTrigger
        public boolean trigger() {
            log.info("trigger");
            return true;
        }

        @AfterEvent
        public void postCollect() {
            log.info("postCollect");
        }
    }
}
