package com.fluxtion.compiler.generation.forkjoin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForkJoinExperimentTest {//} extends CompiledOnlySepTest {

//    public ForkJoinExperimentTest(CompiledAndInterpretedSepTest.SepTestConfig compile) {
//        super(compile);
//    }
//
//    //    @Test
//    public void forkGraph() {
//        writeSourceFile = true;
////        addAuditor();
//        sep(c -> {
//
//            InputHandler inputHandler = new InputHandler("dopey_or_sleepy");
//            c.addNode(new CompletionTrigger(new LongRunningTrigger[]{
//                    new LongRunningTrigger(inputHandler, "dopey"),
//                    new LongRunningTrigger(inputHandler, "sleepy"),
//                    new LongRunningTrigger("doc")
//            }
//            ));
//            c.addEventAudit(LogLevel.DEBUG);
//        });
//        sep.setAuditLogLevel(LogLevel.DEBUG);
//        onEvent("doc");
//        getField("collector", CompletionTrigger.class).reset();
//        onEvent("all");
//        getField("collector", CompletionTrigger.class).reset();
//        onEvent("ignore");
//        getField("collector", CompletionTrigger.class).reset();
////        sep.setAuditLogLevel(LogLevel.DEBUG);
//        onEvent("dopey_or_sleepy");
//        getField("collector", CompletionTrigger.class).reset();
//        onEvent("all");
//    }
//
//
//    @Slf4j
//    public static class InputHandler {
//
//        String in;
//        private final String ignoreString;
//
//        public InputHandler(@AssignToField("ignoreString") String ignoreString) {
//            this.ignoreString = ignoreString;
//        }
//
//
//        @OnEventHandler
//        public boolean stringHandler(String in) {
//            this.in = in;
//            log.debug("event:{}", in);
//            return !in.equals(ignoreString) && !in.equals("ignore");
//        }
//    }
//
//    @Slf4j
//    public static class LongRunningTrigger extends EventLogNode {
//        private final InputHandler inputHandler;
//        private final String name;
//        private String error;
//        private boolean taskComplete = false;
//        private boolean afterTriggerComplete = false;
//        private boolean afterEventComplete = false;
//
//        public LongRunningTrigger(@AssignToField("inputHandler") InputHandler inputHandler, String name) {
//            this.inputHandler = inputHandler;
//            this.name = name;
//        }
//
//        public LongRunningTrigger(String name) {
//            this(new InputHandler(name), name);
//        }
//
//        @SneakyThrows
//        @OnTrigger(parallelExecution = true)
//        public boolean startLongTask() {
//            if (inputHandler.in.equals(name)) {
//                auditLog.info("NoStart", name);
//                return false;
//            }
////            if (afterTriggerComplete || afterEventComplete) {
////                throw new RuntimeException("afterTrigger and afterEvent should not have completed");
////            }
//            long millis = (long) (new Random().nextDouble() * 1200);
//            log.debug("{} start sleep:{}", name, millis);
//            auditLog.info("starting", name)
//                    .info("startTime", System.currentTimeMillis())
//                    .info("sleep", millis);
//            Thread.sleep(millis);
//            log.debug("{} completed", name);
//            auditLog.info("finish", name)
//                    .info("finishTime", System.currentTimeMillis());
//            taskComplete = true;
//            return true;
//        }
//
//        @AfterTrigger
//        public void afterTrigger() {
////            if (!taskComplete || afterEventComplete) {
////                throw new RuntimeException("startLongTask should be complete and afterEvent should not have completed");
////            }
//            log.debug("{} afterTrigger", name);
//            afterTriggerComplete = true;
//        }
//
//        @AfterEvent
//        public void afterEvent() {
////            if (!taskComplete || !afterTriggerComplete) {
////                throw new RuntimeException("startLongTask and afterEvent should be completed");
////            }
//            log.debug("{} afterEvent", name);
//            afterTriggerComplete = true;
//            taskComplete = false;
//            afterTriggerComplete = false;
//        }
//    }
//
//    @Data
//    @Slf4j
//    public static class CompletionTrigger extends EventLogNode implements NamedNode {
//        private boolean taskComplete = false;
//        private boolean afterTriggerComplete = false;
//        private boolean parentUpdateComplete = false;
//        private Set<String> updateSetSet = new HashSet<>();
//
//        private final LongRunningTrigger[] tasks;
//
//        @OnParentUpdate
//        public void taskUpdated(LongRunningTrigger task) {
//            auditLog.info("finished", task.name);
//            if (afterTriggerComplete || taskComplete) {
//                throw new RuntimeException("afterTrigger and collectSlowResults should not have completed");
//            }
//            log.debug("parentCallBack:{}", task.name);
//            updateSetSet.add(task.name);
//            parentUpdateComplete = updateSetSet.size() == tasks.length;
//        }
//
//        @OnTrigger(parallelExecution = true)
//        public boolean collectSlowResults() {
////            if (afterTriggerComplete || !parentUpdateComplete) {
////                throw new RuntimeException("afterTrigger and afterEvent should not have completed");
////            }
//            log.info("Collecting results");
////            auditLog.info()
//            taskComplete = true;
//            return true;
//        }
//
//        @AfterTrigger
//        public void afterTrigger() {
////            if (!taskComplete || !parentUpdateComplete) {
////                throw new RuntimeException("collectSlowResults should be complete and afterEvent should not have completed");
////            }
//            log.debug("afterTrigger");
//            afterTriggerComplete = true;
//        }
//
//        @Override
//        public String getName() {
//            return "collector";
//        }
//
//        public void reset() {
//            taskComplete = false;
//            afterTriggerComplete = false;
//            parentUpdateComplete = false;
//            updateSetSet.clear();
//        }
//    }
}
