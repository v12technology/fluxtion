---
title: 4th tutorial - event tracing
parent: Getting started
has_children: false
nav_order: 4
published: true
auditor_src: tutorial4-lottery-auditlog/src/main/java/com/fluxtion/example/cookbook/lottery/auditor/SystemStatisticsAuditor.java
slf4j_config: tutorial4-lottery-auditlog/src/main/resources/simplelogger.properties
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

Our goal is to write a log file that traces the event call graph as events are processed 

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
Fluxtion produces a structured log output, known as a **[LogRecord]({{site.fluxtion_src_runtime}}/audit/LogRecord.java)**, 
for each event processed. LogRecords are marshalled into yaml documents that are machine-readable and remove logging noise.
A LogRecord includes metadata for the input event, a list of elements that traces the node execution path and optional
key value pairs written by the user code, 

Sample log record for a single event

{% highlight yaml %}
28-Sept-23 18:14:02 [main] INFO FluxtionSlf4jAuditor - eventLogRecord:
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

## Binding 
Event auditing is bound in to the container at build time using the auditor framework. The [EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java) provides
utility methods to add event audit support for easier programming:

{% highlight java %}
eventProcessorConfig.addEventAudit()
{% endhighlight %}

## Method tracing
By default, the nodeLogs element only contains elements where user code has written a key/value pair to the LogRecord.
If method tracing is enabled any node visited is added to nodeLogs regardless of user code. The log level that method 
that enables tracing is passed as an argument to addEventAudit

{% highlight java %}
eventProcessorConfig.addEventAudit(EventLogControlEvent.LogLevel.DEBUG)
{% endhighlight %}

For the above if the runtime event audit LogLevel for the container is set to DEBUG tracing will be enabled

## Runtime log level
The runtime event audit LogLevel can be controlled at the container level:

{% highlight javav %}
lotteryEventProcessor.setAuditLogLevel(EventLogControlEvent.LogLevel.DEBUG);
{% endhighlight %}

## Writing key/value pairs
User code can access the audit LogRecord at runtime via an  **[EventLogger]({{site.fluxtion_src_runtime}}/audit/EventLogger.java)** 
instance and write values that will appear in the nodeLogs element for this node. The container injects EventLogger
instances to any bean that implements **[EventLogSource]({{site.fluxtion_src_runtime}}/audit/EventLogSource.java)**. 
Extending **[EventLogNode]({{site.fluxtion_src_runtime}}/audit/EventLogNode.java)** gives access to an injected EventLogger,
writing values is simply:

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

# Adding event audit to the application
We are going to add a custom audit logger using SLf4J's simple logger to our application. First we create the custom
logger.

{% highlight java %}
@Slf4j
public class FluxtionSlf4jAuditor implements LogRecordListener {
    @Override
    public void processLogRecord(LogRecord logRecord) {
        log.info(logRecord.toString());
    }
}
{% endhighlight %}

## Add node logging
To add audit logging to our nodes we extend EventLogNode and use the injected EventLogger to write key/value pairs

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

## Adding audit logging to the container
Audit logging support is bound to the container at build time by calling addEventAudit on the supplied EventProcessorConfig
instance. The custom FluxtionSlf4jAuditor is injected to the built container using setAuditLogProcessor

{% highlight java %}
    public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
        lotteryEventProcessor = FluxtionSpring.interpret(
                new ClassPathXmlApplicationContext("/spring-lottery.xml"),
                c -> c.addEventAudit(EventLogControlEvent.LogLevel.INFO));
        lotteryEventProcessor.setAuditLogProcessor(new FluxtionSlf4jAuditor());
    }
{% endhighlight %}

# Running the application
[Configuration for SLf4j]({{site.getting_started}}/{{page.slf4j_config}}) is provided in the resources directory that will publish the audit records
to a file called myAudit.log. In production, it is likely the audit log will be published to a separate file that has no
other output.

{% highlight properties %}
org.slf4j.simpleLogger.logFile=myAudit.log
org.slf4j.simpleLogger.defaultLogLevel=INFO
org.slf4j.simpleLogger.showDateTime=false
org.slf4j.simpleLogger.dateTimeFormat=dd-MMM-yy HH:mm:ss
org.slf4j.simpleLogger.showShortLogName=true
org.slf4j.simpleLogger.log.org.apache.velocity.deprecation=ERROR
{% endhighlight %}

## Audit output

Below is the capture of the audit output for our application

{% highlight yaml %}
[main] INFO GenerationContext - classloader:jdk.internal.loader.ClassLoaders$AppClassLoader@251a69d7
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086107
groupingId: null
event: EventLogControlEvent
eventToString: EventLogConfig{level=null, logRecordProcessor=com.fluxtion.example.cookbook.lottery.auditor.FluxtionSlf4jAuditor@62833051, sourceId=null, groupId=null}
thread: main
nodeLogs:
endTime: 1696145086110
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086453
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract void com.fluxtion.example.cookbook.lottery.api.LotteryMachine.setResultPublisher(java.util.function.Consumer<java.lang.String>)
thread: main
nodeLogs:
- lotteryMachine: { thread: main, method: setResultPublisher}
endTime: 1696145086454
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086454
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract void com.fluxtion.example.cookbook.lottery.api.TicketStore.setTicketSalesPublisher(java.util.function.Consumer<java.lang.String>)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: setTicketSalesPublisher}
endTime: 1696145086454
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: 1696145086455
logTime: 1696145086455
groupingId: null
event: LifecycleEvent
eventToString: Start
nodeLogs:
- ticketStore: { ticketSalesPublisher: valid, storeOpen: false}
- lotteryMachine: { resultPublisher: valid}
endTime: 1696145086455
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086455
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, rejectTicket: true, storeOpen: false}
endTime: 1696145086455
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086455
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract void com.fluxtion.example.cookbook.lottery.api.TicketStore.openStore()
thread: main
nodeLogs:
- ticketStore: { thread: main, method: openStore, storeOpen: true}
endTime: 1696145086455
[main] INFO LotteryApp - good luck with Ticket[number=126556, id=e0a29b34-a515-4ac5-8312-025883eda477]
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086455
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, ticketPurchased: Ticket[number=126556, id=e0a29b34-a515-4ac5-8312-025883eda477]}
- lotteryMachine: { thread: main, method: processNewTicketSale, ticketsSold: 1}
endTime: 1696145086464
[main] INFO LotteryApp - good luck with Ticket[number=365858, id=17c8a462-8cd5-48cb-b3fa-64a589cf2591]
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086464
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, ticketPurchased: Ticket[number=365858, id=17c8a462-8cd5-48cb-b3fa-64a589cf2591]}
- lotteryMachine: { thread: main, method: processNewTicketSale, ticketsSold: 2}
endTime: 1696145086465
[main] INFO LotteryApp - good luck with Ticket[number=730012, id=cce29536-de90-47f9-ac96-2400d5d1083b]
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086465
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, ticketPurchased: Ticket[number=730012, id=cce29536-de90-47f9-ac96-2400d5d1083b]}
- lotteryMachine: { thread: main, method: processNewTicketSale, ticketsSold: 3}
endTime: 1696145086465
[main] INFO LotteryApp - invalid numbers Ticket[number=25, id=c96836c2-afc0-424b-ac72-ece88aa9a0d6]
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086465
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, rejectTicket: true, badTicket: Ticket[number=25, id=c96836c2-afc0-424b-ac72-ece88aa9a0d6], invalidNumber: 25}
endTime: 1696145086465
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086465
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract void com.fluxtion.example.cookbook.lottery.api.TicketStore.closeStore()
thread: main
nodeLogs:
- ticketStore: { thread: main, method: closeStore, storeOpen: false}
endTime: 1696145086465
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086465
groupingId: null
event: Exp[tutorial-3.md](tutorial-3.md)ortFunctionAuditEvent
eventToString: public abstract boolean com.fluxtion.example.cookbook.lottery.api.TicketStore.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket)
thread: main
nodeLogs:
- ticketStore: { thread: main, method: buyTicket, rejectTicket: true, storeOpen: false}
endTime: 1696145086465
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: -1
logTime: 1696145086466
groupingId: null
event: ExportFunctionAuditEvent
eventToString: public abstract void com.fluxtion.example.cookbook.lottery.api.LotteryMachine.selectWinningTicket()
thread: main
nodeLogs:
- lotteryMachine: { thread: main, method: selectWinningTicket, winningTicket: Ticket[number=126556, id=e0a29b34-a515-4ac5-8312-025883eda477]}
endTime: 1696145086466
[main] INFO FluxtionSlf4jAuditor - eventLogRecord:
eventTime: 1696145086466
logTime: 1696145086466
groupingId: null
event: LifecycleEvent
eventToString: TearDown
nodeLogs:
endTime: 1696145086466

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

[next tutorial 5](tutorial-5.md)
{: .text-right }