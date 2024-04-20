---
title: Integration and testing
has_children: true
nav_order: 7
published: true
---

# Introduction
Building and executing an event processor are independent functions that can run in separate processes. This section
documents integrating an event processor into an application and unit testing the event processor.

There are three steps to use Fluxtion, step 3 is covered here:

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - **Integrate the event processor in the app and feed it events**
{: .fs-4 }

## Using an event processor

Once the event processor has been generated with user methods bound in it can be used by the application. An instance of an
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, user code connects
the EventProcessor to the application event sources. An application can contain multiple EventProcessors instances, and
routes events to an instance.

- **Call EventProcessor.init() before first use**
- **EventProcessors are not thread safe** a single event should be processed at one time.
- **Each new event processed triggers a graph calculation cycle.**

![](../images/integration_overview-running.drawio.png)

**To be completed**