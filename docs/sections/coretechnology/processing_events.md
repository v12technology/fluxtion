---
title: Processing events
parent: Core technology
has_children: false
nav_order: 1
published: true
---

# Processing event streams
{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

An instance of an 
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, user code connects
the EventProcessor to the application event sources. An application can contain multiple EventProcessors instances, and
routes events to an instance. 

- **EventProcessors are not thread safe** a single event should be processed at one time. 
- **Call EventProcessor#init before first use** 
- **Each new event processed triggers a graph calculation cycle.**

An EventProcessor provides interface methods for the user code to post events for processing. The event processing
api is documented below.

## Event input 
Sends an incoming even to the EventProcessor to trigger a new stream calculation. Any method annotated with 
'@OnEvent' receives the event from the event processor

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
received:TEST
{% endhighlight %}

## Lifecycle callbacks
Fluxtion allows user classes to register for lifecycle callbacks when bound to an event processor. Lifecycle callbacks 
are exposed by the generated event processor and are automatically routed to the client classes.

### Lifecycle - init
{: .no_toc }
`EventProcessor#init` Calls init on any node in the graph that has registered for an init callback. The init calls
are invoked in topological order.

### Lifecycle - teardown
{: .no_toc }
`EventProcessor#tearDown` Calls tearDown on any node in the graph that has registered for an tearDown callback.
The tearDown calls are invoked reverse topological order.

### Lifecycle - start
{: .no_toc }
`EventProcessor#start` Calls start on any node in the graph that has registered for an onStart callback. The start calls
are invoked in topological order. Start must be called after init

### Lifecycle - stop
{: .no_toc }
`EventProcessor#stop` Calls stop on any node in the graph that has registered for an onStop callback.
The stop calls are invoked reverse topological order.

### Attaching a user node to lifecycle callback
User nodes that are added to the processing graph can attach to the lifecycle callbacks

{% highlight java %}
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

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.start();
    processor.stop();
    processor.tearDown();
}
{% endhighlight %}

Output
{% highlight console %}
Initialise
Start
Stop
TearDown
{% endhighlight %}

## Filtering events
User events can implement [Event]({{site.fluxtion_src_runtime}}/event/Event.java), which provides an optional filtering 
field. Event handlers can specify the filter value, so they only see events with matching filters


{% highlight java %}
public static class MyNode {
    @OnEventHandler(filterString = "CLEAR_SIGNAL")
    public boolean allClear(Signal<String> signalToProcess) {
        System.out.println("allClear [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler(filterString = "ALERT_SIGNAL")
    public boolean alertSignal(Signal<String> signalToProcess) {
        System.out.println("alertSignal [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler()
    public boolean anySignal(Signal<String> signalToProcess) {
        System.out.println("anySignal [" + signalToProcess + "]");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.onEvent(new Signal<>("ALERT_SIGNAL", "power failure"));
    System.out.println();
    processor.onEvent(new Signal<>("CLEAR_SIGNAL", "power restored"));
    System.out.println();
    processor.onEvent(new Signal<>("HEARTBEAT_SIGNAL", "heartbeat message"));
}
{% endhighlight %}

Output
{% highlight console %}
alertSignal [Signal: {filterString: ALERT_SIGNAL, value: power failure}]
anySignal [Signal: {filterString: ALERT_SIGNAL, value: power failure}]

allClear [Signal: {filterString: CLEAR_SIGNAL, value: power restored}]
anySignal [Signal: {filterString: CLEAR_SIGNAL, value: power restored}]

anySignal [Signal: {filterString: HEARTBEAT_SIGNAL, value: heartbeat message}]
{% endhighlight %}

## Triggering children
Event notification is propagated to child instances of event handlers. The notification is sent to any method that is
annotated with an `OnTrigger` annotation. Trigger propagation is kin topological order.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleStringEvent(int intToProcess) {
        System.out.println("received:" + intToProcess);
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

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode(), new MyNode2()));
    processor.init();
    processor.onEvent("test");
    System.out.println();   
    processor.onEvent(200);
}
{% endhighlight %}

Output
{% highlight console %}
received:test
triggered

received:200
triggered
{% endhighlight %}

## Conditional triggering children
Event notification is propagated to child instances of event handlers, if the event handler method returns a true value. 
A false value will


{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleStringEvent(int intToProcess) {
        boolean propagate = intToProcess > 100;
        System.out.println("conditional propagate:" + propagate);
        return propagate;
    }
}

public static class Child{
    private final MyNode myNode;
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
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
    processor.onEvent("test");
    System.out.println();   
    processor.onEvent(200);
    System.out.println();   
    processor.onEvent(50);
}
{% endhighlight %}

Output
{% highlight console %}
received:test
Child:triggered

conditional propagate:true
Child:triggered

conditional propagate:false
{% endhighlight %}

## Functional support
The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind functions
into the processor without having to construct classes. Functional building is accessed through the 
[DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) 
builder methods.

## Re-entrant events
Events can be added for processing from inside the graph for processing in the next available cycle. Internal events
are added to LIFO queue for processing in the correct order. The EventProcessor instance maintains the LIFO queue, any 
new input events are queued if there is processing currently acting. Support for internal event publishing is built 
into the streaming api.

Maps an int signal to a String and republishes to the graph
{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received [" + stringToProcess +"]");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
                DataFlow.subscribeToIntSignal("myIntSignal")
                        .mapToObj(d -> "intValue:" + d)
                        .console("republish re-entrant [{}]")
                        .processAsNewGraphEvent();
                cfg.addNode(new MyNode());
            }
    );
    processor.init();
    processor.publishSignal("myIntSignal", 256);
}
{% endhighlight %}

Output
{% highlight console %}
republish re-entrant [intValue:256]
received [intValue:256]
{% endhighlight %}

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

