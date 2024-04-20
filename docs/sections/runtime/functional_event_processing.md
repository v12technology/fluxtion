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
functions into the processor without having to construct classes marked with Fluxtion annotations. The goal of using the
functional DSL is to have no Fluxtion api calls in the business logic only pure vanilla java. 

**Advantages of using Fluxtion functional DSL**

- Business logic components are re-usable and testable outside Fluxtion
- Clear separation between event notification and business logic, event logic is removed from business code
- Complex functions library like windowing and aggregation are well tested and natively supported
- Increased developer productivity, less code to write and support
- New functionality is simple and cheap to integrate, Fluxtion pays the cost of rewiring the event flow
- No vendor lock-in, business code is free from any Fluxtion library dependencies

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - **Bind functions using functional programming with Fluxtion DSL**<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

In this section we are covering the first of these **Bind functions using functional programming with Fluxtion DSL**.

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>


# API overview
Fluxtion offers a DSL to bind functions into the event processor using the familiar map/filter/peek similar to the java
stream api. Bound functions are invoked in accordance to the dispatch rules [dispatch rules](../core-technology#event-dispatch-rules). 

## Creating a flow
In order to bind a functional operation we need to create a head of flow that the event processor will dispatch to when onEvent is called
by user code. In the imperative approach an entry point is registered by [annotating a method](processing_events#handle-event-input) 
with `@OnEventHandler`. The [DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) class provides builder methods 
for creating the head of a flow. There is no restriction to the number of flows bound inside an event processor.

Creat a flow for String events with a call to DataFlow.subscribe

{% highlight java %}
DataFlow.subscribe(String.class)
{% endhighlight %}

Once a flow has been created map/filter/grouping functions can be applied as chained calls.

## All functions are nodes 
A bound function is wrapped in a node/monad that is bound into the event processor and invokes the user function when 
notified. Each wrapping node can be the head of multiple child flows forming complex graph structures that obey the dispatch
rules. This is in contrast to classic java streams that have a terminal operation and a pipeline structure. 

An event processor is a live structure where new events trigger a set of dispatch operations. The node wrapping the function
supports both stateful and stateless functions, it is the user choice what type of function to bind. 

This example creates a simple graph structure where multiple stateful/stateless functions are bound to a single parent function.

{% highlight java %}

var stringFlow = DataFlow.subscribe(String.class);

stringFlow.map(String::toLowerCase);
stringFlow.map(String::toUpperCase);
stringFlow.map(MyFunctions::charCount);
stringFlow.map(new MyFunctions()::totalCharCount);

public static class MyFunctions{
    int charCount;

    public static int charCount(String in) {
        return in.length();
    }

    public int totalCharCount(String in) {
        charCount += in.length();
        return charCount;
    }
}
{% endhighlight %}

## Map
A map operation takes the input from a parent function and then applies a function to the input. If the return of the 
output is null then the event notification no longer propagates down that path. 

  {% highlight java %}
  var stringFlow = DataFlow.subscribe(String.class);

  stringFlow.map(String::toLowerCase);
  stringFlow.map(String::toUpperCase);
  stringFlow.map(MyFunctions::charCount);
  stringFlow.map(new MyFunctions()::totalCharCount);
  stringFlow.mapToInt(new MyFunctions()::totalCharCount);
  stringFlow.mapToInt(s -> s.length()/2);
  {% endhighlight %}
  
**Map supports**

- Stateless functions
- Stateful functions
- Primitive specialisation
- Method references
- Inline lambdas

## Filter
A filter predicate can be applied to a node to control event propagation, true continues the propagation and false swallows
the notification. If the predicate returns true then the input to the predicate is passed to the next operation in the 
event processor.

{% highlight java %}

DataFlow.subscribe(String.class)
        .filter(Objects::nonNull)
        .mapToInt(s -> s.length()/2);

{% endhighlight %}

**Filter supports**

- Stateless functions
- Stateful functions
- Primitive specialisation
- Method references
- Inline lambdas

## Functional operations
The functional DSL supports a rich set of operations. Where appropriate functional operations support:

- Stateless functions
- Stateful functions
- Primitive specialisation
- Method references
- Inline lambdas

### Map with bi function
Takes two flow inputs and applies a bi function to the inputs. Applied once both functions have updated.

### Peek
View the state of a node, invoked when the parent triggers.

### Sink
Publishes the output of the function to a named sink end point. Client code can register as a named sink end point with
the running event processor.

### Id
A node can be given an id that makes it discoverable using EventProcessor.getNodeById.

### Aggregate
Aggregates the output of a node using a user supplied stateful function.

### Aggregate with sliding window
Aggregates the output of a node using a user supplied stateful function, in a sliding window.

### Aggregate with tumbling window
Aggregates the output of a node using a user supplied stateful function, in a tumbling window.

### Default value
Set the initial value of a node without needing an input event to create a value.

### Flat map
Flat map operations on a collection from a parent node.

### Group by
Group by operations.

### Group by with sliding window
Group by operations, in a sliding window.

### Group by with tumbling window
Group by operations, in a tumbling window.

### Lookup
Apply a lookup function to a value as a map operation.

### Merge
Merge multiple streams of the same type into a single output.

### Map and merge
Merge multiple streams of different types into a single output, applying a mapping operation to combine the different types

### Console
Specialisation of peek that logs to console

### Push
Pushes the output of a node to user class, joins functional to imperative flow

### Trigger overrides
External flows can override that standard triggering method to force publication/calculation/downstream notifications.

### Reentrant events
The output of an operation can be published to the event processor as a new event. Will be processed after the current
cycle finishes.

# Examples
The source project for the examples can be
found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/execution)

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