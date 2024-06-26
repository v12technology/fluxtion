---
title: Parallel processing
parent: Examples
has_children: false
nav_order: 2
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/parallel
---

# Parallel processing example
---

Fluxtion supports parallel processing of trigger operations to reduce total response times for long running operations.
A divide and conquer approach for parallel processing is supported:

1. Add the attribute parallelExecution = true to an OnTrigger annotated method
2. Gather the nodes with parallel trigger methods into a child node
3. Fluxtion will use the standard ForkJoin pool to execute the trigger tasks in parallel
4. The gathering node will not be notified until all parent operations have completed
5. The total response time will be reduced if the parallel tasks take a sufficiently long time

## Code example

[See the example here]({{page.example_src}}/Main.java)

The example main runs synchronous and asynchronous tests, executing the application produces a set of results for
both parallel and synchronously execution, displayed as an ascii bar graph.


A [TaskCollector]({{page.example_src}}/TaskCollector.java) collects the results from a list of 
parent [SimulatedTasks]({{page.example_src}}/SimulatedTask.java) nodes, that either run in parallel or synchronously.
The tasks are triggered by a [RequestHandler]({{page.example_src}}/RequestHandler.java) that handles String events.
Calling eventProcessor.onEvent("") will cause the processor to trigger node execution starting with the ResponseHandler.

### Marking parallel OnTrigger methods

The OnTrigger annotation controls the parallel execution of the trigger task:
1. Asynchronous OnTrigger(parallelExecution = true)
2. Synchronous OnTrigger()

[SimulatedTasks]({{page.example_src}}/SimulatedTask.java)

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
The [Main class]({{page.example_src}}/Main.java)  builds the graphs for both cases and executes the processors, 
collecting and printing results. Relevant excerpts of the main method show how two processors are built, one with 
synchronous tasks and the other with asynchronous.

{% highlight java %}
public class Main {

    //code removed for clarity

    public static void main(String[] args) throws NoSuchFieldException {
        //code removed for clarity
        runTest(new AotParallelProcessor(), "\nAOT Parallel trigger test");
        runTest(new AotSynchronousProcessor(), "\nAOT Synchronous trigger test");
    }

    public static void buildParallelProcessor(EventProcessorConfig config) {
        RequestHandler requestHandler = new RequestHandler();
        config.addNode(
                TaskCollector.builder()
                        .task(new Asynchronous("async1", 250, requestHandler))
                        .task(new Asynchronous("async2", 225, requestHandler))
                        .task(new Asynchronous("async3", 18, requestHandler))
                        .task(new Asynchronous("async4", 185, requestHandler))
                        .requestHandler(requestHandler)
                        .build());
    }

    public static void buildSynchronousProcessor(EventProcessorConfig config) {
        RequestHandler requestHandler = new RequestHandler();
        config.addNode(
                TaskCollector.builder()
                        .task(new Synchronous("sync1", 250, requestHandler))
                        .task(new Synchronous("sync2", 225, requestHandler))
                        .task(new Synchronous("sync3", 18, requestHandler))
                        .task(new Synchronous("sync4", 185, requestHandler))
                        .requestHandler(requestHandler)
                        .build());
    }

    @SneakyThrows
    private static void runTest(EventProcessor<?> eventProcessor, String title) {
        //code removed for clarity
        eventProcessor.init();
        eventProcessor.onEvent("test");
        System.out.println(taskCollector.getResults());
    }
}
{% endhighlight %}

The execution graph image shows a set of tasks are triggered from the RequestHandler instance and their outputs are 
gathered in the TaskCollector instance.

![](../../images/parallel/parallel.png)

## Running the example 

The ascii bar chart clearly shows the parallel task test completes in 257ms vs 690ms for the synchronous version, using
the ForkJoin thread pool to execute task in parallel. The synchronous version executes all tasks on the main thread 
serially executing each task

- Y-axis - (task name) (thread name)
- X-Axis - elapsed time


{% highlight console %}
AOT Parallel trigger test TOTAL EXECUTION TIME : 255ms
====================================================================================================
Task thread vs relative time
----------------------------------------------------------------------------------------------------
async1          main |  *************************
async2   FJ-worker-1 |  **********************
async3   FJ-worker-2 |  **
async4   FJ-worker-3 |  ******************
----------------------------------------------------------------------------------------------------
Time milliseconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700


AOT Synchronous trigger test TOTAL EXECUTION TIME : 689ms
====================================================================================================
Task thread vs relative time
----------------------------------------------------------------------------------------------------
sync1           main |  *************************
sync2           main |                           ***********************
sync3           main |                                                  **
sync4           main |                                                    ******************
----------------------------------------------------------------------------------------------------
Time milliseconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700

{% endhighlight %}

## Running the example in Debug

To see detailed task processing set the debug log flag
{% highlight java %}
public static final boolean DEBUG_LOG = false;
{% endhighlight %}

This will prodice the debug output with thread execution id's in the log messages for the SimulatedTasks

{% highlight console %}
AOT Parallel trigger test
====================================================================================================
12:07:29.635 [main] DEBUG RequestHandler - request received:test
12:07:29.638 [main] DEBUG SimulatedTask - async1: start
12:07:29.639 [ForkJoinPool.commonPool-worker-1] DEBUG SimulatedTask - async2: start
12:07:29.639 [ForkJoinPool.commonPool-worker-2] DEBUG SimulatedTask - async3: start
12:07:29.639 [ForkJoinPool.commonPool-worker-3] DEBUG SimulatedTask - async4: start
12:07:29.659 [ForkJoinPool.commonPool-worker-2] DEBUG SimulatedTask - async3: complete - 20ms
12:07:29.827 [ForkJoinPool.commonPool-worker-3] DEBUG SimulatedTask - async4: complete - 188ms
12:07:29.867 [ForkJoinPool.commonPool-worker-1] DEBUG SimulatedTask - async2: complete - 228ms
12:07:29.889 [main] DEBUG SimulatedTask - async1: complete - 251ms
12:07:29.889 [main] DEBUG TaskCollector - collectingResults

TOTAL EXECUTION TIME : 252ms
Task thread vs relative time
----------------------------------------------------------------------------------------------------
async1          main |  *************************
async2   FJ-worker-1 |  **********************
async3   FJ-worker-2 |  **
async4   FJ-worker-3 |  ******************
----------------------------------------------------------------------------------------------------
Time milliseconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700


AOT Synchronous trigger test
====================================================================================================
12:07:29.915 [main] DEBUG RequestHandler - request received:test
12:07:29.915 [main] DEBUG SimulatedTask - sync1: start
12:07:30.169 [main] DEBUG SimulatedTask - sync1: complete - 254ms
12:07:30.169 [main] DEBUG SimulatedTask - sync2: start
12:07:30.397 [main] DEBUG SimulatedTask - sync2: complete - 228ms
12:07:30.397 [main] DEBUG SimulatedTask - sync3: start
12:07:30.418 [main] DEBUG SimulatedTask - sync3: complete - 21ms
12:07:30.419 [main] DEBUG SimulatedTask - sync4: start
12:07:30.608 [main] DEBUG SimulatedTask - sync4: complete - 189ms
12:07:30.608 [main] DEBUG TaskCollector - collectingResults

TOTAL EXECUTION TIME : 693ms
Task thread vs relative time
----------------------------------------------------------------------------------------------------
sync1           main |  *************************
sync2           main |                           **********************
sync3           main |                                                  **
sync4           main |                                                    ******************
----------------------------------------------------------------------------------------------------
Time milliseconds      0   50   100  150  200  250  300  350  400  450  500  550  600  650  700


{% endhighlight %}
