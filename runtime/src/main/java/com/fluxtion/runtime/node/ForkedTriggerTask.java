package com.fluxtion.runtime.node;

import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.concurrent.RecursiveTask;

/**
 * Wraps a trigger method and executes it using the ForkJoin framework.
 */
public class ForkedTriggerTask extends RecursiveTask<Boolean> {

    private final SerializableSupplier<Boolean> nodeTask;

    public ForkedTriggerTask(SerializableSupplier<Boolean> nodeTask) {
        this.nodeTask = nodeTask;
    }

    @Override
    protected Boolean compute() {
        return nodeTask.get();
    }

}
