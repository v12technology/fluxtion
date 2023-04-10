package com.fluxtion.runtime.node;

import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.runtime.audit.EventLogSource;
import com.fluxtion.runtime.audit.EventLogger;
import com.fluxtion.runtime.audit.NullEventLogger;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableSupplier;

import java.util.concurrent.RecursiveTask;

/**
 * Wraps a trigger method and executes it using the ForkJoin framework.
 */
public class ForkedTriggerTask extends RecursiveTask<Boolean> implements EventLogSource {

    private final transient String methodName;
    private final SerializableSupplier<Boolean> nodeTask;
    private final String delegateName;
    protected EventLogger auditLog = NullEventLogger.INSTANCE;
    private volatile boolean executingInCycle = false;

    public ForkedTriggerTask(SerializableSupplier<Boolean> nodeTask, String delegateName) {
        this.nodeTask = nodeTask;
        this.methodName = nodeTask.method().getName();
        this.delegateName = delegateName;
    }

    public void onTrigger() {
        executingInCycle = true;
        fork();
    }

    public boolean afterEvent() {
        if (executingInCycle) {
            executingInCycle = false;
            return join();
        }
        return executingInCycle;
    }

    @Override
    public void reinitialize() {
        afterEvent();
        super.reinitialize();
    }

    @Override
    protected Boolean compute() {
        if (auditLog.canLog(LogLevel.DEBUG)) {
            auditLog.debug("thread", Thread.currentThread().getName())
                    .debug("delegate", delegateName)
                    .debug("delegateMethod", methodName);
        }
        return nodeTask.get();
    }

    @Override
    public void setLogger(EventLogger log) {
        this.auditLog = log;
    }

}
