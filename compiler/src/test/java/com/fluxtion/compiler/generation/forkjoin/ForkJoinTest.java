package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.audit.EventLogNode;
import com.fluxtion.runtime.audit.LogRecord;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
public class ForkJoinTest extends MultipleSepTargetInProcessTest {

    public ForkJoinTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    public void log(LogRecord logRecord) {
        log.info(logRecord.toString());
    }

    @Test
    public void forkGraph() {
        sep(c -> {

            InputHandler inputHandler = new InputHandler("dopey_or_sleepy");
            c.addNode(new CompletionTrigger(new LongRunningTrigger[]{
                    new LongRunningTrigger(inputHandler, "dopey"),
                    new LongRunningTrigger(inputHandler, "sleepy"),
                    new LongRunningTrigger("doc")
            }
            ));
            c.addEventAudit(LogLevel.INFO);
        });
//        sep.setAuditLogProcessor(this::log);
//        sep.setAuditLogLevel(LogLevel.DEBUG);
        onEvent("doc");
        getField("collector", CompletionTrigger.class).reset();
        onEvent("all");
        getField("collector", CompletionTrigger.class).validateAllUpdated();
        getField("collector", CompletionTrigger.class).reset();
        onEvent("ignore");
        getField("collector", CompletionTrigger.class).reset();
        onEvent("dopey_or_sleepy");
        getField("collector", CompletionTrigger.class).reset();
        onEvent("all");
        getField("collector", CompletionTrigger.class).validateAllUpdated();
    }


    @Slf4j
    public static class InputHandler {

        private final String ignoreString;
        String in;

        public InputHandler(@AssignToField("ignoreString") String ignoreString) {
            this.ignoreString = ignoreString;
        }

        @OnEventHandler
        public boolean stringHandler(String in) {
            this.in = in;
            log.debug("event:{}", in);
            return !in.equals(ignoreString) && !in.equals("ignore");
        }
    }

    @Slf4j
    public static class LongRunningTrigger extends EventLogNode {
        private final InputHandler inputHandler;
        private final String name;
        private String error;
        private boolean taskComplete = false;
        private boolean afterTriggerComplete = false;


        public LongRunningTrigger(@AssignToField("inputHandler") InputHandler inputHandler, String name) {
            this.inputHandler = inputHandler;
            this.name = name;
        }

        public LongRunningTrigger(String name) {
            this(new InputHandler(name), name);
        }

        @SneakyThrows
        @OnTrigger(parallelExecution = true)
        public boolean startLongTask() {
            log.debug("{} startLongTask", name);
            if (inputHandler.in.equals(name)) {
                auditLog.info("NoStart", name);
                return false;
            }
            if (afterTriggerComplete) {
                throw new RuntimeException("afterTrigger and afterEvent should not have completed");
            }
            long millis = (long) (new Random().nextDouble() * 50);
            log.debug("{} start sleep:{}", name, millis);
            auditLog.info("starting", name)
                    .info("startTime", System.currentTimeMillis())
                    .info("sleep", millis);
            Thread.sleep(millis);
            log.debug("{} completed", name);
            auditLog.info("finish", name)
                    .info("finishTime", System.currentTimeMillis());
            taskComplete = true;
            return true;
        }

        @AfterTrigger
        public void afterTrigger() {
            log.debug("{} afterTrigger", name);
            if (!taskComplete) {
                throw new RuntimeException("startLongTask should be complete and afterEvent should not have completed");
            }
            afterTriggerComplete = true;
        }

        @AfterEvent
        public void afterEvent() {
            log.debug("{} afterEvent", name);
            if (taskComplete & !afterTriggerComplete) {
                throw new RuntimeException("afterTrigger should be complete");
            }
            if (!taskComplete & afterTriggerComplete) {
                throw new RuntimeException("startLongTask should be completed");
            }
            taskComplete = false;
            afterTriggerComplete = false;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Slf4j
    public static class CompletionTrigger extends EventLogNode implements NamedNode {
        private final LongRunningTrigger[] tasks;
        private boolean taskComplete = false;
        private boolean afterTriggerComplete = false;
        private boolean parentUpdateComplete = false;
        private Set<String> updateSetSet = new HashSet<>();

        @OnParentUpdate
        public void taskUpdated(LongRunningTrigger task) {
            auditLog.info("finished", task.name);
            if (afterTriggerComplete || taskComplete) {
                throw new RuntimeException("afterTrigger and collectSlowResults should not have completed");
            }
            log.debug("parentCallBack:{}", task.name);
            updateSetSet.add(task.name);
            parentUpdateComplete = updateSetSet.size() == tasks.length;
        }

        @OnTrigger(parallelExecution = true)
        public boolean collectResultsAsync() {
            log.debug("collectResultsAsync");
            if (afterTriggerComplete) {
                throw new RuntimeException("afterTriggerComplete should be complete");
            }
            if (updateSetSet.isEmpty()) {
                throw new RuntimeException("at least one update should be received");
            }
            taskComplete = true;
            return true;
        }

        public void validateAllUpdated() {
            if (!parentUpdateComplete) {
                throw new RuntimeException("all should have updated");
            }
        }

        @AfterTrigger
        public void afterTrigger() {
            log.debug("afterTrigger");
            if (!taskComplete) {
                throw new RuntimeException("collectSlowResults should be complete");
            }
            if (updateSetSet.isEmpty()) {
                throw new RuntimeException("at least one update should be received");
            }
            afterTriggerComplete = true;
        }

        @AfterEvent
        public void afterEvent() {
            log.debug("afterEvent");
            if (taskComplete & !afterTriggerComplete) {
                throw new RuntimeException("afterTrigger should be complete");
            }
            if (!taskComplete & afterTriggerComplete) {
                throw new RuntimeException("startLongTask should be completed");
            }
            taskComplete = false;
            afterTriggerComplete = false;
        }

        @Override
        public String getName() {
            return "collector";
        }

        public void reset() {
            taskComplete = false;
            afterTriggerComplete = false;
            parentUpdateComplete = false;
            updateSetSet.clear();
        }
    }
}
