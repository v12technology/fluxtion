---
title: Fluxtion DSL deep dive
parent: Fluxtion explored
has_children: false
nav_order: 3
published: true
---

# Fluxtion DSL Deep dive
{: .no_toc }

The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind
functions into the processor without having to construct classes marked with Fluxtion annotations. The goal of using the
functional DSL is to have no Fluxtion api calls in the business logic only vanilla java.

This section describes the Functional DSL in greater depth than the [Fluxtion DSL](../mark-event-handling/functional_event_processing)
exploring concepts like, aggregation, windowing and groupBy in detail.

**Advantages of using Fluxtion functional DSL**

- Business logic components are re-usable and testable outside Fluxtion
- Clear separation between event notification and business logic, event logic is removed from business code
- Complex library functions like windowing and aggregation are well tested and natively supported
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
environment the reduce operation combines a collection of items into a single value. In a streaming environment
the set of values is never complete, we can view the current value of a stateful map operation which is equivalent to the
reduce operation. The question is rather, when is the value of the stateful map published and reset.

## Automatic wrapping of functions
Fluxtion automatically wraps the function in a node, actually a monad, and binds both into the event processor. The wrapping node
handles all the event notifications, invoking the user function when it is triggered. Each wrapping node can be the
head of multiple child flows forming complex graph structures that obey the dispatch rules. This is in contrast to
classic java streams that have a terminal operation and a pipeline structure.

This example creates a simple graph structure, multiple stateful/stateless functions are bound to a single parent DataFlow.

We are using the `DataFlow.console` operation to print intermediate results to the screen for illustrative purposese. 
The console operation is a specialisation of `DataFlow.peek`.

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

MyFunctions class is a normal java class bound into the event processor.

{% highlight java %}
@Getter
public class MyFunctions {

    private long totalCharCount;
    private long upperCaseCharCount;

    public static long charCount(String s) {
        return s.length();
    }

    public static long upperCaseCharCount(String s) {
        return s.chars().filter(Character::isUpperCase).count();
    }

    public long totalCharCount(String s) {
        totalCharCount += charCount(s);
        return totalCharCount;
    }

    public long totalUpperCaseCharCount(String s) {
        upperCaseCharCount += upperCaseCharCount(s);
        return upperCaseCharCount;
    }

    public static double wordUpperCasePercentage(long longA, long longB) {
        return (double) longA /longB;
    }

    @Getter
    public static class SimpleMath {
        private double a;
        private double b;
        private double percentage;

        public double updatePercentage(long longA, long longB) {
            a += longA;
            b += longB;
            percentage = a / b;
            return percentage;
        }
    }
}
{% endhighlight %}

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

# Trigger control
Fluxtion offers a way to override the triggering of a flow node in the event processor. There are four trigger controls
available for client code to customise:

* **Flow.publishTrigger** - Notifies a child node when triggered, adds a notification to the normal publish
* **Flow.publishTriggerOverride** - Notifies a child node when triggered, removes all other publish notifications
* **Flow.updateTrigger** - Overrides when the flow node runs its functional operation
* **Flow.resetTrigger** - If the functional operation is stateful calls the reset function

In the trigger examples we are using the `DataFlow.subscribeToSignal` and `processor.publishSignal` to drive the trigger
controls on the flow node.

## PublishTrigger
In this example the publishTrigger control enables multiple publish calls for the flow node. Child notifications are in 
addition to the normal triggering operation of the flow node. The values in the parent node are unchanged when publishing.

`publishTrigger(DataFlow.subscribeToSignal("publishMe"))`

Child DataFlow nodes are notified when publishTrigger fires or the map function executes in a calculation cycle.

{% highlight java %}
public class TriggerPublishSample {
    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribeToNode(new SubscribeToNodeSample.MyComplexNode())
                .console("node triggered -> {}")
                .map(SubscribeToNodeSample.MyComplexNode::getIn)
                .aggregate(Collectors.listFactory(4))
                .publishTrigger(DataFlow.subscribeToSignal("publishMe"))
                .console("last 4 elements:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(TriggerPublishSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");
        processor.onEvent("E");
        processor.onEvent("F");

        processor.publishSignal("publishMe");
        processor.publishSignal("publishMe");
        processor.publishSignal("publishMe");
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
last 4 elements:[C, D, E, F]
last 4 elements:[C, D, E, F]
last 4 elements:[C, D, E, F]

{% endhighlight %}

## PublishTriggerOverride
In this example the publishTrigger control overrides the normal triggering operation of the flow node. The child is notified
only when publishTriggerOverride fires, changes due to recalculation are swallowed and not published downstream.
The values in the parent node are unchanged when publishing.

`publishTriggerOverride(DataFlow.subscribeToSignal("publishMe"))`

Child DataFlow nodes are notified when publishTriggerOverride fires.

{% highlight java %}
public class TriggerPublishOverrideSample {
    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribeToNode(new SubscribeToNodeSample.MyComplexNode())
                .console("node triggered -> {}")
                .map(SubscribeToNodeSample.MyComplexNode::getIn)
                .aggregate(Collectors.listFactory(4))
                .publishTriggerOverride(DataFlow.subscribeToSignal("publishMe"))
                .console("last 4 elements:{}\n");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(TriggerPublishOverrideSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");

        processor.publishSignal("publishMe");
        processor.onEvent("E");
        processor.onEvent("F");
        processor.onEvent("G");
        processor.onEvent("H");

        processor.publishSignal("publishMe");
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
node triggered -> SubscribeToNodeSample.MyComplexNode(in=A)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=B)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=C)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=D)
last 4 elements:[A, B, C, D]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=E)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=F)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=G)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=H)
last 4 elements:[E, F, G, H]
{% endhighlight %}

## UpdateTrigger
In this example the updateTrigger controls when the functional mapping operation of the flow node is invoked. The values 
are only aggregated when the update trigger is called. Notifications from the parent node are ignored and do not trigger
a mapping operation.

`updateTrigger(DataFlow.subscribeToSignal("updateMe"))`

A map operation only occurs when the update trigger fires. 

{% highlight java %}
public class TriggerUpdateSample {
    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribeToNode(new SubscribeToNodeSample.MyComplexNode())
                .console("node triggered -> {}")
                .map(SubscribeToNodeSample.MyComplexNode::getIn)
                .aggregate(Collectors.listFactory(4))
                .updateTrigger(DataFlow.subscribeToSignal("updateMe"))
                .console("last 4 elements:{}\n");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(TriggerUpdateSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.publishSignal("updateMe");

        processor.onEvent("D");
        processor.onEvent("E");
        processor.onEvent("F");
        processor.publishSignal("updateMe");
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
node triggered -> SubscribeToNodeSample.MyComplexNode(in=A)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=B)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=C)
last 4 elements:[C]

node triggered -> SubscribeToNodeSample.MyComplexNode(in=D)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=E)
node triggered -> SubscribeToNodeSample.MyComplexNode(in=F)
last 4 elements:[C, F]
{% endhighlight %}

## ResetTrigger
In this example the resetTrigger controls when the functional mapping operation of the flow node is reset. The aggregate
operation is stateful so all the values in the list are removed when then reset trigger fires. The reset operation causes 
trigger a notification to children of the flow node.

`resetTrigger(DataFlow.subscribeToSignal("resetMe"))`

The reset trigger notifies the stateful function to clear its state.

{% highlight java %}
public class TriggerResetSample {
    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribeToNode(new SubscribeToNodeSample.MyComplexNode())
                .console("node triggered -> {}")
                .map(SubscribeToNodeSample.MyComplexNode::getIn)
                .aggregate(Collectors.listFactory(4))
                .resetTrigger(DataFlow.subscribeToSignal("resetMe").console("\n--- resetTrigger ---"))
                .console("last 4 elements:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(TriggerResetSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");

        processor.publishSignal("resetMe");
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

--- resetTrigger ---
last 4 elements:[]
node triggered -> SubscribeToNodeSample.MyComplexNode(in=E)
last 4 elements:[E]
node triggered -> SubscribeToNodeSample.MyComplexNode(in=F)
last 4 elements:[E, F]
{% endhighlight %}

## Stateful function reset
Stateful functions can be reset by implementing the [Stateful]({{site.fluxtion_src_runtime}}/dataflow/Stateful.java) interface with a reset 
method. Configuring the resetTrigger will automatically route calls to the reset method of the stateful function.


{% highlight java %}
public class ResetFunctionSample {
    public static class MyResetSum implements Stateful<Integer> {
        public int count = 0;

        public int increment(Object o){
            return ++count;
        }

        @Override
        public Integer reset() {
            System.out.println("--- RESET CALLED ---");
            count = 0;
            return count;
        }
    }

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(String.class)
                .map(new MyResetSum()::increment)
                .resetTrigger(DataFlow.subscribeToSignal("resetMe"))
                .console("count:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(ResetFunctionSample::buildGraph);
        processor.init();

        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");

        processor.publishSignal("resetMe");
        processor.onEvent("E");
        processor.onEvent("F");
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
count:1
count:2
count:3
count:4
--- RESET CALLED ---
count:0
count:1
count:2
{% endhighlight %}


# Aggregating
Aggregating extends the concept of stateful map functions by adding behaviour when using functions in stateful operations
like windowing and grouping. An aggregate function has these behaviours:

- Stateful - defines the reset method
- aggregate - aggregate a value and calculate a result
- combine/deduct - combine or deduct another instance of this function, used when windowing
- deduct supported - can this instance deduct another instance of this function or is loop required to recalculate

Create an aggregate in a DataFlow with the call:

`DataFlow.aggregate(Supplier<AggregateFlowFunction> aggregateSupplier)` 

DataFlow.aggregate takes a Supplier of [AggregateFlowFunction]({{site.fluxtion_src_runtime}}/dataflow/aggregate/AggregateFlowFunction.java)'s not a 
single AggregateFlowFunction instance. When managing windowing and groupBy operations the event processor creates instances 
of AggregateFlowFunction to partition function state.

{% highlight java %}
public class AggregateSample {
    public record ResetList() {}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ResetList.class).console("\n--- RESET ---");

        DataFlow.subscribe(String.class)
                .aggregate(Collectors.listFactory(3))
                .resetTrigger(resetSignal)
                .console("ROLLING list: {}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(AggregateSample::buildGraph);
        processor.init();
        processor.onEvent("A");
        processor.onEvent("B");
        processor.onEvent("C");
        processor.onEvent("D");
        processor.onEvent("E");

        processor.onEvent(new ResetList());
        processor.onEvent("P");
        processor.onEvent("Q");
        processor.onEvent("R");

        processor.onEvent(new ResetList());
        processor.onEvent("XX");
        processor.onEvent("YY");
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
ROLLING list: [A]
ROLLING list: [A, B]
ROLLING list: [A, B, C]
ROLLING list: [B, C, D]
ROLLING list: [C, D, E]

--- RESET ---
ROLLING list: []
ROLLING list: [P]
ROLLING list: [P, Q]
ROLLING list: [P, Q, R]

--- RESET ---
ROLLING list: []
ROLLING list: [XX]
ROLLING list: [XX, YY]
{% endhighlight %}

## Custom aggregate function
Users can create aggregate functions that plug into the reset trigger callbacks in a DataFlow. The steps to create a
user aggregate function:

- Extend [AggregateFlowFunction]({{site.fluxtion_src_runtime}}/dataflow/aggregate/AggregateFlowFunction.java), the type parameters define the input and output types of the function
- Implement the reset, get and aggregate methods
- Return null from the aggregate method to indicate no change to the aggregate output

The example below maintains a date range as a String and resets the range when reset trigger is fired. When the date range
is unaltered the aggregate operation returns a null and no notifications are triggered.

{% highlight java %}
public class CustomAggregateFunctionSample {
    public static class DateRangeAggregate implements AggregateFlowFunction<LocalDate, String, DateRangeAggregate> {
        private LocalDate startDate;
        private LocalDate endDate;
        private String message;
        private final transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public String reset() {
            System.out.println("--- RESET ---");
            startDate = null;
            endDate = null;
            message = null;
            return get();
        }

        @Override
        public String get() {
            return message;
        }

        @Override
        public String aggregate(LocalDate input) {
            startDate = startDate == null ? input : startDate;
            endDate = endDate == null ? input : endDate;
            if (input.isBefore(startDate)) {
                startDate = input;
            } else if (input.isAfter(endDate)) {
                endDate = input;
            } else {
                //RETURN NULL -> NO CHANGE NOTIFICATIONS FIRED
                return null;
            }
            message = formatter.format(startDate) + " - " + formatter.format(endDate);
            return message;
        }
    }

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(LocalDate.class)
                .aggregate(DateRangeAggregate::new)
                .resetTrigger(DataFlow.subscribeToSignal("resetDateRange"))
                .console("UPDATED date range : '{}'");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(CustomAggregateFunctionSample::buildGraph);
        processor.init();

        processor.onEvent(LocalDate.of(2019, 8, 10));
        processor.onEvent(LocalDate.of(2009, 6, 14));
        processor.onEvent(LocalDate.of(2024, 4, 22));
        processor.onEvent(LocalDate.of(2021, 3, 30));

        //reset
        processor.publishSignal("resetDateRange");
        processor.onEvent(LocalDate.of(2019, 8, 10));
        processor.onEvent(LocalDate.of(2021, 3, 30));
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
UPDATED date range : '2009-06-14 - 2019-08-10'
UPDATED date range : '2009-06-14 - 2024-04-22'
--- RESET ---
UPDATED date range : '2019-08-10 - 2021-03-30'
{% endhighlight %}

# Windowing
Fluxtion supports windowing operations in a DataFlow to aggregate data. There are four types of windows supported:

- Tumbling window with custom start/stop triggers
- Tumbling time based windows start/stop triggers fire on a timer
- Sliding time based windows bucket size is timer based, calculations fire on a timer
- Sliding windows bucket size is based on count calculations fire on a bucket count

Fluxtion does not run threads, it is an event driven data structure. On a calculation cycle the window monitors read 
the time of the clock and expire windows if necessary.

{: .info }
To advance time in an event processor send any event regularly, this causes the window expiry calculation to run
{: .fs-4 }

## Tumbling windows
{: .no_toc }

Imagine tumbling windows as distinct buckets collecting data for a fixed size window. Once a bucket fills up, it's closed and 
published downstream. A new, empty bucket is created to collect the next batch of data. Tumbling windows never overlap, 
ensuring all data points are processed exactly once. This is good for capturing complete snapshots of the data at regular intervals.

## Sliding windows
{: .no_toc }

Think of sliding window as a constantly moving window on the data stream. The window has a fixed size, but it advances 
by a set increment (called the slide). As the window slides forward, new data enters at the front, and old data falls 
out the back. Unlike tumbling windows, sliding windows can overlap significantly, with data points contributing to 
multiple windows. This is useful for capturing trends and changes happening over time. As each slide occurs downstream
nodes are triggered.

## Diagram comparing tumbling and sliding windows
{: .no_toc }

<br/>

![](../../images/tumbling_vs_sliding_windows.png)

## Tumbling time window

Fluxtion supports a tumbling time window for any DataFlow node with this call:

`tumblingAggregate(Supplier<AggregateFlowFunction> aggregateFunction, int bucketSizeMillis)`

The lifecycle of the AggregateFlowFunction is managed by the event processor, tracking the current time and firing 
notifications to child nodes when the timer expires. Reset calls to the stateful function are also handled by the event 
processor.

An automatically added [FixedRateTrigger]({{site.fluxtion_src_runtime}}/time/FixedRateTrigger.java) monitors the tumbling
window for expiry an event is received. If the window has expired, the following actions occur:

* The window aggregate is calculated and cached for inspection
* The aggregate function is reset
* Downstream nodes are triggered with the cached value

This example publishes a random Integer every 10 milliseconds, the int sum calculates the current sum for the window. 
Every 300 milliseconds the cumulative sum for the window just expired is logged to console.

{% highlight java %}
public class TumblingWindowSample {

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Integer.class)
                .tumblingAggregate(Aggregates.intSumFactory(), 300)
                .console("current tumble sum:{} eventTime:%e");
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(TumblingWindowSample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(rand.nextInt(100));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
current tumble sum:1325 eventTime:1714920607551
current tumble sum:1543 eventTime:1714920607858
current tumble sum:1482 eventTime:1714920608150
current tumble sum:1694 eventTime:1714920608451
current tumble sum:1785 eventTime:1714920608751
current tumble sum:1338 eventTime:1714920609051
current tumble sum:1160 eventTime:1714920609350
current tumble sum:1511 eventTime:1714920609651
current tumble sum:1516 eventTime:1714920609951
current tumble sum:1489 eventTime:1714920610252
current tumble sum:1389 eventTime:1714920610551
current tumble sum:1693 eventTime:1714920610851
current tumble sum:1347 eventTime:1714920611152
{% endhighlight %}

## Tumbling trigger based window

To create a tumbling cart that is none-time based we use the trigger overrides to control resetting and publishing the
values in the tumbling window:

{% highlight java %}
resetTrigger(resetSignal)
publishTriggerOverride(publishSignal)
{% endhighlight %}

In this example we have a shopping cart that can have at the most three items. The cart can be cleared with a ClearCart
event. A GoToCheckout event publishes the contents of the cart down stream if the number of items > 0;

{% highlight java %}
public class TumblingTriggerSample {

    public record ClearCart() {}
    public record GoToCheckout() {}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ClearCart.class).console("\n--- CLEAR CART ---");
        var publishSignal = DataFlow.subscribe(GoToCheckout.class).console("\n--- CHECKOUT CART ---");

        DataFlow.subscribe(String.class)
                .aggregate(Collectors.listFactory(3))
                .resetTrigger(resetSignal)
                .publishTriggerOverride(publishSignal)
                .filter(l -> !l.isEmpty())
                .console("CURRENT CART: {}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(TumblingTriggerSample::buildGraph);
        processor.init();
        processor.onEvent("Gloves");
        processor.onEvent("Toothpaste");
        processor.onEvent("Towel");
        processor.onEvent("Plug");
        processor.onEvent("Mirror");
        processor.onEvent("Drill");
        processor.onEvent("Salt");

        processor.onEvent(new ClearCart());
        processor.onEvent("Apples");
        processor.onEvent("Camera");

        processor.onEvent(new GoToCheckout());
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
--- CLEAR CART ---

--- CHECKOUT CART ---
CURRENT CART: [Apples, Camera]
{% endhighlight %}

## Sliding time window

Fluxtion supports a sliding time window for any DataFlow node with this call:

`slidingAggregate(Supplier<AggregateFlowFunction> aggregateFunction, int bucketSizeMillis, int bucketsPerWindow)`

The lifecycle of the AggregateFlowFunction is managed by the event processor, tracking the current time and firing
notifications to child nodes when the timer expires. 

An automatically added [FixedRateTrigger]({{site.fluxtion_src_runtime}}/time/FixedRateTrigger.java) monitors the sliding
window for expiry an event is received. If the window has expired, the following actions occur:

* The aggregate for the current window is calculated and combined with the aggregate for the whole sliding window
* The aggregate for the oldest window is deducted from the aggregate for the whole sliding window
* The aggregate for the whole sliding window is cached and stored for inspection
* Downstream nodes are triggered with the cached value

This example publishes a random Integer every 10 milliseconds, the int sum calculates the current sum for the window.
There are 4 buckets each of 300 milliseconds in size, once every 300 milliseconds the aggregate sum for the past 1.2 
seconds is logged to console.

As the effective window size is 1.2 seconds the sliding window values are approximately 4 times larger than the tumbling
window example that resets the sum every 300 milliseconds.

{% highlight java %}
public class SlidingWindowSample {

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Integer.class)
                .slidingAggregate(Aggregates.intSumFactory(), 300, 4)
                .console("current sliding 1.2 second sum:{} eventTime:%e");
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(SlidingWindowSample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(rand.nextInt(100));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
current sliding 1.2 second sum:6092 eventTime:1714938615556
current sliding 1.2 second sum:5905 eventTime:1714938615854
current sliding 1.2 second sum:5751 eventTime:1714938616156
current sliding 1.2 second sum:5612 eventTime:1714938616456
current sliding 1.2 second sum:5558 eventTime:1714938616756
current sliding 1.2 second sum:5817 eventTime:1714938617054
current sliding 1.2 second sum:6295 eventTime:1714938617356
current sliding 1.2 second sum:6437 eventTime:1714938617656
current sliding 1.2 second sum:6397 eventTime:1714938617955
current sliding 1.2 second sum:6130 eventTime:1714938618254
{% endhighlight %}


# GroupBy
Fluxtion dsl offers many groupBy operations that partition based on a key function and then apply and aggregate operation
to the partition.

## GroupBy and aggregate

{% highlight java %}
public class GroupBySample {
    public record ResetList() {}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ResetList.class).console("\n--- RESET ---");

        DataFlow.subscribe(Integer.class)
                .groupBy(i -> i % 2 == 0 ? "evens" : "odds", Aggregates.countFactory())
                .resetTrigger(resetSignal)
                .map(GroupBy::toMap)
                .console("ODD/EVEN map:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupBySample::buildGraph);
        processor.init();
        processor.onEvent(1);
        processor.onEvent(2);

        processor.onEvent(new ResetList());
        processor.onEvent(5);
        processor.onEvent(7);

        processor.onEvent(new ResetList());
        processor.onEvent(2);
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
ODD/EVEN map:{odds=1}
ODD/EVEN map:{odds=1, evens=1}

--- RESET ---
ODD/EVEN map:{}
ODD/EVEN map:{odds=1}
ODD/EVEN map:{odds=2}

--- RESET ---
ODD/EVEN map:{}
ODD/EVEN map:{evens=1}
{% endhighlight %}


## GroupBy to list

{% highlight java %}
public class GroupByToListSample {
    public record ResetList() {}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ResetList.class).console("\n--- RESET ---");

        DataFlow.subscribe(Integer.class)
                .groupByToList(i -> i % 2 == 0 ? "evens" : "odds")
                .resetTrigger(resetSignal)
                .map(GroupBy::toMap)
                .console("ODD/EVEN map:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupByToListSample::buildGraph);
        processor.init();
        processor.onEvent(1);
        processor.onEvent(2);
        processor.onEvent(5);
        processor.onEvent(7);
        processor.onEvent(2);
        processor.onEvent(new ResetList());
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
ODD/EVEN map:{odds=[1]}
ODD/EVEN map:{odds=[1], evens=[2]}
ODD/EVEN map:{odds=[1, 5], evens=[2]}
ODD/EVEN map:{odds=[1, 5, 7], evens=[2]}
ODD/EVEN map:{odds=[1, 5, 7], evens=[2, 2]}

--- RESET ---
ODD/EVEN map:{}
{% endhighlight %}


## GroupBy to set

{% highlight java %}
public class GroupByToSetSample {

    public record ResetList() {}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ResetList.class).console("\n--- RESET ---");

        DataFlow.subscribe(Integer.class)
                .groupByToSet(i -> i % 2 == 0 ? "evens" : "odds")
                .resetTrigger(resetSignal)
                .map(GroupBy::toMap)
                .console("ODD/EVEN map:{}");
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupByToSetSample::buildGraph);
        processor.init();
        processor.onEvent(1);
        processor.onEvent(2);
        processor.onEvent(2);
        processor.onEvent(5);
        processor.onEvent(5);
        processor.onEvent(5);
        processor.onEvent(7);
        processor.onEvent(2);
        processor.onEvent(new ResetList());
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
ODD/EVEN map:{odds=[1]}
ODD/EVEN map:{odds=[1], evens=[2]}
ODD/EVEN map:{odds=[1], evens=[2]}
ODD/EVEN map:{odds=[1, 5], evens=[2]}
ODD/EVEN map:{odds=[1, 5], evens=[2]}
ODD/EVEN map:{odds=[1, 5], evens=[2]}
ODD/EVEN map:{odds=[1, 5, 7], evens=[2]}
ODD/EVEN map:{odds=[1, 5, 7], evens=[2]}

--- RESET ---
ODD/EVEN map:{}
{% endhighlight %}

## GroupBy with compound key

{% highlight java %}
public class GroupByFieldsSample {

    public record Pupil(int year, String sex, String name){}

    public static void buildGraph(EventProcessorConfig processorConfig) {

        DataFlow.subscribe(Pupil.class)
                .groupByFieldsAggregate(Aggregates.countFactory(), Pupil::year, Pupil::sex)
                .map(GroupByFieldsSample::formatGroupBy)
                .console("Pupil count by year/sex \n----\n{}----\n");
    }

    private static String formatGroupBy(GroupBy<GroupByKey<Pupil>, Integer> groupBy) {
        Map<GroupByKey<Pupil>, Integer> groupByMap = groupBy.toMap();
        StringBuilder stringBuilder = new StringBuilder();
        groupByMap.forEach((k, v) -> stringBuilder.append(k.getKey() + ": " + v + "\n"));
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupByFieldsSample::buildGraph);
        processor.init();

        processor.onEvent(new Pupil(2015, "Female", "Bob"));
        processor.onEvent(new Pupil(2013, "Male", "Ashkay"));
        processor.onEvent(new Pupil(2013, "Male", "Channing"));
        processor.onEvent(new Pupil(2013, "Female", "Chelsea"));
        processor.onEvent(new Pupil(2013, "Female", "Tamsin"));
        processor.onEvent(new Pupil(2013, "Female", "Ayola"));
        processor.onEvent(new Pupil(2015, "Female", "Sunita"));
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
Pupil count by year/sex
----
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 1
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 2
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 2
2013_Female_: 1
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 2
2013_Female_: 2
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 2
2013_Female_: 3
2015_Female_: 1
----

Pupil count by year/sex
----
2013_Male_: 2
2013_Female_: 3
2015_Female_: 2
----
{% endhighlight %}

# Windowed GroupBy

## Tumbling GroupBy

{% highlight java %}
public class TumblingGroupBySample {
    public record Trade(String symbol, int amountTraded) {}
    private static String[] symbols = new String[]{"GOOG", "AMZN", "MSFT", "TKM"};

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Trade.class)
                .groupByTumbling(Trade::symbol, Trade::amountTraded, Aggregates.intSumFactory(), 250)
                .map(GroupBy::toMap)
                .console("Trade volume for last 250 millis:{} time:%e");
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(TumblingGroupBySample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(new Trade(symbols[rand.nextInt(symbols.length)], rand.nextInt(100)));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}


{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
Trade volume for last 250 millis:{MSFT=560, GOOG=95, AMZN=411, TKM=331} time:1714938250829
Trade volume for last 250 millis:{MSFT=486, GOOG=408, AMZN=169, TKM=247} time:1714938251079
Trade volume for last 250 millis:{MSFT=427, GOOG=423, AMZN=246, TKM=215} time:1714938251329
Trade volume for last 250 millis:{MSFT=461, GOOG=204, AMZN=477, TKM=369} time:1714938251579
Trade volume for last 250 millis:{MSFT=392, GOOG=181, AMZN=273, TKM=341} time:1714938251829
Trade volume for last 250 millis:{MSFT=322, GOOG=296, AMZN=403, TKM=175} time:1714938252079
Trade volume for last 250 millis:{MSFT=348, GOOG=253, AMZN=204, TKM=316} time:1714938252328
Trade volume for last 250 millis:{MSFT=377, GOOG=481, AMZN=161, TKM=382} time:1714938252578
Trade volume for last 250 millis:{MSFT=302, GOOG=344, AMZN=386, TKM=232} time:1714938252829
Trade volume for last 250 millis:{MSFT=232, GOOG=144, AMZN=477, TKM=365} time:1714938253078
Trade volume for last 250 millis:{MSFT=430, GOOG=328, AMZN=294, TKM=473} time:1714938253329
Trade volume for last 250 millis:{MSFT=499, GOOG=183, AMZN=152, TKM=470} time:1714938253578
Trade volume for last 250 millis:{MSFT=338, GOOG=334, AMZN=579, TKM=200} time:1714938253829
Trade volume for last 250 millis:{MSFT=278, GOOG=363, AMZN=390, TKM=44} time:1714938254079
Trade volume for last 250 millis:{MSFT=429, GOOG=225, AMZN=399, TKM=36} time:1714938254329
Trade volume for last 250 millis:{MSFT=88, GOOG=290, AMZN=300, TKM=554} time:1714938254577
{% endhighlight %}


## Sliding GroupBy

{% highlight java %}
public class SlidingGroupBySample {
    public record Trade(String symbol, int amountTraded) {}
    private static String[] symbols = new String[]{"GOOG", "AMZN", "MSFT", "TKM"};

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Trade.class)
                .groupBySliding(Trade::symbol, Trade::amountTraded, Aggregates.intSumFactory(), 250, 4)
                .map(GroupBy::toMap)
                .console("Trade volume for last second:{} time:%e");
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(SlidingGroupBySample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(new Trade(symbols[rand.nextInt(symbols.length)], rand.nextInt(100)));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
Trade volume for last second:{MSFT=970, GOOG=1355, AMZN=1203, TKM=1077} time:1714938293704
Trade volume for last second:{MSFT=857, GOOG=1359, AMZN=1147, TKM=1245} time:1714938293954
Trade volume for last second:{MSFT=1078, GOOG=1370, AMZN=832, TKM=1237} time:1714938294204
Trade volume for last second:{MSFT=1433, GOOG=1239, AMZN=836, TKM=1211} time:1714938294454
Trade volume for last second:{MSFT=1494, GOOG=1458, AMZN=862, TKM=1153} time:1714938294704
Trade volume for last second:{MSFT=1515, GOOG=1401, AMZN=1170, TKM=1141} time:1714938294954
Trade volume for last second:{MSFT=1413, GOOG=1204, AMZN=1372, TKM=1458} time:1714938295202
Trade volume for last second:{MSFT=1113, GOOG=1144, AMZN=1555, TKM=1618} time:1714938295454
Trade volume for last second:{MSFT=1250, GOOG=967, AMZN=1365, TKM=1402} time:1714938295704
Trade volume for last second:{MSFT=1388, GOOG=938, AMZN=1126, TKM=1416} time:1714938295954
Trade volume for last second:{MSFT=1331, GOOG=1060, AMZN=1195, TKM=1121} time:1714938296202
Trade volume for last second:{MSFT=1439, GOOG=998, AMZN=907, TKM=1111} time:1714938296453
Trade volume for last second:{MSFT=1159, GOOG=1537, AMZN=1151, TKM=1133} time:1714938296704
{% endhighlight %}


## Tumbling GroupBy with compound key

{% highlight java %}

public class TumblingGroupByCompoundKeySample {
    public record Trade(String symbol, String client, int amountTraded) {}
    private static String[] symbols = new String[]{"GOOG", "AMZN", "MSFT", "TKM"};
    private static String[] clients = new String[]{"client_A", "client_B", "client_D", "client_E"};

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Trade.class)
                .groupByTumbling(
                        GroupByKey.build(Trade::client, Trade::symbol),
                        Trade::amountTraded,
                        Aggregates.intSumFactory(),
                        250)
                .map(TumblingGroupByCompoundKeySample::formatGroupBy)
                .console("Trade volume tumbling per 250 millis by client and symbol time:%e:\n{}----------------------\n");
    }

    private static <T> String formatGroupBy(GroupBy<GroupByKey<T>, Integer> groupBy) {
        Map<GroupByKey<T>, Integer> groupByMap = groupBy.toMap();
        StringBuilder stringBuilder = new StringBuilder();
        groupByMap.forEach((k, v) -> stringBuilder.append(k.getKey() + ": " + v + "\n"));
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(TumblingGroupByCompoundKeySample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(new Trade(symbols[rand.nextInt(symbols.length)], clients[rand.nextInt(clients.length)], rand.nextInt(100)));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
Trade volume tumbling per 250 millis by client and symbol time:1714950058070:
client_B_TKM_: 176
client_E_AMZN_: 2
client_D_AMZN_: 32
client_A_TKM_: 96
client_E_GOOG_: 296
client_D_GOOG_: 2
client_A_AMZN_: 44
client_D_TKM_: 74
client_A_GOOG_: 161
client_E_MSFT_: 68
client_D_MSFT_: 47
client_B_MSFT_: 239
client_A_MSFT_: 73
----------------------

Trade volume tumbling per 250 millis by client and symbol time:1714950058321:
client_B_TKM_: 119
client_E_AMZN_: 47
client_D_AMZN_: 107
client_A_TKM_: 18
client_E_GOOG_: 142
client_E_TKM_: 181
client_D_GOOG_: 83
client_B_AMZN_: 17
client_D_TKM_: 169
client_A_GOOG_: 2
client_B_GOOG_: 181
client_E_MSFT_: 26
client_D_MSFT_: 1
client_A_MSFT_: 132
client_B_MSFT_: 56
----------------------

Trade volume tumbling per 250 millis by client and symbol time:1714950058571:
client_B_TKM_: 131
client_E_AMZN_: 41
client_D_AMZN_: 14
client_A_TKM_: 39
client_E_GOOG_: 4
client_E_TKM_: 153
client_D_GOOG_: 53
client_A_AMZN_: 137
client_B_AMZN_: 158
client_D_TKM_: 86
client_A_GOOG_: 91
client_B_GOOG_: 156
client_E_MSFT_: 43
client_A_MSFT_: 36
client_B_MSFT_: 253
----------------------
{% endhighlight %}


## Sliding GroupBy with compound key

{% highlight java %}
public class SlidingGroupByCompoundKeySample {
    public record Trade(String symbol, String client, int amountTraded) {}
    private static String[] symbols = new String[]{"GOOG", "AMZN", "MSFT", "TKM"};
    private static String[] clients = new String[]{"client_A", "client_B", "client_D", "client_E"};

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Trade.class)
                .groupBySliding(
                        GroupByKey.build(Trade::client, Trade::symbol),
                        Trade::amountTraded, 
                        Aggregates.intSumFactory(),
                        250, 4)
                .map(SlidingGroupByCompoundKeySample::formatGroupBy)
                .console("Trade volume for last second by symbol/client time:%e:\n{} \n--------\n");
    }

    private static String formatGroupBy(GroupBy<GroupByKey<Trade>, Integer> groupBy) {
        Map<GroupByKey<Trade>, Integer> groupByMap = groupBy.toMap();
        StringBuilder stringBuilder = new StringBuilder();
        groupByMap.forEach((k, v) -> stringBuilder.append(k.getKey() + ": " + v + "\n"));
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        var processor = Fluxtion.interpret(SlidingGroupByCompoundKeySample::buildGraph);
        processor.init();
        Random rand = new Random();

        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(
                    () -> {
                        processor.onEvent("tick");
                        processor.onEvent(new Trade(symbols[rand.nextInt(symbols.length)], clients[rand.nextInt(clients.length)], rand.nextInt(100)));
                    },
                    10,10, TimeUnit.MILLISECONDS);
            Thread.sleep(4_000);
        }
    }
}

{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
Trade volume for last second by client and symbol time:1714948876439:
client_B_TKM_: 391
client_E_AMZN_: 29
client_A_TKM_: 393
client_A_GOOG_: 254
client_B_MSFT_: 333
client_A_MSFT_: 220
client_B_GOOG_: 165
client_E_GOOG_: 381
client_D_GOOG_: 357
client_E_TKM_: 270
client_E_MSFT_: 372
client_D_MSFT_: 408
client_A_AMZN_: 342
client_B_AMZN_: 277
client_D_AMZN_: 46
client_D_TKM_: 462
----------------------

Trade volume for last second by client and symbol time:1714948876689:
client_B_TKM_: 517
client_E_AMZN_: 70
client_A_TKM_: 304
client_A_GOOG_: 257
client_B_MSFT_: 351
client_A_MSFT_: 217
client_B_GOOG_: 119
client_E_GOOG_: 278
client_D_GOOG_: 244
client_E_TKM_: 281
client_E_MSFT_: 390
client_D_MSFT_: 297
client_A_AMZN_: 434
client_B_AMZN_: 465
client_D_AMZN_: 118
client_D_TKM_: 347
----------------------

Trade volume for last second by client and symbol time:1714948876939:
client_B_TKM_: 386
client_E_AMZN_: 50
client_A_TKM_: 249
client_A_GOOG_: 118
client_B_MSFT_: 551
client_A_MSFT_: 335
client_B_GOOG_: 119
client_E_GOOG_: 300
client_D_GOOG_: 116
client_E_TKM_: 310
client_E_MSFT_: 526
client_D_MSFT_: 297
client_A_AMZN_: 392
client_B_AMZN_: 495
client_D_AMZN_: 190
client_D_TKM_: 295
----------------------
{% endhighlight %}

# GroupBy functional support

Fluxtion offers extended methods for manipulating a GroupBy instance of DataFlow node

## Mapping keys
Keys of GroupBy can be mapped with

`mapKeys(Function<KEY_OLD, KEY_NEW> keyMappingFunction)`


{% highlight java %}
public class GroupByMapKeySample {

    public record Pupil(int year, String sex, String name){}

    public static void buildGraph(EventProcessorConfig processorConfig) {
        DataFlow.subscribe(Pupil.class)
                .groupByFieldsAggregate(Aggregates.countFactory(), Pupil::year, Pupil::sex)
                .mapKeys(GroupByKey::getKey)//MAPS KEYS
                .map(GroupBy::toMap)
                .console("{}\n----");
    }
    
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupByMapKeySample::buildGraph);
        processor.init();

        processor.onEvent(new Pupil(2015, "Female", "Bob"));
        processor.onEvent(new Pupil(2013, "Male", "Ashkay"));
        processor.onEvent(new Pupil(2013, "Male", "Channing"));
        processor.onEvent(new Pupil(2013, "Female", "Chelsea"));
        processor.onEvent(new Pupil(2013, "Female", "Tamsin"));
        processor.onEvent(new Pupil(2013, "Female", "Ayola"));
        processor.onEvent(new Pupil(2015, "Female", "Sunita"));
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
{2015_Female_=1}
{2013_Male_=1, 2015_Female_=1}
{2013_Male_=2, 2015_Female_=1}
{2013_Male_=2, 2013_Female_=1, 2015_Female_=1}
{2013_Male_=2, 2013_Female_=2, 2015_Female_=1}
{2013_Male_=2, 2013_Female_=3, 2015_Female_=1}
{2013_Male_=2, 2013_Female_=3, 2015_Female_=2}
{% endhighlight %}


## Mapping values
Values of GroupBy can be mapped with

`mapValues(Function<VALUE_OLD, VALUE_NEW> valueMappingFunction)`


{% highlight java %}
public class GroupByMapValuesSample {

    public record ResetList() {
    }

    public static void buildGraph(EventProcessorConfig processorConfig) {
        var resetSignal = DataFlow.subscribe(ResetList.class).console("\n--- RESET ---");

        DataFlow.subscribe(Integer.class)
                .groupByToSet(i -> i % 2 == 0 ? "evens" : "odds")
                .resetTrigger(resetSignal)
                .mapValues(GroupByMapValuesSample::toRange)//MAPS VALUES
                .map(GroupBy::toMap)
                .console("ODD/EVEN map:{}");
    }

    private static String toRange(Set<Integer> integers) {
        int max = integers.stream().max(Integer::compareTo).get();
        int min = integers.stream().min(Integer::compareTo).get();
        return "range [" + min + "," + max + "]";
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(GroupByMapValuesSample::buildGraph);
        processor.init();
        processor.onEvent(1);
        processor.onEvent(2);
        processor.onEvent(2);
        processor.onEvent(5);
        processor.onEvent(5);
        processor.onEvent(5);
        processor.onEvent(7);
        processor.onEvent(2);
        processor.onEvent(new ResetList());
    }
}
{% endhighlight %}

Running the example code above logs to console

{% highlight console %}
ODD/EVEN map:{odds=range [1,1]}
ODD/EVEN map:{odds=range [1,1], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,1], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,5], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,5], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,5], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,7], evens=range [2,2]}
ODD/EVEN map:{odds=range [1,7], evens=range [2,2]}

--- RESET ---
ODD/EVEN map:{}
{% endhighlight %}