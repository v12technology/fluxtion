package com.fluxtion.compiler.generation.forkjoin;

import com.fluxtion.runtime.node.ForkedTriggerTask;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.Test;

public class ForkJoinTest {

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

}
