---
title: Library functions
parent: Mark event handling
grand_parent: Reference documentation
has_children: false
nav_order: 5
published: true
---

# Runtime library functions
{: .no_toc }
---

The runtime environment provides several library functions that bound classes can use. This section documents the runtime
environment and how to access the library functions.

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

## Processing output
An application can register for output from the EventProcessor by supplying a consumer
to addSink. Support for publishing to a sink is built into the streaming api, `[builder_type]#sink`.
A consumer has a string key to partition outputs.

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg ->
    DataFlow.subscribeToIntSignal("myIntSignal")
        .mapToObj(d -> "intValue:" + d)
        .sink("mySink"));
    processor.init();
    processor.addSink("mySink", (Consumer<String>) System.out::println);
    processor.publishSignal("myIntSignal", 10);
    processor.publishSignal("myIntSignal", 256);
}

{% endhighlight %}

Output
{% highlight console %}
intValue:10
intValue:256
{% endhighlight %}

An application can remove sink using the call `EventProcessor#removeSink`

## Clock time

### Code sample
{: .no_toc }

{% highlight java %}

public class ClockExample {
    public static class TimeLogger {
        public Clock wallClock = Clock.DEFAULT_CLOCK;

        @OnEventHandler
        public boolean publishTime(DateFormat dateFormat) {
            System.out.println("time " + dateFormat.format(new Date(wallClock.getWallClockTime())));
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(new TimeLogger());
        processor.init();
        //PRINT CURRENT TIME
        processor.onEvent(new SimpleDateFormat("HH:mm:ss.SSS"));

        //SLEEP AND PRINT TIME
        Thread.sleep(100);
        processor.onEvent(new SimpleDateFormat("HH:mm:ss.SSS"));
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
time 07:33:45.744
time 07:33:45.849
{% endhighlight %}

## TImed alarm trigger

### Code sample
{: .no_toc }

{% highlight java %}

public class FixedRateTriggerExample {
    public static class RegularTrigger {

        private final FixedRateTrigger fixedRateTrigger;

        public RegularTrigger(FixedRateTrigger fixedRateTrigger) {
            this.fixedRateTrigger = fixedRateTrigger;
        }

        public RegularTrigger(int sleepMilliseconds) {
            fixedRateTrigger = new FixedRateTrigger(sleepMilliseconds);
        }

        @OnTrigger
        public boolean trigger() {
            System.out.println("RegularTrigger::triggered");
            return true;
        }
    }

    public static void main(String... args) throws InterruptedException {
        var processor = Fluxtion.interpret(new RegularTrigger(100));
        processor.init();

        //NO TRIGGER - 10MS NEEDS TO ELAPSE
        processor.onEvent(new Object());
        processor.onEvent("xxx");

        //WILL TRIGGER - 10MS HAS ELAPSED
        Thread.sleep(100);
        processor.onEvent("xxx");
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
RegularTrigger::triggered
{% endhighlight %}

## Buffer and trigger calculation
An event processor can buffer multiple events without causing any triggers to fire, and at some point in the future 
cause all potentially dirty trigger to fire. This is known as buffering and triggering it is achieved by call 
`EventProcessr.bufferEvent` multiple times and then following it with a call `EventProcessor.triggerCalculation`

### Code sample
{: .no_toc }

{% highlight java %}
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

## Audit logging

### Code sample
{: .no_toc }

{% highlight java %}
public class AuditExample {
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

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(c ->{
           c.addNode(new MyAuditingNode());
           c.addEventAudit();
        });
        processor.init();
        //AUDIT IS INFO BY DEFAULT
        processor.onEvent("detailed message 1");
    }
}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
eventLogRecord: 
    eventTime: 1714287142943
    logTime: 1714287142943
    groupingId: null
    event: LifecycleEvent
    eventToString: Init
    nodeLogs: 
        - myAuditingNode_0: { MyAuditingNode: init, MyAuditingNode_debug: some debug message}
    endTime: 1714287142946
---
eventLogRecord: 
    eventTime: 1714287142946
    logTime: 1714287142946
    groupingId: null
    event: String
    eventToString: detailed message 1
    nodeLogs: 
        - myAuditingNode_0: { event: detailed message 1}
    endTime: 1714287142949
---
{% endhighlight %}

## EventProcessorContext - context parameters

### Code sample
{: .no_toc }

{% highlight java %}

public class ContextParamInput {
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

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new ContextParamReader());
        processor.init();

        processor.addContextParameter("myContextParam1", "[param1: update 1]");
        processor.start();

        processor.addContextParameter("myContextParam1", "[param1: update 2]");
        processor.addContextParameter("myContextParam2", "[param2: update 1]");
        processor.start();
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

## DirtyStateMonitor - node dirty flag control

### Code sample
{: .no_toc }

{% highlight java %}

public class DirtyStateMonitorExample {
    public static class TriggeredChild implements NamedNode {
        @Inject
        public DirtyStateMonitor dirtyStateMonitor;
        private final FlowSupplier<Integer> intDataFlow;

        public TriggeredChild(FlowSupplier<Integer> intDataFlow) {
            this.intDataFlow = intDataFlow;
        }

        @OnTrigger
        public boolean triggeredChild() {
            System.out.println("TriggeredChild -> " + intDataFlow.get());
            return true;
        }

        public void printDirtyStat() {
            System.out.println("\nintDataFlow dirtyState:" + dirtyStateMonitor.isDirty(intDataFlow));
        }

        public void markDirty() {
            dirtyStateMonitor.markDirty(intDataFlow);
            System.out.println("\nmark dirty intDataFlow dirtyState:" + dirtyStateMonitor.isDirty(intDataFlow));
        }

        @Override
        public String getName() {
            return "triggeredChild";
        }
    }

    public static void main(String[] args) throws NoSuchFieldException {
        var processor = Fluxtion.interpret(new TriggeredChild(DataFlow.subscribe(Integer.class).flowSupplier()));
        processor.init();
        TriggeredChild triggeredChild = processor.getNodeById("triggeredChild");

        processor.onEvent(2);
        processor.onEvent(4);

        //NOTHING HAPPENS
        triggeredChild.printDirtyStat();
        processor.triggerCalculation();

        //MARK DIRTY
        triggeredChild.markDirty();
        processor.triggerCalculation();
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }
{% highlight console %}
TriggeredChild -> 2
TriggeredChild -> 4

intDataFlow dirtyState:false

mark dirty intDataFlow dirtyState:true
TriggeredChild -> 4
{% endhighlight %}

## EventDispatcher - event re-dispatch
Events can be dispatched into the event processor as re-entrant events from a node during a calculation cycle. The
[EventDispatcher]({{site.fluxtion_src_runtime}}/callback/EventDispatcher.java) class gives access to event re-dispatch
functions. In order to access the EventDispatcher for the containing event processor we use the `@Inject` annotation.

{% highlight java %}
@Inject
public EventDispatcher eventDispatcher;
{% endhighlight %}

The event processor will inject the EventDispatcher instance at runtime.

**Any events that re-entrant will be queued and only execute when the current cycle has completed.**

In this example a String event handler method receives a csv like string and redispatches an int event for each element
in the record. An Integer event handler method handles each int event in a separate event cycle.

### Code sample
{: .no_toc }

{% highlight java %}
public class CallBackExample {
    public static class MyCallbackNode{
        @Inject
        public EventDispatcher eventDispatcher;

        @OnEventHandler
        public boolean processString(String event) {
            for (String item : event.split(",")) {
                eventDispatcher.processAsNewEventCycle(Integer.parseInt(item));
            }
            return false;
        }

        @OnEventHandler
        public boolean processInteger(Integer event) {
            System.out.println("received event: " + event);
            return false;
        }
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new MyCallbackNode());
        processor.init();

        processor.onEvent("20,45,89");
    }
}
{% endhighlight %}

### Sample log
{: .no_toc }
{% highlight console %}
received event: 20
received event: 45
received event: 89
{% endhighlight %}
