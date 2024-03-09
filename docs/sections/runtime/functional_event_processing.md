---
title: Functional event processing
parent: Runtime execution
has_children: false
nav_order: 2
published: true
---

# Function event stream processing
{: .no_toc }

This section documents the runtime event processing callback api and behaviour using functional programming.

The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind functions
into the processor without having to construct classes. Functional building is accessed through the
[DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java)
builder methods. User code reads the event streams calling onEvent with each new event received, the event processor 
then notifies functions according to the [dispatch rules](../core-technology#event-dispatch-rules).

The source project for the examples can be found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/execution)

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

## Event processing requirements
{: .no_toc }
To process an event stream correctly the following requirements must be met:

-  **Call EventProcessor.init() before first use**
-  **EventProcessors are not thread safe** a single event should be processed at one time.

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