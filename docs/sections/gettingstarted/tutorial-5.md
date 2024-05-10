---
title: 5th tutorial - event tracing
parent: Developer tutorials
has_children: false
nav_order: 6
published: true
auditor_src: tutorial4-lottery-auditlog/src/main/java/com/fluxtion/example/cookbook/lottery/auditor/SystemStatisticsAuditor.java
processor_src: tutorial4-lottery-auditlog/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java
slf4j_config: tutorial4-lottery-auditlog/src/main/resources/simplelogger.properties
---

# 5th tutorial - event tracing
{: .no_toc }
---

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

# Introduction
---

This tutorial is an introduction to tracing event processing at runtime, we call this **event audit**.
The reader should be proficient in Java, maven, git, Spring and have completed the [first lottery tutorial](tutorial-1.md) before
starting this tutorial.

Our goal is to write a log file that traces the event call graph as events are processed 

At the end of this tutorial you should understand:

- The role of the EventLogger
- The format of an event log element
- How to add event auditing to the container 
- How to use EventLogger in a bean
- How to load a custom AuditLogProcessor in the container

# Example project
The [example project]({{site.getting_started}}/tutorial4-lottery-auditlog) is referenced in this tutorial.

# Event Audit concepts
Fluxtion manages the build complexity as more beans are added to the container but understanding deployed event processing 
can be difficult. Tracing the event propagation call tree through the nodes helps the developer understand the runtime 
behaviour. Fluxtion provides an audit log facility that records the traced call chain as a yaml document.

Event auditing is implemented as a custom Auditor using the monitoring callbacks to create a LogRecord, see [audit tutorial](tutorial-4)
for an example of Fluxtion auditing.


## Processing logic

{: .no_toc }
Our design sketches show how the event logging integrates into our system. The EventLogManager is an auditor that is 
automatically registered with the event processor. The event processor exposes an api to control the event log auditor
at runtime.

```mermaid

flowchart TB
    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}


    LotteryMachine([<b>ServiceLookup</b>::LotteryMachine]):::exportedService
    TicketStore([<b>ServiceLookup</b>::TicketStore]):::exportedService
    
    TicketStoreNode[TicketStoreNode \n<b>Extends</b>::EventLogNode \n<b>ExportService</b>::TicketStore]:::graphNode
    LotteryMachineNode[LotteryMachineNode \n<b>Extends</b>::EventLogNode \n<b>ExportService</b>::LotteryMachine]:::graphNode
    Auditor[<b>Auditor::</b>EventLogManager] ----> id1[(<b>Event audit log</b>)]
 
    LotteryMachine & TicketStore ---> Auditor

    subgraph EventProcessor
        Auditor --> TicketStoreNode --> LotteryMachineNode
    end

    configureAuditLog[configure audit log:\n setAuditLogLevel] ----> EventProcessor

```

## Spring config

{% highlight xml %}
<beans>
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode">
    </bean>
    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>
    <bean class="com.fluxtion.compiler.extern.spring.FluxtionSpringConfig">
        <!-- IMPLICITLY ADDS THE EVENTLOGMANAGER AS AN AUDITOR -->
        <property name="logLevel" value="DEBUG"/>
    </bean>
</beans>
{% endhighlight %}

## Log output
Fluxtion produces a structured log output, known as a **[LogRecord]({{site.fluxtion_src_runtime}}/audit/LogRecord.java)**, 
for each event processed. LogRecords are marshalled into yaml documents that are machine-readable and remove logging noise.
A LogRecord includes metadata for the input event, a list of elements that traces the node execution path and optional
key value pairs written by the user code, 

Sample log record for a single event

{% highlight yaml %}

eventLogRecord:
    eventTime: 1696144229587
    logTime: 1696144229587
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs:
        - ticketStore: { ticketPurchased: Ticket[number=126556, id=dcf80fbc-b553-4465-b60c-0e7ee3125084]}
        - lotteryMachine: { ticketsSold: 1}
    endTime: 1696144229592
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
The **[LogRecordListener]({{site.fluxtion_src_runtime}}/audit/LogRecord.java)** receives a LogRecord for processing, 
the default Fluxtion LogRecordListener uses java util logging to output the records. A custom LogRecordListener can be added 
to the container at runtime allowing the LogRecord to be bound into any logging framework.

{% highlight java %}
lotteryEventProcessor.setAuditLogProcessor(new FluxtionSlf4jAuditor());
{% endhighlight %}

## Custom LogRecord encoding
The event audit encodes the log records as a yaml document, the container accepts a custom encoder at runtime that 
encodes the log record before it is pushed to the LogRecordListener.

## Method tracing
By default, the nodeLogs element only contains elements where user code has written a key/value pair to the LogRecord.
If method tracing is enabled any node visited is added to nodeLogs regardless of user code. The log level that method 
that enables tracing is passed as an argument to addEventAudit or as a config element in ths spring file

## Runtime log level
The runtime event audit LogLevel can be controlled at the container level:

{% highlight javav %}
lotteryEventProcessor.setAuditLogLevel(EventLogControlEvent.LogLevel.DEBUG);
{% endhighlight %}

## Add audit logging to user classes
To add audit logging to our nodes we extend EventLogNode and use the injected EventLogger to write key/value pairs.
User code can access the audit LogRecord at runtime via an  **[EventLogger]({{site.fluxtion_src_runtime}}/audit/EventLogger.java)**
instance and write values that will appear in the nodeLogs element for this node. The container injects EventLogger
instances to any bean that implements **[EventLogSource]({{site.fluxtion_src_runtime}}/audit/EventLogSource.java)**.


# Adding custom event auditing to the application

## Add auditing 
Classes that want to create audit log records extend **[EventLogNode]({{site.fluxtion_src_runtime}}/audit/EventLogNode.java)** and will receive an injected EventLogger
at runtime. Writing audit key/values is simply:

{% highlight java %}
public class LotteryMachineNode extends EventLogNode implements @ExportService LotteryMachine {

    //code removed for clarity ....
    @Start
    public void start() {
        Objects.requireNonNull(resultPublisher, "must set a results publisher before starting the lottery game");
        auditLog.info("resultPublisher", "valid");
    }

    @OnTrigger
    public boolean processNewTicketSale() {
        ticketsBought.add(ticketSupplier.get());
        auditLog.info("ticketsSold", ticketsBought.size());
        return false;
    }
}
{% endhighlight %}

## Custom Slf4f audit logger
The default Fluxtion audit implementation uses java.util.logging as and audit logger, we are going to replace this with  
a custom audit logger that is implemented with Slf4J's logger. Logging events will be sent to this class and can be 
processed in any way the user wants. 

To create a custom logger we implement the LogRecordListener interface to create the 
custom logger. 

{% highlight java %}

@Slf4j
public class FluxtionSlf4jAuditor implements LogRecordListener {
    @Override
    public void processLogRecord(LogRecord logRecord) {
        log.info("\n" + logRecord.toString() + "\n---");
    }
}
{% endhighlight %}

## Building the event processor with audit logging
As Fluxtion is in aot mode the serialised [LotteryProcessor]({{site.getting_started}}/{{page.processor_src}}) can be inspected
to locate where the notification callbacks to the auditor are injected.

The [FluxtionSpringConfig]({{site.fluxtion_src_compiler}}/extern/spring/FluxtionSpringConfig.java) bean in the spring
config file will automatically add the audit logger to the generated container. The supplied log level determines 
when method tracing is switched on, when audit LogLevel for the container is set to DEBUG tracing will be enabled.


{% highlight xml %}
<beans>
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode">
    </bean>
    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>
    <bean class="com.fluxtion.compiler.extern.spring.FluxtionSpringConfig">
        <!-- IMPLICITLY ADDS THE EVENTLOGMANAGER AS AN AUDITOR -->
        <property name="logLevel" value="DEBUG"/>
    </bean>
</beans>
{% endhighlight %}

The Fluxtion maven plugin will run as part of the build, logging this output as part of the build:

{% highlight console %}
greg@Gregs-iMac tutorial3-lottery-auditor % mvn compile exec:java
[INFO] Scanning for projects...
[INFO]
[INFO] --- fluxtion:3.0.14:springToFluxtion (spring to fluxtion builder) @ getting-started-tutorial3 ---
[main] INFO com.fluxtion.compiler.generation.compiler.EventProcessorCompilation - generated EventProcessor file: /development/fluxtion-examples/getting-started/tutorial3-lottery-auditor/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
{% endhighlight %}

# Running the application
[Configuration for SLf4j]({{site.getting_started}}/{{page.slf4j_config}}) is provided in the resources directory that will publish the audit records
to a file called myAudit.log. In production, it is likely the audit log will be published to a separate file that has no
other output.

{% highlight properties %}
org.slf4j.simpleLogger.logFile=myAudit.log
org.slf4j.simpleLogger.defaultLogLevel=INFO
org.slf4j.simpleLogger.showDateTime=false
org.slf4j.simpleLogger.showLogName=false
org.slf4j.simpleLogger.showThreadName=false
org.slf4j.simpleLogger.log.org.apache.velocity.deprecation=ERROR
{% endhighlight %}

## Audit output

Below is the capture of the audit output for our application

{% highlight yaml %}

eventLogRecord: 
    eventTime: 1714489211055
    logTime: 1714489211055
    groupingId: null
    event: LifecycleEvent
    eventToString: Start
    nodeLogs: 
        - ticketStore: { ticketSalesPublisher: valid, storeOpen: false}
        - lotteryMachine: { resultPublisher: valid}
    endTime: 1714489211055
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211079
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { rejectTicket: true, storeOpen: false}
    endTime: 1714489211079
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211079
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.openStore()
    thread: main
    nodeLogs: 
        - ticketStore: { storeOpen: true}
    endTime: 1714489211079
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211079
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { ticketPurchased: Ticket[number=126556, id=4ae79f78-e276-4d36-b94e-effece4f8ba1]}
        - lotteryMachine: { ticketsSold: 1}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { ticketPurchased: Ticket[number=365858, id=310108a0-ced5-4ed5-bcad-93a2ab3cab06]}
        - lotteryMachine: { ticketsSold: 2}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { ticketPurchased: Ticket[number=730012, id=043338f1-11c0-49c5-b132-93ec2b56348c]}
        - lotteryMachine: { ticketsSold: 3}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { rejectTicket: true, badTicket: Ticket[number=25, id=206b57a8-c374-44be-8b8a-f43146626b9b], invalidNumber: 25}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.closeStore()
    thread: main
    nodeLogs: 
        - ticketStore: { storeOpen: false}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
    thread: main
    nodeLogs: 
        - ticketStore: { rejectTicket: true, storeOpen: false}
    endTime: 1714489211090
---
eventLogRecord: 
    eventTime: -1
    logTime: 1714489211090
    groupingId: null
    event: ExportFunctionAuditEvent
    eventToString: public void com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode.selectWinningTicket()
    thread: main
    nodeLogs: 
        - lotteryMachine: { winningTicket: Ticket[number=365858, id=310108a0-ced5-4ed5-bcad-93a2ab3cab06]}
    endTime: 1714489211090
---


{% endhighlight %}

# Conclusion
In this tutorial we have seen how event auditing can be added to the container, user code can write key/value pairs and 
a custom event auditor can be injected into the container. With very little effort the following benefits are
realised:

- Tracing through a complex system can be recorded for later analysis
- Event audit provides a valuable tool for fault-finding in production and development
- Custom auditors control how and where we separate our tracing records from normal text based logs
- Event auditing can be used in systems that require proof processing requirements have been met

I hope you have enjoyed reading this tutorial, and it has given you a desire to adding event auditing to your applications
. Please send me any comments or suggestions to improve this tutorial

[next tutorial 6](tutorial-6)
{: .text-right }