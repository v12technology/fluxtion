---
title: Processing events
parent: Core technology
has_children: false
nav_order: 1
published: true
---

# Processing event streams

An instance of an 
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, user code connects
the EventProcessor to the application event sources. An application can contain multiple EventProcessors instances, and
routes events to an instance. 

- **EventProcessors are not thread safe** a single event should be processed at one time. 
- **Call EventProcessor#init before first use** 
- **Each new event processed triggers a graph calculation cycle.**

# EventProcessor
User code interacts with an EventProcessor instance at runtime to process event streams. An EventProcessor provides 
interface methods for the user code to invoke depending on the usecase.

## Event input 
Sends an incoming even to the EventProcessor to trigger a new stream calculation. Any method annotated with 
'@OnEvent' receives the event from the event processor

{% highlight java %}
EventProcessor processor = Fluxtion.interpret(new MyNode());
processor.init();
processor.onEvent("test");

public static class MyNode {

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        return true;
    }
}
{% endhighlight %}

## Lifecycle callbacks
Fluxtion allows user classes to register for lifecycle callbacks when bound to an event processor. Lifecycle callbacks 
are exposed by the generated event processor and are automatically routed to the client classes.

### Lifecycle - init
`EventProcessor#init` Calls init on any node in the graph that has registered for an init callback. The init calls
are invoked in topological order.

### Lifecycle - teardown
`EventProcessor#tearDown` Calls tearDown on any node in the graph that has registered for an tearDown callback.
The tearDown calls are invoked reverse topological order.

### Lifecycle - start
`EventProcessor#start` Calls start on any node in the graph that has registered for an onStart callback. The start calls
are invoked in topological order. Start must be called after init

### Lifecycle - stop
`EventProcessor#stop` Calls stop on any node in the graph that has registered for an onStop callback.
The stop calls are invoked reverse topological order.

### Attaching a user node to lifecycle callback
User nodes that are added to the processing graph can attach to the lifecycle callbacks

{% highlight java %}
EventProcessor processor = Fluxtion.interpret(new MyNode());
processor.init();
processor.start();
processor.stop();
processor.tearDown();

public class MyNode{

    @Initialise
    public void myInitMethod(){}

    @TearDown
    public void myTearDownMethod(){}

    @Start
    public void myStartMethod(){}
    
    @Stop
    public void myStopMethod(){}
}
{% endhighlight %}

## Filtering events
User events can implement [Event]({{site.fluxtion_src_runtime}}/event/Event.java), which provides an optional filtering 
field. Event handlers can specify the filter value, so they only see events with matching filters


{% highlight java %}
EventProcessor processor = Fluxtion.interpret(new MyNode());
processor.init();
processor.onEvent(new Signal<>("DANGER_SIGNAL", "power failure"));

public static class MyNode {

    @OnEventHandler(filterString = "ALERT_SIGNAL")
    public boolean handleStringEvent(Signal<String> siganlToProcess) {
        return true;
    }
}
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
EventProcessor processor = Fluxtion.interpret(cfg -> {
            DataFlow.subscribeToIntSignal("myIntSignal")
                    .mapToObj(d -> "intValue:" + d)
                    .processAsNewGraphEvent();
            cfg.addNode(new MyNode());
        }
);
{% endhighlight %}

## Processing output
An application can register for output from the EventProcessor by supplying a consumer
to addSink. Support for publishing to a sink is built into the streaming api, ```EventStreamBuilder#sink```. 
A consumer has a string key to partition outputs.

{% highlight java %}
EventProcessor processor = Fluxtion.interpret(cfg -> EventFlow.subscribeToIntSignal("myIntSignal")
        .mapToObj(d -> "intValue:" + d)
        .sink("mySink")
);
processor.init();
processor.addSink("mySink", (Consumer<String>) System.out::println);
processor.publishSignal("myIntSignal", 10);
{% endhighlight %}

Output
{% highlight console %}
intValue:10
{% endhighlight %}

An application can remove sink using the call `EventProcessor#removeSink`

