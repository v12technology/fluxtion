---
title: Functional programming
parent: Event handling
has_children: false
nav_order: 2
published: true
---

# Functional event stream processing

{: .no_toc }

This section documents the runtime event processing callback api and behaviour using functional programming.

The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind
functions into the processor without having to construct classes. Functional building is accessed through the
[DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java)
builder methods to create a flow. Once a flow has been created map/filter/grouping functions can be applied
as chained calls. 

The source project for the examples can be
found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/execution)

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

- **Call EventProcessor.init() before first use**
- **EventProcessors are not thread safe** a single event should be processed at one time.

The flow must be built within the [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) build
methods. Calls to DataFlow map/filer/etc. will add any required instances to the model automatically.

## Bind functions to events

To bind functions to a flow of events a flow must be created with a subscription method in DataFlow.

`DataFlow.subscribe([event class])`

A lambda or a method reference can be bound as the next item in the function flow. 

{% highlight java %}
public static String toUpper(String incoming){
    return incoming.toUpperCase();
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
            .console("input: '{}'")
            .map(FunctionalStatic::toUpper)
            .console("transformed: '{}'");
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
input: 'hello world'
transformed: 'HELLO WORLD'
{% endhighlight %}

## Bind instance functions

Instance functions can be bound into the event processor using method references

{% highlight java %}
public static class PrefixString{
    private final String prefix;

    public PrefixString(String prefix) {
        this.prefix = prefix;
    }

    public String addPrefix(String input){
        return prefix + input;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
            .console("input: '{}'")
            .map(new PrefixString("XXXX")::addPrefix)
            .console("transformed: '{}'");
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
input: 'hello world'
transformed: 'XXXXhello world'
{% endhighlight %}

## Combining imperative and functional binding

Both imperative and functional binding can be used in the same build consumer. All the user classes and functions will
be added to the model for generation.

{% highlight java %}
public static String toUpper(String incoming){
    return incoming.toUpperCase();
}

public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("IMPERATIVE received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
   var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
            .console("FUNCTIONAL input: '{}'")
            .map(CombineFunctionalAndImperative::toUpper)
            .console("FUNCTIONAL transformed: '{}'");

        cfg.addNode(new MyNode());
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
FUNCTIONAL input: 'hello world'
FUNCTIONAL transformed: 'HELLO WORLD'
IMPERATIVE received:hello world
{% endhighlight %}

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
    });

    processor.init();
    processor.publishSignal("myIntSignal", 256);
}
{% endhighlight %}

Output
{% highlight console %}
republish re-entrant [intValue:256]
received [intValue:256]
{% endhighlight %}