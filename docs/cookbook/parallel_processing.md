---
title: Parallel processing
parent: Cookbook
has_children: false
nav_order: 7
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/parallel
---

## Introduction

Fluxtion supports parallel processing of trigger operations to reduce total response times for long running operations.
A divide and conquer approach for parallel processing is supported:

1. Add the attribute parallelExecution = true to an OnTrigger annotation
2. Gather the nodes with parallel trigger methods into a gathering child class
3. Fluxtion will use the standard ForkJoin pool to execute the trigger tasks in parallel
4. The gathering node will not be notified until all parent operations have completed
5. The total response time will be reduced if the parallel tasks take a sufficiently long time

## Code example

[See the example here]({{page.example_src}}/Main.java)

The example runs a synchronous and asynchronous tests. The [TaskCollector]({{page.example_src}}/TaskCollector.java) 
collects the results from a list of parent [SimulatedTask]({{page.example_src}}/SimulatedTask.java), 
that either run in parallel or synchronously. Executing the main produces a set of results for
both parallel and synchronously execution, displayed as an ascii bar graph. 

### Marking parallel OnTrigger methods

The OnTrigger annotation controls the parallel execution of the trigger task:
1. Asynchronous OnTrigger(parallelExecution = true)
2. Synchronous OnTrigger()

{% highlight java %}
@Slf4j
public static class Asynchronous extends SimulatedTask{
    public Asynchronous(String name, int workDurationMillis, RequestHandler requestHandler) {
        super(name, workDurationMillis, requestHandler);
    }
    /**
     * The trigger method is annotated with parallelExecution, signaling this method should be run in parallel
     * if possible
     * @return notification of a change
     */
    @SneakyThrows
    @OnTrigger(parallelExecution = true)
    public boolean executeTask(){
        return _executeTask();
    }
}

@Slf4j
public static class Synchronous extends SimulatedTask{
    public Synchronous(String name, int workDurationMillis, RequestHandler requestHandler) {
        super(name, workDurationMillis, requestHandler);
    }
    /**
     * The trigger method should be run synchronously before or after sibling trigger methods
     * @return notification of a change
     */
    @SneakyThrows
    @OnTrigger
    public boolean executeTask(){
        return _executeTask();
    }
}
{% endhighlight %}

### Building the graphs and executing
The code below builds the graphs for both cases and executes the processors, collecting and printing results. 

{% highlight java %}
public class Main {

    public static void main(String[] args) throws NoSuchFieldException {
        //uncomment to see task execution log output
        //System.setProperty("org.slf4j.simpleLogger.log.com.fluxtion.example.cookbook.parallel", "DEBUG");
        var eventProcessor = Fluxtion.interpret(c -> {
            RequestHandler requestHandler = new RequestHandler();
            c.addNode(
                    TaskCollector.builder()
                            .task(new Asynchronous("async1", 250, requestHandler))
                            .task(new Asynchronous("async2", 225, requestHandler))
                            .task(new Asynchronous("async3", 18, requestHandler))
                            .task(new Asynchronous("async4", 185, requestHandler))
                            .requestHandler(requestHandler)
                            .build(), "taskCollector"
            );
        });
        runTest(eventProcessor, "Parallel trigger test");

        eventProcessor = Fluxtion.interpret(c -> {
            RequestHandler requestHandler = new RequestHandler();
            c.addNode(
                    TaskCollector.builder()
                            .task(new Synchronous("sync1", 250, requestHandler))
                            .task(new Synchronous("sync2", 225, requestHandler))
                            .task(new Synchronous("sync3", 18, requestHandler))
                            .task(new Synchronous("sync4", 185, requestHandler))
                            .requestHandler(requestHandler)
                            .build(), "taskCollector"
            );
        });
        runTest(eventProcessor, "\nSynchronous trigger test");
    }

    private static void runTest(EventProcessor<?> eventProcessor, String title) throws NoSuchFieldException {
        eventProcessor.init();
        TaskCollector taskCollector = eventProcessor.getNodeById("taskCollector");
        System.out.println(title);
        System.out.println("=".repeat(100));
        eventProcessor.onEvent("test");
        System.out.println("\nTOTAL EXECUTION TIME : " + taskCollector.getDuration() + "ms");
        System.out.println("-".repeat(100));
        System.out.println(taskCollector.getResults());
    }
}
{% endhighlight %}



## Running the example 

The ascii bar chart clearly shows the parallel task test completes in 257ms vs 690ms for the synchronous version, using
the ForkJoin thread pool to execute task in parallel. The synchronous version executes all tasks on the main thread 
serially executing each task

- Y-axis - (task name) (thread name)
- X-Axis - elapsed time


{% highlight console %}
Parallel trigger test
====================================================================================================

TOTAL EXECUTION TIME : 257ms
----------------------------------------------------------------------------------------------------
async1          main |  *************************
async2   FJ-worker-1 |  ***********************
async3   FJ-worker-2 |  *
async4   FJ-worker-3 |  *******************
----------------------------------------------------------------------------------------------------
Time milliesconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700


Synchronous trigger test
====================================================================================================

TOTAL EXECUTION TIME : 690ms
----------------------------------------------------------------------------------------------------
sync1           main |  *************************
sync2           main |                           ***********************
sync3           main |                                                  **
sync4           main |                                                    ******************
----------------------------------------------------------------------------------------------------
Time milliesconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700
{% endhighlight %}

## Running the example in Debug

To see detailed task processing, uncomment the debug control line at the top of the main method:
{% highlight java %}
System.setProperty("org.slf4j.simpleLogger.log.com.fluxtion.example.cookbook.parallel", "DEBUG");
{% endhighlight %}

This will prodice the debug output with thread execution id's in the log messages for the SimulatedTasks

{% highlight console %}
Parallel trigger test
====================================================================================================
08:54:21.193 [main] DEBUG RequestHandler - request received:test
08:54:21.196 [ForkJoinPool.commonPool-worker-2] DEBUG SimulatedTask - async2: start
08:54:21.196 [ForkJoinPool.commonPool-worker-1] DEBUG SimulatedTask - async1: start
08:54:21.197 [ForkJoinPool.commonPool-worker-3] DEBUG SimulatedTask - async3: start
08:54:21.197 [ForkJoinPool.commonPool-worker-4] DEBUG SimulatedTask - async4: start
08:54:21.219 [ForkJoinPool.commonPool-worker-3] DEBUG SimulatedTask - async3: complete - 22ms
08:54:21.387 [ForkJoinPool.commonPool-worker-4] DEBUG SimulatedTask - async4: complete - 190ms
08:54:21.424 [ForkJoinPool.commonPool-worker-2] DEBUG SimulatedTask - async2: complete - 228ms
08:54:21.451 [ForkJoinPool.commonPool-worker-1] DEBUG SimulatedTask - async1: complete - 255ms
08:54:21.451 [main] DEBUG TaskCollector - collectingResults

TOTAL EXECUTION TIME : 258ms
----------------------------------------------------------------------------------------------------
async1   FJ-worker-1 |  *************************
async2   FJ-worker-2 |  **********************
async3   FJ-worker-3 |  **
async4   FJ-worker-4 |  *******************
----------------------------------------------------------------------------------------------------
Time milliesconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700


Synchronous trigger test
====================================================================================================
08:54:21.484 [main] DEBUG RequestHandler - request received:test
08:54:21.484 [main] DEBUG SimulatedTask - sync1: start
08:54:21.739 [main] DEBUG SimulatedTask - sync1: complete - 255ms
08:54:21.739 [main] DEBUG SimulatedTask - sync2: start
08:54:21.969 [main] DEBUG SimulatedTask - sync2: complete - 230ms
08:54:21.970 [main] DEBUG SimulatedTask - sync3: start
08:54:21.992 [main] DEBUG SimulatedTask - sync3: complete - 22ms
08:54:21.993 [main] DEBUG SimulatedTask - sync4: start
08:54:22.183 [main] DEBUG SimulatedTask - sync4: complete - 190ms
08:54:22.183 [main] DEBUG TaskCollector - collectingResults

TOTAL EXECUTION TIME : 699ms
----------------------------------------------------------------------------------------------------
sync1           main |  *************************
sync2           main |                           ***********************
sync3           main |                                                  **
sync4           main |                                                    *******************
----------------------------------------------------------------------------------------------------
Time milliesconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700
{% endhighlight %}
