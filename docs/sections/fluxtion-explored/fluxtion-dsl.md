---
title: Fluxtion DSL deep dive
parent: Fluxtion explored
has_children: false
nav_order: 3
published: true
---

**To be completed**

# Fluxtion DSL Deep dive
{: .no_toc }

The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind
functions into the processor without having to construct classes marked with Fluxtion annotations. The goal of using the
functional DSL is to have no Fluxtion api calls in the business logic only pure vanilla java.

This section describes the Functional DSL in greater depth than the [Fluxtion DSL](../mark-event-handling/functional_event_processing)
exploring concepts like, aggregation, windowing and groupBy in detail.

**Advantages of using Fluxtion functional DSL**

- Business logic components are re-usable and testable outside Fluxtion
- Clear separation between event notification and business logic, event logic is removed from business code
- Complex functions library like windowing and aggregation are well tested and natively supported
- Increased developer productivity, less code to write and support
- New functionality is simple and cheap to integrate, Fluxtion pays the cost of rewiring the event flow
- No vendor lock-in, business code is free from any Fluxtion library dependencies

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
stream api. Bound functions are invoked in accordance to the [dispatch rules](../fluxtion-explored#event-dispatch-rules).

An event processor is a live structure where new events trigger a set of dispatch operations. The node wrapping a function
supports both stateful and stateless functions, it is the user choice what type of function to bind.

## DataFlow
To bind a functional operation we first create a [DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) 
in the event processor. A DataFlow triggers when the event processor starts a calculation cycle and there is a matching 
dispatch rule. In the imperative approach an event processor entry point is registered by [annotating a method](processing_events#handle-event-input) 
with `@OnEventHandler` or an interface exported with `@ExportService`.

The [DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) class provides builder methods to create and bind flows in an event processor. There is no restriction 
on the number of data flows bound inside an event processor.

To create a flow for String events, call  `DataFlow.subscribe(String.class)`, any call to processor.onEvent("myString") will be 
routed to this flow.

{% highlight java %}
DataFlow.subscribe(String.class)
{% endhighlight %}

Once a flow has been created map, filter, groupBy, etc. functions can be applied as chained calls.


## Map
A map operation takes the output from a parent node and then applies a function to it. If the return of the
function is null then the event notification no longer propagates down that path.

{% highlight java %}
var stringFlow = DataFlow.subscribe(String.class);

stringFlow.map(String::toLowerCase);
stringFlow.mapToInt(s -> s.length()/2);
{% endhighlight %}

**Map supports**

- Stateless functions
- Stateful functions
- Primitive specialisation
- Method references
- Inline lambdas - **interpreted mode only support, AOT mode will not serialise the inline lambda**

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
- Inline lambdas - **interpreted mode only support, AOT mode will not serialise the inline lambda**

## Reduce
There is no reduce function required in Fluxtion, stateful map functions perform the role of reduce. In a classic batch
environment the reduce operation combines the element of collection of items into a single value. In a streaming environment
the set of values is never complete, we can view the current value of a stateful map operation which is equivalent to the
reduce operation. The question is rather, when is the value of the stateful map published and reset.

## Automatic wrapping of functions
Fluxtion automatically wraps the function in a node, actually a monad, and binds both into the event processor. The wrapping node
handles all the event notifications, invoking the user function when it is triggered. Each wrapping node can be the
head of multiple child flows forming complex graph structures that obey the dispatch rules. This is in contrast to
classic java streams that have a terminal operation and a pipeline structure.

This example creates a simple graph structure, multiple stateful/stateless functions are bound to a single parent DataFlow.

{% highlight java %}
//STATEFUL FUNCTIONS
MyFunctions myFunctions = new MyFunctions();
SimpleMath simpleMath = new SimpleMath();

//BUILD THE GRAPH WITH DSL
var stringFlow = DataFlow.subscribe(String.class).console("\ninput: '{}'");

var charCount = stringFlow.map(myFunctions::totalCharCount)
    .console("charCount: {}");

var upperCharCount = stringFlow.map(myFunctions::totalUpperCaseCharCount)
    .console("upperCharCount: {}");

DataFlow.mapBiFunction(simpleMath::updatePercentage, upperCharCount, charCount)
    .console("percentage chars upperCase all words:{}");

//STATELESS FUNCTION
DataFlow.mapBiFunction(MyFunctions::wordUpperCasePercentage, upperCharCount, charCount)
    .console("percentage chars upperCase this word:{}");
{% endhighlight %}

Running the above with a strings **'test ME', 'and AGAIN'** outputs

{% highlight console %}
input: 'test ME'
charCount: 7
upperCharCount: 2
percentage chars upperCase all words:0.2857142857142857
percentage chars upperCase this word:0.2857142857142857

input: 'and AGAIN'
charCount: 16
upperCharCount: 7
percentage chars upperCase all words:0.391304347826087
percentage chars upperCase this word:0.4375
{% endhighlight %}

### Processing graph
{: .no_toc }
Fluxtion DSL only requires the developer to write functions, any wrapping nodes are automatically added to the event processor.
The compiler automatically selects stateful or stateless map functions, binding user instances if a stateful map 
function is specified.

```mermaid
flowchart TB

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA><b>InputEvent</b>::String]:::eventHandler 

    HandlerA[<b>Subscriber</b>::String\nid - stringFlow]:::graphNode
    
    MapData1[<b>Map - stateful</b>\nid - charCount\nmyFunctions::totalCharCount]:::graphNode 
    MapData2[<b>Map - stateful</b>\nid - upperCharCount\nmyFunctions::totalUpperCaseCharCount]:::graphNode
    
    BiMapSum[<b>BiMap - stateful</b>\nsimpleMath::updatePercentage]:::graphNode 
    BiMapSum2[<b>BiMap - stateless</b>\nMyFunctions::wordUpperCasePercentage]:::graphNode
    
    EventA --> HandlerA

    
    subgraph EventProcessor
      myFunctions[<b>User object::MyFunctions</b>\nid - myFunctions]  --- MapData1 & MapData2
      simpleMath[<b>User object::SimpleMath</b>\nid - simpleMath] ----- BiMapSum
      HandlerA --> MapData1 & MapData2 ---> BiMapSum
      MapData1 & MapData2 ---> BiMapSum2
    end
    
```

## Node to DataFlow
A Dataflow can be created by subscribing to a node that has been imperatively added to the event processor. When the node 
triggers in a calculation cycle the DataFlow will be triggered. Create a DataFlow from a node with:

`DataFlow.subscribeToNode(new MyComplexNode())`

If the node referred to in the DataFlow.subscribeToNode method call is not in the event processor it will be bound
automatically.

The example below creates an instance of MyComplexNode as the head of a DataFlow. When a String event is received the
DataFlow path is executed. In this case we are aggregating into a list that has the four most recent elements


{% highlight java %}
public class SubscribeToNodeSample {
    @Getter
    @ToString
    public static class MyComplexNode {
        private String in;

        @OnEventHandler
        public boolean stringUpdate(String in) {
            this.in = in;
            return true;
        }
    }
    
    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribeToNode(new MyComplexNode())
                .console("node triggered -> {}")
                .map(MyComplexNode::getIn)
                .aggregate(Collectors.listFactory(4))
                .console("last 4 elements:{}\n");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(SubscribeToNodeSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");
        processor.onEvent("E");
        processor.onEvent("F");
    }
}
{% endhighlight %}

Running the example code above logs to console
{% highlight console %}
node triggered -> SubscribeToNodeSample.MyComplexNode(in=A)
last 4 elements:[A]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=B)
last 4 elements:[A, B]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=C)
last 4 elements:[A, B, C]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=D)
last 4 elements:[A, B, C, D]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=E)
last 4 elements:[B, C, D, E]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=F)
last 4 elements:[C, D, E, F]
{% endhighlight %}

# Aggregating
# Windowing
# GroupBy