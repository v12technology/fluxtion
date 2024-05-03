---
title: Mark event handling
has_children: true
nav_order: 6
published: true
---

# Introduction

Building and executing an event processor are independent functions that can run in separate processes. This section
describes how to mark user functions as event handling methods and when the functions should be invoked. Event handler 
invocation follows the [event dispatch rules](event-dispatch-rules). 

There are three steps to use Fluxtion, step 1 is covered here:

## Three steps to using Fluxtion

{: .info }
1 - **Mark event handling methods with annotations or via functional programming**<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

![](../images/integration_overview-running.drawio.png)

## Using an event processor

Once the event processor has been generated with user methods bound in it can be used by the application. An instance of
an
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, user code connects the EventProcessor to the application event
sources. An application can contain multiple EventProcessors instances or a single event processor can contain all the
application logic. An event processor notifies annotated callback methods according to the [dispatch rules](../core-technology#event-dispatch-rules).

- **Call EventProcessor.init() before first use**
- **EventProcessors are not thread safe** a single event should be processed at one time.
- **Each new event processed triggers a graph calculation cycle.**

Methods and instances are added to the config that feeds the fluxtion generator. The runtime behaviour of the generated
event processor is controlled by the annotations and config input. 

# Event dispatch rules
Notification connections between beans are calculated at construction time using the same data that is used to add beans
to the event processor and annotations that mark event handling methods.

When the proxy event handler method is called on the container it dispatches with the following with logic:

- Any top level event handler is invoked with the arguments provided
- The event handler method indicates whether child instances should be notified with a Boolean return type
- Any child reference defined in the DI structure is conditionally notified of the parent event handler completing
  processing
- A child instance can only be notified if all of its parents have finished processing their notifications
- The trigger method of the child returns a Boolean indicating whether the event notification should propagate
- The container recursively works through the child references and trigger methods in the container
- Dispatch callbacks are in strict topological order, with the event handler the root of the call tree
- Each instance is guaranteed to be invoked at maximum once per event processing cycle
- Any instances not connected to an executing root event handler will not be triggered in the cycle
- Connections can be either direct or through a reference chain

