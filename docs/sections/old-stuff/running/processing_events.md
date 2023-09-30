---
title: Processing event streams
parent: Event stream processing
grand_parent: Old stuff
has_children: false
nav_order: 1
published: true
---

# Processing event streams

An instance of an 
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the binding point between event streams and processing logic, user code connects
the EventProcessor to the application event sources. An application can contain multiple EventProcessors instances, and
routes events to an instance. 

- **EventProcessors are not thread safe** a single event should be processed at one time. 
- **Call EventProcessor#init before first use** 
- **Each new event processed triggers a real-time calculation.**

## EventProcessor
User code interacts with an EventProcessor instance at runtime to process event streams. An EventProcessor provides 
interface methods for the user code to invoke depending on the usecase.

### Event input 
Sends an incoming even to the EventProcessor to trigger a new stream calculation

{% highlight java %}
EventProcessor processor = Fluxtion.interpret(Main::buildProcessingLogic);
processor.init();
processor.onEvent("test");
{% endhighlight %}

### Signal input
A utility method that sends signals to any registered listeners in the processor. 
A signal can contain optionally contain a value. A String filter on the signal routes the signal to a handler that has 
a matching filter.
{% highlight java %}
EventProcessor processor = Fluxtion.interpret(cfg -> EventFlow.subscribeToIntSignal("myIntSignal"));
processor.init();
processor.publishSignal("myIntSignal", 10);
{% endhighlight %}

### Re-entrant events
Events can be added for processing from inside the graph for processing in the next available cycle. Internal events
are added to LIFO queue for processing in the correct order. The EventProcessor instance maintains the LIFO queue, any 
new input events are queued if there is processing currently acting. Support for internal event publishing is built 
into the streaming api.

Maps an int signal to a String and republishes to the graph
{% highlight java %}
EventProcessor processor = Fluxtion.interpret(cfg -> EventFlow.subscribeToIntSignal("myIntSignal")
        .mapToObj(d -> "intValue:" + d)
        .processAsNewGraphEvent()
);
{% endhighlight %}

### Processing output
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

An application can remove sink using the call ```EventProcessor#removeSink```

### Lifecycle - init
```EventProcessor#init``` Calls init on any node in the graph that has registered for an init callback. The init calls 
are invoked in topological order.

### Lifecycle - teardown
```EventProcessor#tearDown``` Calls tearDown on any node in the graph that has registered for an tearDown callback. 
The tearDown calls are invoked reverse topological order.

### Attaching a user node to lifecycle callback
User nodes that are added to the processing graph can attach to the lifecycle callbacks

{% highlight java %}
public static class MyNode{
    @Initialise
    public void myInitMethod(){}
    
    @TearDown
    public void myTearDownMethod(){}
}
{% endhighlight %}