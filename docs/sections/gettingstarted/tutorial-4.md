---
title: 4th tutorial - event tracing
parent: Getting started
has_children: false
nav_order: 4
published: true
---

<details markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

# Introduction
This tutorial is an introduction to tracing event processing at runtime, we call this **event audit**.
The reader should be proficient in Java, maven, git, Spring and have completed the [first lottery tutorial](tutorial-1.md) before
starting this tutorial. The project source can be found [here.]({{site.getting_started}}/tutorial4-lottery-auditlog)

Our goal is to create a write a log file that traces the event call graph as events are processed 

At the end of this tutorial you should understand:

- The role of the EventLogger
- The format of an event log element
- How to add event auditing to the container 
- How to use EventLogger in a bean
- How to load a custom AuditLogProcessor in the container


# Event Audit concepts
Fluxtion manages the build complexity as more beans are added to the container but understanding deployed event processing 
can be difficult. Tracing the event propagation call tree through the nodes helps the developer understand the runtime 
behaviour. Fluxtion provides an audit log facility that records the traced call chain as a yaml document.

Event auditing is implemented as a custom Auditor using the monitoring callbacks to create a LogRecord, see [audit tutorial](tutorial-3.md)
for an example of Fluxtion auditing.

## Log output
Fluxtion produces a structured log output, each event is distinct LogRecord formatted as a yaml. A LogRecord includes
metadata for the input event and a list of elements that traces the node execution path. Each user node can write key value
pairs to the audit log. A yaml record distills the information, removes logging noise and are machine-readable. Node names 
are consistent with the bean names in the container simplifying correlation from log to source code.

{% highlight yaml %}
28-Sept-23 18:14:02 [main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: 1695921242024
logTime: 1695921242024
groupingId: null
event: ExportFunctionAuditEvent
eventToString: {public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)}
thread: {main}
nodeLogs:
- ticketStore: { ticketPurchased: Ticket[number=126556, id=d202456b-7231-476c-95e7-b718378d95a8]}
- lotteryMachine: { ticketsSold: 1}
endTime: 1695921242029
{% endhighlight %}

Points to note from this example:
- The header records the context for the event being processed
  - eventTime: the time the event was created if the incoming event provides that information
  - logTime: the time the log record is created i.e. when the event processing began 
  - endTime: the time the log record is complete i.e. when the event processing completed 
  - groupingId: a set of nodes can have a groupId and their audit configuration is controlled as a group 
  - event: The simple class name of the event that created the execution 
  - optional elements
    - eventToString: records the toString of the incoming event
    - thread: the thread the container event processing was called on
- The nodeLogs element records the progress through the node graph as a list element
  - Only nodes that are visited are recorded in the list
  - The order of the list mirrors the order of execution
  - The key is same name of the node variable in the container
  - A map of values is written for each node visited.

## LogRecordListener
The LogRecordListener receives a LogRecord for processing, the default LogRecordListener uses java util logging to output the 
records. A custom LogRecordListener can be added to the container at runtime allowing the LogRecord to be bound into 
any logging framework

## Custom LogRecord encoding
The event audit encodes the log records as a yaml document, the container accepts a custom encoder at runtime that 
encodes the log record before it is pushed to the LogRecordListener.
