package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.CompiledOnlySepTest;
import com.fluxtion.runtime.annotations.*;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.node.ForkedTriggerTask;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Random;

@Slf4j
public class ForkJoinTest extends CompiledOnlySepTest {

    public ForkJoinTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
        super(compile);
    }

    @Test
    public void forkGraph() {
        writeSourceFile = true;
        sep(c -> {
            c.addNode(new CompletionTrigger(new LongRunningTrigger[]{
                    new LongRunningTrigger("dopey"),
                    new LongRunningTrigger("sleepy"),
                    new LongRunningTrigger("doc")
            }
            ));
        });
        onEvent("hi");
        onEvent("ho");
    }

    @Test
    public void fjTest() {
        ForkedTriggerTask task = new ForkedTriggerTask(new MyTask("task_1")::executeTask);
        ForkedTriggerTask task2 = new ForkedTriggerTask(new MyTask("task_2")::executeTask);
        System.out.println("forking currentThread:" + Thread.currentThread().getName());
        task.fork();
        task2.fork();
        System.out.println("waiting currentThread:" + Thread.currentThread().getName());
        task.join();
        task2.join();
        System.out.println("completed currentThread:" + Thread.currentThread().getName());
    }

    @Value
    public static class MyTask {
        String name;

        @SneakyThrows
        private boolean executeTask() {
            System.out.println(name + " forked task start currentThread:" + Thread.currentThread().getName());
            Thread.sleep(1_000);
            System.out.println(name + " forked task complete currentThread:" + Thread.currentThread().getName());
            return true;
        }
    }

    @Slf4j
    public static class InputHandler {

        @OnEventHandler
        public boolean stringHandler(String in) {
            log.info("event:{}", in);
            return true;
        }
    }

    @Slf4j
    public static class LongRunningTrigger {

        //        @SepNode
        private final InputHandler inputHandler;
        private final String name;

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
            long millis = (long) (new Random().nextDouble() * 1200);
            log.info("{} start sleep:{}", name, millis);
            Thread.sleep(millis);
            log.info("{} completed", name);
            return true;
        }

        @AfterTrigger
        public void afterTrigger() {
            log.info("{} afterTrigger", name);
        }

        @AfterEvent
        public void afterEvent() {
            log.info("{} afterEvent", name);
        }
    }

    @Value
    @Slf4j
    public static class CompletionTrigger {

        LongRunningTrigger[] tasks;

        @OnParentUpdate
        public void taskUpdated(LongRunningTrigger task) {
            log.info("parentCallBack:{}", task.name);
        }

        @OnTrigger(forkExecution = true)
        public boolean collectSlowResults() {
            log.info("Collecting results");
            return true;
        }

        @AfterTrigger
        public void afterTrigger() {
            log.info("afterTrigger");
        }
    }
}
