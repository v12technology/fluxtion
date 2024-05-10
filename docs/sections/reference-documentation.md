---
title: Reference documentation
has_children: true
nav_order: 9
published: true
---

# Reference documentation
---

Describing event handling logic, building an event processor and integrating it into an application are independent
functions.
This section is a developer reference for each of those activities.

There are three steps to use Fluxtion, all 3 are covered here:

## Three steps to using Fluxtion

{: .no_toc }

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

A section for [dsl deep dive](fluxtion-explored/fluxtion-dsl) is also available for developers.

# Quick reference

A list of the most commonly used commands, annotations and DSL equivalents. On a day-to-day operation a developer will
only use 2 or 3 Fluxtion annotations, captured here.

## Event handling

Mark methods as callbacks that will be invoked on a calculation cycle. An event listener callback is triggered
when external events are posted to the processor. A trigger callback method is called when its parent has triggered due
to an incoming event. Boolean return type from trigger or event handler method indicates a change notification should be
propagated.

| Use                       | Annotation                         | DSL Equivalent                                                                                                                 | Description                                                                                                 |
|---------------------------|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| Event listener            | `@OnEventHandler`                  | `DataFlow.subscribe(Class<T> eventClass)`                                                                                      | Marks method as a subscriber callback<br/> to event stream of type T                                        |
| Trigger                   | `@OnTrigger`                       | `[flow].map.(Function<T, R> mapFunction)`                                                                                      | Marks method as callback calc method<br/>in a process cycle<br/>                                            |
| Identify trigger source   | `@OnParentUpdate`                  |                                                                                                                                | Marks method as callback method <br/>identifying changed parent. <br/>Called before trigger method          |
| No trigger Event listener | `@OnEventHandler(propagate=false)` |                                                                                                                                | Marks method as a subscriber callback<br/>No triggering of child callbacks                                  |
| Data only parent          | `@NoTriggerReference`              |                                                                                                                                | Mark a parent reference as data only.<br/>Parent changes are non-triggering for this                        |
| Push data to child        | `@PushReference`                   | `[flow].push.(Consumer<T, R> mapFunction)`                                                                                     | Marks a parent reference as a push target<br/> This pushes data to parent. <br/>Parent triggers after child |
| Filter events             | `@OnEvent(filterId)`               | `DataFlow.subscribe(` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`Class<T> classSubscription, ` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`int filter)` | Marks method as a subscriber callback<br/> to a filtered event stream of type T                             |

## Service export

Mark an interface as exported and the event processor will implement the interface and route any calls to the instance.
An interface method behaves as an event listener call back method that is annotated with `@OnEventHandler`.

| Use                   | Annotation                        | Description                                                          |
|-----------------------|-----------------------------------|----------------------------------------------------------------------|
| Export an interface   | `@ExportService`                  | All interface methods are event handlers triggering a process cycle  |
| No trigger one method | `@NoPropagateFunction`            | Mark a method as non-triggering an event process cycle on invocation |
| Data only interface   | `@ExportService(propagate=false)` | Mark a whole interface as non-triggering                             |

## DSL

DSL is used to create a data flow that can be mapped, filter, windowed, grouped etc. A data flow is created with a
subscription and then can be manipulated with functional operations.

| Use                        | DSL                                                                                                                                                                                                                       | Description                                                           |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| DataFlow from event stream | `DataFlow.subscribe(Class<T> eventClass)`                                                                                                                                                                                 | Subscribe to event of type T, creates a data flow of T                |
| DataFlow from a node       | `DataFlow.subscribeToNode(T sourceNode)`                                                                                                                                                                                  | Create a data flow of T. Triggers when T triggers                     |
| Map                        | `[flow].map(Function<T, R> mapFunction)`                                                                                                                                                                                  | Maps T to R when triggered                                            |
| Filter                     | `[flow].filter(Function<T, Boolean> filterFunction)`                                                                                                                                                                      | Filters T when triggered                                              |
| Tumbling window            | `[flow].tumblingAggregate(` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`Supplier<AggregateFlowFunction> aggregateFunction, ` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`int bucketSizeMillis)`                                                     | Aggregates T with aggregate function <br/>in a tumbling window        |
| Sliding window             | `[flow].slidingAggregate(` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`Supplier<AggregateFlowFunction> aggregateFunction, ` <br/>&nbsp;&nbsp;&nbsp;&nbsp;`int bucketSizeMillis, ` <br/>&nbsp;&nbsp;&nbsp;&nbsp;`int bucketsPerWindow)` | Aggregates T with aggregate function <br/>in a sliding window         |
| Group by                   | `[flow].groupBy(` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`Function<T, K1> keyFunction, ` <br/>&nbsp;&nbsp;&nbsp;&nbsp;`Supplier<F> aggregateFunctionSupplier`                                                                      | Groups T with key function applies an aggregate function to each item |
| Joining                    | `JoinFlowBuilder.innerJoin(` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`GroupByFlow<K1, V1> leftGroupBy, ` <br/> &nbsp;&nbsp;&nbsp;&nbsp;`GroupByFlow<K2, V2> rightGroupBy)`                                                          | Joins two group by data flows on their keys                           |

## Lifecycle

Mark methods to receive lifecycle callbacks that are invoked on the event processor. None of the lifecycle calls are
automatic it is the client code that is responsible for calling lifecycle methods on the event processor.

| Phase      | Annotation    | Description                                                                            |
|------------|---------------|----------------------------------------------------------------------------------------|
| Initialise | `@Initialise` | Called by client code once on an event processor. Must be called before start          |
| Start      | `@Start`      | Called by client code 0 to many time. Must be called after start                       |
| Stop       | `@Stop`       | Called by client code 0 to many time. Must be called after start                       |
| TearDown   | `@TearDown`   | Called by client code 0 or once on an event processor before the processor is disposed |
