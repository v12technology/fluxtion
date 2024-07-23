package com.fluxtion.runtime.server.service.admin;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.server.subscription.*;
import lombok.extern.java.Log;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Experimental
@Log
public class CliAdmin implements EventFlowService, Admin, Lifecycle, EventSource<AdminCommand> {

    private static final AtomicBoolean runLoop = new AtomicBoolean(true);
    private ExecutorService executorService;
    private final CommandProcessor commandProcessor = new CommandProcessor();
    private final Map<String, AdminCommand> registeredCommandMap = new HashMap<>();
    private EventFlowManager eventFlowManager;
    private String serviceName;

    private static class AdminCallbackType implements CallBackType {

        @Override
        public String name() {
            return "AdminCallback";
        }
    }

    @Override
    public void setEventFlowManager(EventFlowManager eventFlowManager, String serviceName) {
        this.eventFlowManager = eventFlowManager;
        this.serviceName = serviceName;
        eventFlowManager.registerEventMapperFactory(AdminCommandInvoker::new, AdminCallbackType.class);
    }

    @Override
    public void init() {
        log.info("init");
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void start() {
        log.info("start");
        executorService.submit(() -> {
            log.info("starting");

            commandProcessor.help(null);
            commandProcessor.registeredCommands(null);

            Scanner scanner = new Scanner(System.in);

            while (runLoop.get()) {
                // Prompt the user
                System.out.print("Command > ");

                // Read user input as String
                String[] commandArgs = scanner.nextLine().strip().split(" ");
                if (commandArgs.length > 0) {
                    switch (commandArgs[0]) {
                        case "eventSources": {
                            commandProcessor.eventQueues(null);
                            break;
                        }
                        case "commands": {
                            commandProcessor.registeredCommands(null);
                            break;
                        }
                        case "?":
                        case "help": {
                            commandProcessor.help(null);
                            break;
                        }
                        default: {
                            commandProcessor.anyControlMethod(Arrays.asList(commandArgs));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void stop() {
        log.info("stop");
        runLoop.set(false);
    }

    @Override
    public void tearDown() {
        log.info("stop");
        runLoop.set(false);
        executorService.close();
    }

    @Override
    public void registerCommand(String name, Consumer<List<String>> command) {
        if (EventFlowManager.currentProcessor() == null) {
            registeredCommandMap.put(name, new AdminCommand(command));
        } else {
            String queueKey = "adminCommand." + name;
            addCommand(
                    name,
                    queueKey,
                    new AdminCommand(command, eventFlowManager.registerEventSource(queueKey, this)));
        }
    }

    @Override
    public void registerCommand(String name, AdminFunction command) {
        if (EventFlowManager.currentProcessor() == null) {
            registeredCommandMap.put(name, new AdminCommand(command));
        } else {
            String queueKey = "adminCommand." + name;
            addCommand(
                    name,
                    queueKey,
                    new AdminCommand(command, eventFlowManager.registerEventSource(queueKey, this)));
        }
    }

    private void addCommand(String name, String queueKey, AdminCommand adminCommand) {
        StaticEventProcessor staticEventProcessor = EventFlowManager.currentProcessor();
        log.info("registered command:" + name + " queue:" + queueKey + " processor:" + staticEventProcessor);

        registeredCommandMap.put(name, adminCommand);

        EventSubscriptionKey<?> subscriptionKey = new EventSubscriptionKey<>(
                new EventSourceKey<>(queueKey),
                AdminCallbackType.class,
                queueKey
        );

        staticEventProcessor.getSubscriptionManager().subscribe(subscriptionKey);
    }

    @Override
    public void subscribe(EventSubscriptionKey<AdminCommand> eventSourceKey) {
    }

    @Override
    public void unSubscribe(EventSubscriptionKey<AdminCommand> eventSourceKey) {
    }

    @Override
    public void setEventToQueuePublisher(EventToQueuePublisher<AdminCommand> targetQueue) {
    }

    private class CommandProcessor {

        private static final String help = "default commands:\n" +
                                           "---------------------------\n" +
                                           "quit         - exit the console\n" +
                                           "help/?       - this message\n" +
                                           "name         - service name\n" +
                                           "commands     - registered service commands\n" +
                                           "eventSources - list event sources\n";

        public boolean controlMethod(Signal<List<String>> publishSignal) {
            System.out.println("service name: " + serviceName);
            return false;
        }

        public boolean quit(Signal<List<String>> publishSignal) {
            log.info("quit");
            runLoop.set(false);
            return false;
        }

        public boolean help(Signal<List<String>> publishSignal) {
            System.out.println(help);
            return false;
        }

        public boolean eventQueues(Signal<List<String>> publishSignal) {
            eventFlowManager.appendQueueInformation(System.out);
            return false;
        }

        public boolean registeredCommands(Signal<List<String>> publishSignal) {
            String commandsString = registeredCommandMap.keySet().stream()
                    .sorted()
                    .collect(Collectors.joining(
                            "\n",
                            "Service commands:\n---------------------------\n",
                            "\n"));
            System.out.println(commandsString);
            return false;
        }

        public boolean anyControlMethod(List<String> publishSignal) {
            String command = publishSignal.getFirst();
            if (registeredCommandMap.containsKey(command)) {
                registeredCommandMap.get(command).publishCommand(publishSignal);
            } else {
                System.out.println("unknown command: " + command);
                System.out.println(help);
            }
            return false;
        }
    }
}

