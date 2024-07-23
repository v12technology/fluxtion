package com.fluxtion.runtime.server.service.admin;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.subscription.EventToQueuePublisher;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Experimental
@Data
@AllArgsConstructor
public class AdminCommand {
    private Consumer<List<String>> command;
    private PrintStream output;
    private PrintStream errOutput;
    private AdminFunction commandWithOutput;
    private final EventToQueuePublisher<AdminCommand> targetQueue;
    private final Semaphore semaphore = new Semaphore(1);
    private transient List<String> args;

    public AdminCommand(Consumer<List<String>> command, EventToQueuePublisher<AdminCommand> targetQueue) {
        this.command = command;
        this.output = System.out;
        this.errOutput = System.err;
        this.targetQueue = targetQueue;
    }

    public AdminCommand(AdminFunction commandWithOutput, EventToQueuePublisher<AdminCommand> targetQueue) {
        this.commandWithOutput = commandWithOutput;
        this.output = System.out;
        this.errOutput = System.err;
        this.targetQueue = targetQueue;
    }

    public AdminCommand(AdminFunction commandWithOutput) {
        this.commandWithOutput = commandWithOutput;
        this.output = System.out;
        this.errOutput = System.err;
        this.targetQueue = null;
    }

    public AdminCommand(Consumer<List<String>> command) {
        this.command = command;
        this.output = System.out;
        this.errOutput = System.err;
        this.targetQueue = null;
    }

    public void publishCommand(List<String> value) {
        if (targetQueue == null) {
            if (command != null) {
                command.accept(value);
            } else {
                commandWithOutput.processAdminCommand(value, output, errOutput);
            }
        } else {
            try {
                if (semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                    args = value;
                    targetQueue.publish(this);
                    semaphore.acquire();
                    semaphore.release();
                } else {
                    output.println("command is busy try again");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executeCommand() {
        try {
            if (command != null) {
                command.accept(args);
            } else {
                commandWithOutput.processAdminCommand(args, output, errOutput);
            }
        } catch (Exception e) {
            errOutput.println("problem executing command exception:" + e.getMessage());
            e.printStackTrace(errOutput);
        } finally {
            semaphore.release();
        }
    }
}
