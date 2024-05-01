---
title: Application integration
parent: Integrate event processor
has_children: false
nav_order: 1
published: true
example_project: https://github.com/v12technology/fluxtion-examples/tree/main/getting-started/developer-workflow
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/reference-examples/integration/src/main/java/com/fluxtion/example/reference/integration/replay
---


# Application integration of an event processor
{: .no_toc }

Once the event processor has been generated it can be used by the application. An instance of an
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, application integration connects the event processor to the application 
event sources. 

An application can contain multiple event processor instances, they are lightweight objects designed to be 
embedded in your application. The application creates instances of event processors and routes events to an instance.

The source project for the examples can be found [here]({{site.reference_examples}}/integration/src/main/java/com/fluxtion/example/reference/integration)

{: .warning }
**EventProcessors are not thread safe** a single event should be processed at one time. Application code is responsible 
for synchronizing thread access to an event processor instance. 
{: .fs-4 }

User code interacts with an event processor instance in one of five ways
1. Creating a new processor
2. Input to a processor
2. Output from a processor
3. Control of a processor
4. Query into a processor


![](../images/integration_overview-running.drawio.png)

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Example project

The source project for the examples can be found [here]({{site.reference_examples}}/integration/src/main/java/com/fluxtion/example/reference/integration)

# Creating a new processor
Fluxtion provides several strategies for creating an event processor instance that you will use in your application:
* Generate in process
* Use an AOT processor as POJO
* Factory method on an event processor instance
* Instance per partitioned event stream 

Once you have created an instance you can use it as any normal java class. For information about generating an event processor 
see [build event processor section](build-event-processor) for further details

## Create an instance
Creating a new processor in process by calling one of the Fluxtion methods` Fluxtion.interpret` or `Fluxtion.compile`, 
or by using an AOT generated processor. An AOT processor is a normal java class that has a constructor, use it as a POJO
in your application.

### Code sample
{: .no_toc }

{% highlight java %}

public class VanillaCreateEventProcessor {
    public static void main(String[] args) {
        //IN PROCESS CREATION
        var processor = Fluxtion.interpret(new VanillaAotBuilder()::buildGraph);
        processor.init();
        processor.onEvent("hello world - in process");

        //AOT USE AS POJO
        processor = new VanillaProcessor();
        processor.init();
        processor.onEvent("hello world - AOT");
    }

    //THE BUILDER IS CALLED AS PART OF THE MAVEN BUILD TO GENERATE AOT PROCESSOR
    public static class VanillaAotBuilder implements FluxtionGraphBuilder{
        @Override
        public void buildGraph(EventProcessorConfig eventProcessorConfig) {
            DataFlow.subscribe(String.class)
                    .console("received -> {}");
        }

        @Override
        public void configureGeneration(FluxtionCompilerConfig compilerConfig) {
            compilerConfig.setClassName("VanillaProcessor");
            compilerConfig.setPackageName("com.fluxtion.example.reference.integration.genoutput");
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
received -> hello world - in process
received -> hello world - AOT
{% endhighlight %}

## Factory method
An event processor instance provides a factory method that creates new instances of the same event processor class. This
can be useful when multiple instances of the same processor are required but each with different state. Applications 
sometimes partition an event streams, each instance of the event processor can be bound to that partitioned stream.

### Code sample
{: .no_toc }

{% highlight java %}

public class FactoryExample {
    public static void main(String[] args) {
        var processor = Fluxtion.compile(new MyPartitionedLogic());
    
        var processor_A = processor.newInstance();
        var processor_B = processor.newInstance();
    
        //Factory method to create new event processor instances
        processor_A.init();
        processor_B.init();
    
        //user partitioning event flow logic
        processor_A.onEvent("for A");
        processor_B.onEvent("for B");
    }
    
    public static class MyPartitionedLogic {
        @OnEventHandler
        public boolean onString(String signal) {
            System.out.println(signal);
            return true;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
for A
for B
{% endhighlight %}

### Compiled event processor only support
{: .no_toc }
The compiled event processors create bound nodes using their constructors so each compiled event processor has no shared 
references. As an interpreted instance refers to the nodes it has been generated with, it does create new instance of bound nodes,
it cannot create a segregated instance of an event processor. 

{: .warning }
Factory method processor.newInstance() is only supported for compiled event processors
{: .fs-4 }
 
## Partitioning
Fluxtion provides an automatic [partitioning function]({{site.fluxtion_src_runtime}}/partition/Partitioner.java) 
that can be used to query event flow, partition it and assign a new event processor instance to that flow. The partitioner
takes a factory method to create new event processor instances and a key function to segregate event flow.

The partitioner will create and initialise a new event processor when the key function returns a previously unseen key. 

### Code sample
{: .no_toc }

{% highlight java %}

public class AutomaticPartitionExample {
    public record DayEvent(String day, int amount) {}

    public static void main(String[] args) {
        var processorFactory = Fluxtion.compile(new DayEventProcessor());

        //Create a partitioner that will partition data based on a property
        Partitioner<StaticEventProcessor> partitioner = new Partitioner<>(processorFactory::newInstance);
        partitioner.partition(DayEvent::day);

        //new processor for Monday
        partitioner.onEvent(new DayEvent("Monday", 2));
        partitioner.onEvent(new DayEvent("Monday", 4));

        //new processor for Tuesday
        partitioner.onEvent(new DayEvent("Tuesday", 14));

        //new processor for Friday
        partitioner.onEvent(new DayEvent("Friday", 33));

        //re-use processor for Monday
        System.out.println();
        partitioner.onEvent(new DayEvent("Monday", 999));
    }

    public static class DayEventProcessor {
        @Initialise
        public void initialise() {
            System.out.println("\nDayEventProcessor::initialise");
        }

        @OnEventHandler
        public boolean onDayaEvent(DayEvent signal) {
            System.out.println(signal);
            return true;
        }
    }
}


{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
DayEventProcessor::initialise
DayEvent[day=Monday, amount=2]
DayEvent[day=Monday, amount=4]

DayEventProcessor::initialise
DayEvent[day=Tuesday, amount=14]

DayEventProcessor::initialise
DayEvent[day=Friday, amount=33]

DayEvent[day=Monday, amount=999]
{% endhighlight %}

# Inputs to a processor
An event processor responds to input by triggering a calculation cycle, triggering a calculation cycle from an input 
is covered in this section. Functions are bound to the calculation cycle to meet the business requirements, see the
[mark event handling section](mark-event-handling) for further details. To process inputs the event processor instance must be 
initialised before any input is submitted.

{: .info }
1 - Call EventProcessor.init() before submitting any input<br/>
2 - Each new input triggers a graph calculation cycle
{: .fs-4 }

## Events
A simple example demonstrating how an application triggers an event process cycle by calling

`processor.onEvent("WORLD");`


### Code sample
{: .no_toc }
{% highlight java %}

public class EventInput {
    public static void main(String[] args) {
        EventProcessor<?> processor = Fluxtion.interpret(c -> DataFlow.subscribe(String.class).console("Hello {}"));
        //lifecycle init required
        processor.init();

        //send event
        processor.onEvent("WORLD");
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
Hello WORLD
{% endhighlight %}

## Exported service
The generated event processor implements any exported service interface, calling a service method will trigger an
event process cycle. The service reference is discovered in the application with a call to the event processor instance, 

`ServiceController svc = processor.getExportedService(ServiceController.class);`

An event processor provides several service discovery methods:
* Lookup using type inference -  `ServiceController svc = processor.getExportedService()`
* Lookup using a specific type - `var svc = processor.getExportedService(ServiceController.class)`
* Consuming a service if it is exported - `processor.consumeServiceIfExported(ServiceController.class, s -> {})`
* Checking if a service is exported - `processor.exportsService(MiaServiceController.class))`

### Code sample
{: .no_toc }
{% highlight java %}

public class ServiceInput {

    public static void main(String[] args) {
        EventProcessor<?> processor = Fluxtion.interpret(new MyConsumer());

        //lifecycle init required
        processor.init();

        //lookup service using type inference
        ServiceController svc = processor.getExportedService();
        svc.serviceOn(System.out::println, "WORLD");

        //SUPPORTED  service lookups

        //lookup with explicit type
        svc = processor.getExportedService(ServiceController.class);
        svc.serviceOn(System.out::println, "WORLD");

        //lookup with explicit type, use default value if none exported
        MiaServiceController svcMissing = processor.getExportedService(MiaServiceController.class, (consumer, message) -> {
            System.out.println("MiaServiceController not exported");
            return false;
        });
        svcMissing.serviceOn(System.out::println, "WORLD");

        //lookup and consume service if exported
        processor.consumeServiceIfExported(ServiceController.class, s -> s.serviceOn(System.out::println, "WORLD"));

        //is service exported
        System.out.println("ServiceController.class exported   : " + processor.exportsService(ServiceController.class));
        System.out.println("MiaServiceController.class exported: " + processor.exportsService(MiaServiceController.class));
    }

    public interface ServiceController {
        boolean serviceOn(Consumer<String> consumer, String message);
    }

    public interface MiaServiceController {
        boolean serviceOn(Consumer<String> consumer, String message);
    }

    public static class MyConsumer implements @ExportService ServiceController {
        @Override
        public boolean serviceOn(Consumer<String> consumer, String message) {
            consumer.accept("hello " + message);
            return false;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
hello WORLD
hello WORLD
MiaServiceController not exported
hello WORLD
ServiceController.class exported   : true
MiaServiceController.class exported: false
{% endhighlight %}

## Signals
Fluxtion provides some prebuilt signal classes that are re-usable and reduce the application code to write. The Signal 
classes can be sent as events triggering an event process cycle using the Api calls built into the event processor:

* publish a signal with a filter and value
* publish a signal with only a filter
* publish a signal with only a value
* publish primitive signals with a filter

### Code sample
{: .no_toc }
{% highlight java %}

public class SignalInput {

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new SignalInput());
        processor.init();

        processor.publishIntSignal("1", 200);
        processor.publishSignal("ALERT_SIGNAL", "alert!!");
        processor.publishSignal("WAKEUP");
        processor.publishObjectSignal("WAKEUP");
        processor.publishObjectSignal(new Date());
    }

    @OnEventHandler(filterString = "1")
    public boolean intSignal(Signal.IntSignal value) {
        System.out.println("intSignal [" + value.getValue() + "]");
        return true;
    }

    @OnEventHandler(filterString = "ALERT_SIGNAL")
    public boolean alertSignal(Signal<String> signalToProcess) {
        System.out.println("alertStringSignal [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler(filterStringFromClass = String.class)
    public boolean anyStringSignal(Signal<String> signalToProcess) {
        System.out.println("anyStringSignal [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler(filterStringFromClass = Date.class)
    public boolean anyDateSignal(Signal<Date> signalToProcess) {
        System.out.println("anyDateSignal [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler(filterString = "WAKEUP")
    public boolean namedSignal(Signal<?> signalToProcess) {
        System.out.println("namedSignal [" + signalToProcess.filterString() + "]");
        return true;
    }
}

{% endhighlight %}
### Sample log
{: .no_toc }

{% highlight console %}
intSignal [200]
alertStringSignal [Signal: {filterString: ALERT_SIGNAL, value: alert!!}]
namedSignal [WAKEUP]
anyStringSignal [Signal: {filterString: java.lang.String, value: WAKEUP}]
anyDateSignal [Signal: {filterString: java.util.Date, value: Sat Apr 27 06:25:28 BST 2024}]
{% endhighlight %}

## Buffer and trigger

An event processor can buffer multiple events without causing any triggers to fire, and at some point in the future 
cause all potentially dirty trigger to fire. This is known as buffering and triggering it is achieved by call 
`EventProcessr.bufferEvent` multiple times and then following it with a call `EventProcessor.triggerCalculation`

### Code sample
{: .no_toc }

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode(), new MyNode2()));
    processor.init();
    processor.bufferEvent("test");
    System.out.println();
    processor.bufferEvent(200);
    System.out.println();
    processor.bufferEvent(50);
    System.out.println();
    processor.triggerCalculation();
}

public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode event received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleIntEvent(int intToProcess) {
        boolean propagate = intToProcess > 100;
        System.out.println("MyNode2 conditional propagate:" + propagate);
        return propagate;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode2 event received:" + stringToProcess);
        return true;
    }
}

public static class Child{
    private final MyNode myNode;
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
    }

    @OnParentUpdate
    public void node1Updated(MyNode myNode1){
        System.out.println("1 - myNode updated");
    }

    @OnParentUpdate
    public void node2Updated(MyNode2 myNode2){
        System.out.println("2 - myNode2 updated");
    }

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
MyNode2 event received:test
2 - myNode2 updated
MyNode event received:test
1 - myNode updated

MyNode2 conditional propagate:true
2 - myNode2 updated

MyNode2 conditional propagate:false

Child:triggered
{% endhighlight %}


# Outputs from a processor

## Sink

An application can register for output from the EventProcessor by supplying a consumer to addSink and removed with a 
call to removeSink. Bound classes can publish to sinks during an event process cycle, any registered sinks will see 
the update as soon as the data is published, not at the end of the cycle. 

* Adding sink - `processor.addSink("mySink", (Consumer<T> t) ->{})`
* Removing sink - `processor.removeSink("mySink")`

### Code sample
{: .no_toc }

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg ->
            DataFlow.subscribeToIntSignal("myIntSignal")
                    .mapToObj(d -> "intValue:" + d)
                    .sink("mySink")//CREATE A SINK IN THE PROCESSOR
    );
    processor.init();

    //ADDING A SINK
    processor.addSink("mySink", (Consumer<String>) System.out::println);

    processor.publishSignal("myIntSignal", 10);
    processor.publishSignal("myIntSignal", 256);

    //REMOVING A SINK
    processor.removeSink("mySink");
    processor.publishSignal("myIntSignal", 512);
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
intValue:10
intValue:256
{% endhighlight %}

## Audit logging 
Fluxtion provides an audit logging facility that captures the output when an event processing cycle is triggered.
An event processor must be generated with audit logging enabled, nodes can optionally write key/value tuples. Audit
output includes:

* The triggering event
* Time of the event
* The order in which nodes are invoked
* A map structure that each node can write a key/value pair to

The control of the output is covered in the control section, by default the audit log writes output using the standard
java util logging framework.

### Code sample
{: .no_toc }
{% highlight java %}

public class AuditExample {
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(c ->{
           c.addNode(new MyAuditingNode());
           c.addEventAudit();
        });
        processor.init();
        //AUDIT IS INFO BY DEFAULT
        processor.onEvent("detailed message 1");
    }

    public static class MyAuditingNode extends EventLogNode {
        @Initialise
        public void init(){
            auditLog.info("MyAuditingNode", "init");
            auditLog.info("MyAuditingNode_debug", "some debug message");
        }

        @OnEventHandler
        public boolean stringEvent(String event) {
            auditLog.info("event", event);
            auditLog.debug("charCount", event.length());
            return true;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

Audit records are encoded as a stream of yaml documents by default.  

{% highlight console %}

eventLogRecord: 
    eventTime: 1714197503584
    logTime: 1714197503584
    groupingId: null
    event: LifecycleEvent
    eventToString: Init
    nodeLogs: 
        - myAuditingNode_0: { MyAuditingNode: init, MyAuditingNode_debug: some debug message}
    endTime: 1714197503584
---
Apr 27, 2024 6:58:23 AM com.fluxtion.runtime.audit.EventLogManager calculationLogConfig
INFO: updating event log config:EventLogConfig{level=DEBUG, logRecordProcessor=null, sourceId=null, groupId=null}
eventLogRecord: 
    eventTime: 1714197503597
    logTime: 1714197503598
    groupingId: null
    event: String
    eventToString: detailed message 1
    nodeLogs: 
        - myAuditingNode_0: { event: detailed message 1, charCount: 18}
    endTime: 1714197503598
---

{% endhighlight %}

# Control of a processor

## Lifecycle

User nodes that are added to the processing graph can attach to the lifecycle callbacks by annotating methods with 
the relevant annotations. The event processor implements the Lifecycle interface, application code calls a lifecyle method
to trigger a lifecycle process cycle.

{: .warning }
**EventProcessor.init()** must always be called before any events are submitted to an event processor instance
{: .fs-4 }

### Code sample
{: .no_toc }

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.start();
    processor.stop();
    processor.tearDown();
}

public static class MyNode {
    @Initialise
    public void myInitMethod() {
        System.out.println("Initialise");
    }

    @Start
    public void myStartMethod() {
        System.out.println("Start");
    }

    @Stop
    public void myStopMethod() {
        System.out.println("Stop");
    }

    @TearDown
    public void myTearDownMethod() {
        System.out.println("TearDown");
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
Initialise
Start
Stop
TearDown
{% endhighlight %}


## Clocks and time
User classes bound into an event processor have access to a [Clock]({{site.fluxtion_src_runtime}}/time/Clock.java) to 
query the current time in the processor. The Clock instance is driven by a strategy the supplies a long to the clock. 
A [ClockStrategy]({{site.fluxtion_src_runtime}}/time/ClockStrategy.java) can be changed at runtime by the application. 
This is particularly useful during replay mode, or when writing unit tests that have time dependent variables and the 
time needs to be data driven.

Set the clock strategy with

`processor.setClockStrategy(ClockStrategy customClockStrategy);`

The clock has no units it is a long the application can use in any way it wants.

### Code sample
{: .no_toc }
{% highlight java %}

public class ClockExample {
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new TimeLogger());
        processor.init();
        //PRINT CURRENT TIME
        processor.onEvent(DateFormat.getDateTimeInstance());

        //USE A SYNTHETIC STRATEGY TO SET TIME FOR THE PROCESSOR CLOCK
        LongAdder syntheticTime = new LongAdder();
        processor.setClockStrategy(syntheticTime::longValue);

        //SET A NEW TIME - GOING BACK IN TIME!!
        syntheticTime.add(1_000_000_000);
        processor.onEvent(DateFormat.getDateTimeInstance());

        //SET A NEW TIME - BACK TO THE FUTURE
        syntheticTime.add(1_800_000_000_000L);
        processor.onEvent(DateFormat.getDateTimeInstance());
    }

    public static class TimeLogger {
        public Clock wallClock = Clock.DEFAULT_CLOCK;

        @OnEventHandler
        public boolean publishTime(DateFormat dateFormat) {
            System.out.println("time " + dateFormat.format(new Date(wallClock.getWallClockTime())));
            return true;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
time 27 Apr 2024, 09:30:31
time 12 Jan 1970, 14:46:40
time 15 Jan 2027, 08:00:02
{% endhighlight %}

## Batch support

Batch callbacks are supported through the BatchHandler interface that the generated EventHandler implements. Any methods 
that are annotated with, `@OnBatchPause` or `@OnBatchEnd` will receive calls from the matching BatchHandler method. 

### Code sample
{: .no_toc }

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();

    processor.onEvent("test");

    //use BatchHandler service
    BatchHandler batchHandler = (BatchHandler)processor;
    batchHandler.batchPause();
    batchHandler.batchEnd();
}

public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode event received:" + stringToProcess);
        return true;
    }

    @OnBatchPause
    public void batchPause(){
        System.out.println("MyNode::batchPause");
    }

    @OnBatchEnd
    public void batchEnd(){
        System.out.println("MyNode::batchEnd");
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
MyNode event received:test
MyNode::batchPause
MyNode::batchEnd
{% endhighlight %}

## Audit log control
The audit log facility can be controlled at runtime by the application. [LogRecord]({{site.fluxtion_src_runtime}}/audit/LogRecord.java) 
are the output of the event auditor, the application can customise LogRecord handling in three ways:

* Change the log level - info is the default level
* Log record encoding - yaml is the default encoding
* Log record processor - java.util.logging is the default LogRecord processor

### Code sample
{: .no_toc }
{% highlight java %}

public class AuditControlExample {
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(c ->{
           c.addNode(new MyAuditingNode());
           c.addEventAudit();
        });
        processor.init();

        //AUDIT IS INFO BY DEFAULT
        processor.onEvent("detailed message 1");

        //CHANGE LOG LEVEL DYNAMICALLY
        processor.setAuditLogLevel(EventLogControlEvent.LogLevel.DEBUG);
        processor.onEvent("detailed message 2");

        //REPLACE LOGRECORD ENCODER
        processor.setAuditLogRecordEncoder(new MyLogEncoder(Clock.DEFAULT_CLOCK));

        //REPLACE LOGRECORD PROCESSOR
        processor.setAuditLogProcessor(logRecord -> {
            System.err.println("WARNING -> "+ logRecord.toString());
        });

        processor.onEvent("detailed message 1");
        processor.onEvent("detailed message 2");
    }

    public static class MyLogEncoder extends LogRecord{
        public MyLogEncoder(Clock clock) {
            super(clock);
        }

        @Override
        public CharSequence asCharSequence(){
            return "IGNORING ALL RECORDS!!";
        }
    }

    public static class MyAuditingNode extends EventLogNode {
        @Initialise
        public void init(){
            auditLog.info("MyAuditingNode", "init");
            auditLog.info("MyAuditingNode_debug", "some debug message");
        }

        @OnEventHandler
        public boolean stringEvent(String event) {
            auditLog.info("event", event);
            auditLog.debug("charCount", event.length());
            return true;
        }
    }
}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
eventLogRecord: 
    eventTime: 1714205423167
    logTime: 1714205423167
    groupingId: null
    event: LifecycleEvent
    eventToString: Init
    nodeLogs: 
        - myAuditingNode_0: { MyAuditingNode: init, MyAuditingNode_debug: some debug message}
    endTime: 1714205423170
---
eventLogRecord: 
    eventTime: 1714205423170
    logTime: 1714205423170
    groupingId: null
    event: String
    eventToString: detailed message 1
    nodeLogs: 
        - myAuditingNode_0: { event: detailed message 1}
    endTime: 1714205423172
---
Apr 27, 2024 9:10:23 AM com.fluxtion.runtime.audit.EventLogManager calculationLogConfig
INFO: updating event log config:EventLogConfig{level=DEBUG, logRecordProcessor=null, sourceId=null, groupId=null}
eventLogRecord: 
    eventTime: 1714205423186
    logTime: 1714205423186
    groupingId: null
    event: String
    eventToString: detailed message 2
    nodeLogs: 
        - myAuditingNode_0: { event: detailed message 2, charCount: 18}
    endTime: 1714205423186
---
WARNING -> IGNORING ALL RECORDS!!
WARNING -> IGNORING ALL RECORDS!!
{% endhighlight %}


## Setting context parameters
An application can dynamically inject key/value properties into a running event processor. The context parameters are
available for any bound node to look up using an injected EventProcessorContext. Setting context parameters does not
trigger an event processing cycle. 

Context parameters api:

* Single parameter - `EventProcessor.addContextParameter(Object key, Object value)`
* Overwrite all parameters - `EventProcessor.setContextParameterMap(Map<Object, Object> newContextMapping)`

### Code sample
{: .no_toc }
{% highlight java %}

public class ContextParamInput {
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new ContextParamReader());
        processor.init();

        processor.addContextParameter("myContextParam1", "[param1: update 1]");
        processor.start();

        processor.addContextParameter("myContextParam1", "[param1: update 2]");
        processor.addContextParameter("myContextParam2", "[param2: update 1]");
        processor.start();
    }

    public static class ContextParamReader {
        @Inject
        public EventProcessorContext context;

        @Start
        public void start() {
            System.out.println("myContextParam1 -> " + context.getContextProperty("myContextParam1"));
            System.out.println("myContextParam2 -> " + context.getContextProperty("myContextParam2"));
            System.out.println();
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
myContextParam1 -> [param1: update 1]
myContextParam2 -> null

myContextParam1 -> [param1: update 2]
myContextParam2 -> [param2: update 1]
{% endhighlight %}

## Runtime inject
Instances can be injected at runtime to a node using the `@Inject(instanceName = "startData")` annotation on a
[InstanceSupplier]({{site.fluxtion_src_runtime}}/node/InstanceSupplier.java)  data member. The instance has to be injected at runtime to a built event processor before calling init with:

`processor.injectNamedInstance(new Date(1000000), "startData")`

Instances can be updated once the processor is running by injecting a new instance with the same name.

### Code sample
{: .no_toc }
{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.injectNamedInstance(new Date(1000000), "startData");

    processor.init();
    processor.onEvent("TEST");

    processor.injectNamedInstance(new Date(999000000), "startData");
    processor.onEvent("TEST");
}

public static class MyNode{
    @Inject(instanceName = "startData")
    public InstanceSupplier<Date> myDate;

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("runtime injected:" + myDate.get());
        return true;
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
runtime injected:Thu Jan 01 01:16:40 GMT 1970
runtime injected:Mon Jan 12 14:30:00 GMT 1970
{% endhighlight %}

# Query into a processor

## User node lookup by id
[NamedNodes]({{site.fluxtion_src_runtime}}/node/NamedNode.java) are available for lookup from an event processor instance 
using their name. The application can then use the reference to pull data from the node without requiring an event 
process cycle to push data to an output.

### Code sample
{: .no_toc }
{% highlight java %}

public class GetNodeByIdExample {
    public static void main(String[] args) throws NoSuchFieldException {
        var processor = Fluxtion.interpret(new MondayChecker());
        processor.init();

        processor.onEvent("Monday");
        processor.onEvent("Tuesday");
        processor.onEvent("Wednesday");

        //LOOKUP USER NODE
        MondayChecker mondayChecker = processor.getNodeById("MondayChecker");

        //PULL DATA
        System.out.println("PULLING Monday count:" + mondayChecker.getMondayCount());

        processor.onEvent("Monday");
        //PULL DATA
        System.out.println("PULLING Monday count:" + mondayChecker.getMondayCount());
    }

    public static class MondayChecker implements NamedNode {
        private int mondayCounter = 0;

        @OnEventHandler
        public boolean checkIsMonday(String day){
            boolean isMonday = day.equalsIgnoreCase("monday");
            mondayCounter += isMonday ? 1 : 0;
            return isMonday;
        }

        public int getMondayCount() {
            return mondayCounter;
        }

        @Override
        public String getName() {
            return "MondayChecker";
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
PULLING Monday count:1
PULLING Monday count:2
{% endhighlight %}

## DataFlow node lookup by id
DataFlow nodes are available for lookup from an event processor instance using their name. In this case the lookup 
returns a reference to the wrapped value and not the wrapping node. The application can then use the reference to 
pull data from the node without requiring an event process cycle to push data to an output.

When building the graph with DSL a call to `id` makes that element addressable for lookup.

### Code sample
{: .no_toc }
{% highlight java %}

public class GetFlowNodeByIdExample {
    public static void main(String[] args) throws NoSuchFieldException {
        var processor = Fluxtion.interpret(c ->{
            DataFlow.subscribe(String.class)
                    .filter(s -> s.equalsIgnoreCase("monday"))
                    //ID START - this makes the wrapped value accessible via the id
                    .mapToInt(Mappers.count()).id("MondayChecker")
                    //ID END
                    .console("Monday is triggered");
        });
        processor.init();

        processor.onEvent("Monday");
        processor.onEvent("Tuesday");
        processor.onEvent("Wednesday");

        //ACCESS THE WRAPPED VALUE BY ITS ID
        Integer mondayCheckerCount = processor.getStreamed("MondayChecker");
        System.out.println("Monday count:" + mondayCheckerCount + "\n");

        //ACCESS THE WRAPPED VALUE BY ITS ID
        processor.onEvent("Monday");
        mondayCheckerCount = processor.getStreamed("MondayChecker");
        System.out.println("Monday count:" + mondayCheckerCount);
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
Monday is triggered
Monday count:1

Monday is triggered
Monday count:2
{% endhighlight %}

## Auditor lookup by id
[Auditors]({{site.fluxtion_src_runtime}}/audit/Auditor.java) are available for lookup from an event processor instance
using their name. The application can then use the reference to pull data from the Auditor without requiring an event
process cycle to push data to an output.

### Code sample
{: .no_toc }

{% highlight java %}

public class AuditLookupExample {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        var processor = Fluxtion.interpret(c -> {
            //ADDING A NAMED AUDITOR
            c.addAuditor(new MyAuditor(), "myAuditor");
            DataFlow.subscribe(String.class);
        });
        processor.init();
        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");

        //LOOKUP AUDITOR BY NAME
        MyAuditor myAuditor = processor.getAuditorById("myAuditor");
        //PULL DATA FROM AUDITOR
        System.out.println("\nPULL MyAuditor::invocationCount " + myAuditor.getInvocationCount());
    }

    public static class MyAuditor implements Auditor {
        private int invocationCount = 0;

        @Override
        public void nodeRegistered(Object node, String nodeName) {
        }

        @Override
        public void eventReceived(Object event) {
            System.out.println("MyAuditor::eventReceived " + event);
            invocationCount++;
        }

        public int getInvocationCount() {
            return invocationCount;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
MyAuditor::eventReceived Init
MyAuditor::eventReceived A
MyAuditor::eventReceived B
MyAuditor::eventReceived C

PULL MyAuditor::invocationCount 4
{% endhighlight %}
