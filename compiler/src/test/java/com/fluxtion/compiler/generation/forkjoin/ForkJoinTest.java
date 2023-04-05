package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.node.NamedNode;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
public class ForkJoinTest extends CompiledOnlySepTest {

    public ForkJoinTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void forkGraph() {
        writeSourceFile = true;
        sep(c -> {
            InputHandler inputHandler = new InputHandler();
            c.addNode(new CompletionTrigger(new LongRunningTrigger[]{
                    new LongRunningTrigger(inputHandler, "dopey"),
                    new LongRunningTrigger(inputHandler, "sleepy"),
                    new LongRunningTrigger("doc")
            }
            ));
        });
        onEvent("hi");
        getField("collector", CompletionTrigger.class).reset();
        onEvent("ho");
    }


    @Slf4j
    public static class InputHandler {

        @OnEventHandler
        public boolean stringHandler(String in) {
            log.debug("event:{}", in);
            return true;
        }
    }

    @Slf4j
    public static class LongRunningTrigger {
        private final InputHandler inputHandler;
        private final String name;
        private String error;
        private boolean taskComplete = false;
        private boolean afterTriggerComplete = false;
        private boolean afterEventComplete = false;

        public LongRunningTrigger(@AssignToField("inputHandler") InputHandler inputHandler, String name) {
            this.inputHandler = inputHandler;
            this.name = name;
        }

        public LongRunningTrigger(String name) {
            this(new InputHandler(), name);
        }

        @SneakyThrows
        @OnTrigger(forkExecution = true)
        public boolean startLongTask() {
            if (afterTriggerComplete || afterEventComplete) {
                throw new RuntimeException("afterTrigger and afterEvent should not have completed");
            }
            long millis = (long) (new Random().nextDouble() * 1200);
            log.debug("{} start sleep:{}", name, millis);
            Thread.sleep(millis);
            log.debug("{} completed", name);
            taskComplete = true;
            return true;
        }

        @AfterTrigger
        public void afterTrigger() {
            if (!taskComplete || afterEventComplete) {
                throw new RuntimeException("startLongTask should be complete and afterEvent should not have completed");
            }
            log.debug("{} afterTrigger", name);
            afterTriggerComplete = true;
        }

        @AfterEvent
        public void afterEvent() {
            if (!taskComplete || !afterTriggerComplete) {
                throw new RuntimeException("startLongTask and afterEvent should be completed");
            }
            log.debug("{} afterEvent", name);
            afterTriggerComplete = true;
            taskComplete = false;
            afterTriggerComplete = false;
        }
    }

    @Data
    @Slf4j
    public static class CompletionTrigger implements NamedNode {
        private boolean taskComplete = false;
        private boolean afterTriggerComplete = false;
        private boolean parentUpdateComplete = false;
        private Set<String> updateSetSet = new HashSet<>();

        private final LongRunningTrigger[] tasks;

        @OnParentUpdate
        public void taskUpdated(LongRunningTrigger task) {
            if (afterTriggerComplete || taskComplete) {
                throw new RuntimeException("afterTrigger and collectSlowResults should not have completed");
            }
            log.debug("parentCallBack:{}", task.name);
            updateSetSet.add(task.name);
            parentUpdateComplete = updateSetSet.size() == tasks.length;
        }

        @OnTrigger(forkExecution = true)
        public boolean collectSlowResults() {
            if (afterTriggerComplete || !parentUpdateComplete) {
                throw new RuntimeException("afterTrigger and afterEvent should not have completed");
            }
            log.debug("Collecting results");
            taskComplete = true;
            return true;
        }

        @AfterTrigger
        public void afterTrigger() {
            if (!taskComplete || !parentUpdateComplete) {
                throw new RuntimeException("collectSlowResults should be complete and afterEvent should not have completed");
            }
            log.debug("afterTrigger");
            afterTriggerComplete = true;
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
