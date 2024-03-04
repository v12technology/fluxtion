---
title: 3rd tutorial - auditor
parent: Getting started
has_children: false
nav_order: 3
published: true
auditor_src: tutorial3-lottery-auditor/src/main/java/com/fluxtion/example/cookbook/lottery/auditor/SystemStatisticsAuditor.java
processor_src: tutorial3-lottery-auditor/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java
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
This tutorial is an introduction to monitoring the dependency injection container at runtime, we call this **auditing**. 
The reader should be proficient in Java, maven, git, Spring and have completed the [second lottery tutorial](tutorial-2.md) before 
starting this tutorial. The project source can be found [here.]({{site.getting_started}}/tutorial3-lottery-auditor)

Our goal is to create a custom monitoring class that will observe the event processing without any changes to the 
application code, and record the following statistics:

- Node stats, method invocation count grouped by bean instance
- Event stats, method invocation count grouped by exported method
- Node method stats, method invocation count grouped by bean instance and method

At the end of this tutorial you should understand:

- The role of the Auditor interface in Fluxtion
- How the auditor is notified by the container
- How to implement a custom auditor
- How to load a custom auditor in the container


# Auditing concepts
As event driven systems grow more complex tools are needed to monitor critical application metrics giving early
warning of potential problems. It is preferable to develop monitoring separately to application code so that we do not
accidentally introduce bugs to business functionality, and we can add or remove monitoring to the application without 
changing functional behaviour.

In Fluxtion we achieve this through the **[Auditor]({{site.fluxtion_src_runtime}}/audit/Auditor.java)** interface. 
Client code implements the Auditor interface and register an auditor instance at build time. At runtime the custom
auditor receives monitoring notifications with event metadata attached. The auditor is free to process the event 
metadata in any way it wants.

## Auditor notifications
The [Auditor]({{site.fluxtion_src_runtime}}/audit/Auditor.java) interface is copied below with javadoc comments removed

{% highlight java %}
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;

public interface Auditor extends Lifecycle {
  default boolean auditInvocations() {
    return false;
  }
  @Override
  default void init() {}
  void nodeRegistered(Object node, String nodeName);
  default void eventReceived(Event event) {}
  default void eventReceived(Object event) {}
  default void nodeInvoked(Object node, String nodeName, String methodName, Object event) {}
  default void processingComplete() {}
  @Override
  default void tearDown() {}
}
{% endhighlight %}

Methods in the interface are called by the container at runtime except auditInvocations, which is called at build time.
The callback nodeInvoked is a high frequency notification called on every method invoked on every bean, 
auditInvocations controls whether this monitoring method is notified at runtime.

Javadoc is attached to the class for detail reading, but it is important to understand these concepts:

-  **init** called once before any other lifecycle methods
-  **nodeRegistered** called after init before any lifecycle methods, allows the auditor to build a map of beans at startup
-  **eventReceived** called when a service call or event is received by the container. Precedes any bean method calls
-  **nodeInvoked** called before any bean service or trigger method in an event processing cycle
-  **processingComplete** called at the end of a processing cycle
-  **tearDown** called once when the container tearDown lifecycle method is called

## Loading an auditor into the container
An auditor is bound into the container at build time using the [EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java)
instance that is provided in one of the overloaded Fluxtion build methods. The auditor instance must be registered with
a name that is unique for the container.

{% highlight java %}
eventProcessorConfig.addAuditor(new SystemStatisticsAuditor(), "lotteryAuditor");
{% endhighlight %}

# Implementing the auditor
The [SystemStatisticsAuditor]({{site.getting_started}}/{{page.auditor_src}}) implements Auditor and calculates the 
monitoring statistics we want to report. The exported service method publishStats creates the report and publishes it 
to the console. For our auditor to be useful it needs to interact with the outside world, in this example we provide 
two ways for publishStats to be called.

1. SystemStatisticsAuditor implements the lifecycle tearDown method that chains a call to publishStats() on shutdown
2. SystemStatisticsAuditor exports the service SystemMonitor, application code can locate the auditor's exported service and call publishStats on demand


{% highlight java %}
public class SystemStatisticsAuditor implements Auditor, @ExportService SystemMonitor {
  private transient final Map<Object, Stats> nodeStats = new IdentityHashMap<>();
  private transient final Map<String, Stats> eventStats = new HashMap<>();
  private transient final Map<String, Stats> methodStats = new HashMap<>();

    @Override
    public void nodeRegistered(Object o, String s) {
        nodeStats.put(o, new Stats(s));
    }

    @Override
    public void eventReceived(Object event) {
        updateEventStats(event);
    }

    @Override
    public void eventReceived(Event event) {
        updateEventStats(event);
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
        nodeStats.computeIfPresent(node, (n, s) -> s.incrementCallCount());
        String name = nodeName + "#" + methodName;
        methodStats.compute(name, (n, s) ->{
            s = s == null ? new Stats(n) : s;
            s.incrementCallCount();
            return s;
        });
    }

    @Override
    public void tearDown() {
        publishStats();
    }

    @Override
    public boolean auditInvocations() {
        return true;
    }

    @Override
    public void publishStats() {
        System.out.println(
                nodeStats.values().stream()
                        .sorted(Comparator.comparing(Stats::getCount))
                        .map(Stats::report)
                        .collect(Collectors.joining("\n\t", "Node stats:\n\t", ""))
        );
        System.out.println(
                eventStats.values().stream()
                        .sorted(Comparator.comparing(Stats::getCount))
                        .map(Stats::eventReport)
                        .collect(Collectors.joining("\n\t", "Event stats:\n\t", ""))
        );
        System.out.println(
                methodStats.values().stream()
                        .sorted(Comparator.comparing(Stats::getCount))
                        .map(Stats::methodReport)
                        .collect(Collectors.joining("\n\t", "Node method stats:\n\t", ""))
        );
    }

    private void updateEventStats(Object event){
        String name = event.getClass().getSimpleName();
        if(event instanceof ExportFunctionAuditEvent func){
            name = func.toString();
        }
        eventStats.compute(name, (c, s) ->{
            s = s == null ? new Stats(c) : s;
            s.incrementCallCount();
            return s;
        });
    }

    @Data
    public static final class Stats {
        private final String name;
        private int count;

        public Stats incrementCallCount() {
            count++;
            return this;
        }

        public String report() {
            return "node:" + name + ", invokeCount:" + count;
        }

        public String eventReport() {
            return "event:" + name + ", invokeCount:" + count;
        }

        public String methodReport() {
            return "method:" + name + ", invokeCount:" + count;
        }
    }
}
{% endhighlight %}

We won’t discuss the calculation in detail, but important points to note are how the monitoring callbacks are used to 
drive the statistics we want to capture.

- **nodeRegistered** - builds an IdentityHashMap of node instances that will be used to record node stats
- **eventReceived** - calculates the client to container event call statistics
- **nodeInvoked** - builds the method invocation statistics for a node

# Building the application
As Fluxtion is in aot mode the serialised [LotteryProcessor]({{site.getting_started}}/{{page.processor_src}}) can be inspected
to locate where the notification callbacks to the auditor are injected. 

A [FluxtionSpringConfig]({{site.fluxtion_src_compiler}}/extern/spring/FluxtionSpringConfig.java) bean is added to the spring 
config file that references the SystemStatisticsAuditor we want to include in the event processor. A specialised handler 
for FluxtionSpringConfig customises the generated event processor by calling methods on 
the [EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java) at build time.


{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode">
    </bean>

    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>

    <!--AUDITORS-->
    <bean id="systemAuditor" class="com.fluxtion.example.cookbook.lottery.auditor.SystemStatisticsAuditor"/>
    <bean class="com.fluxtion.compiler.extern.spring.FluxtionSpringConfig">
        <property name="auditors">
            <list>
                <ref bean="systemAuditor"/>
            </list>
        </property>
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
The lottery application has a few changes from the second tutorial:

-  LotteryEventProcessor instance is assigned to a member variable
-  App code calls LotterySystemMonitor::publishStats on the exported service during execution
-  Teardown of the container is called, lotteryEventProcessor.tearDown(), that forces a final stats publication from the auditor
-  Logging has been removed from the beans TicketStoreNode and LotteryMachineNode to reduce output

Our updated LotteryApp looks like this:

{% highlight java %}
public class LotteryApp {
    //code removed for clarity ...

    //Now a member variable 
    private static LotteryProcessor lotteryEventProcessor;

    public static void main(String[] args) {
        start(LotteryApp::ticketReceipt, LotteryApp::lotteryResult);
        //try and buy a ticket - store is closed
        ticketStore.buyTicket(new Ticket(12_65_56));

        //open store and buy ticket
        ticketStore.openStore();
        ticketStore.buyTicket(new Ticket(12_65_56));

        //print stats
        lotteryEventProcessor.consumeServiceIfExported(LotterySystemMonitor.class, LotterySystemMonitor::publishStats);

        //code removed for clarity ...

        //teardown - should print stats
        lotteryEventProcessor.tearDown();
    }

    public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
        lotteryEventProcessor = new LotteryProcessor();
        //code removed for clarity ...
    }

}
{% endhighlight %}

Executing our application produces different output from the second tutorial. The statistics output from the auditor
is published twice to the console, one driven by user code and the other by teardown lifecycle.

{% highlight console %}
27-Sept-23 21:10:07 [main] INFO LotteryApp - store shut - no tickets can be bought
27-Sept-23 21:10:07 [main] INFO LotteryApp - good luck with Ticket[number=126556, id=07a90af9-9ea7-4081-b27d-3670e8e98e19]

-------------------------------------------------------------------------------------------
NODE STATS START
-------------------------------------------------------------------------------------------
Node stats:
  node:context, invokeCount:0
  node:callbackDispatcher, invokeCount:0
  node:subscriptionManager, invokeCount:0
  node:lotteryMachine, invokeCount:2
  node:ticketStore, invokeCount:4
Event stats:
  event:public void com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode.setResultPublisher(java.util.function.Consumer<java.lang.String>), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.auditor.SystemStatisticsAuditor.publishStats(), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.setTicketSalesPublisher(java.util.function.Consumer<java.lang.String>), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.openStore(), invokeCount:1
  event:LifecycleEvent, invokeCount:2
  event:public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket), invokeCount:2
Node method stats:
  method:ticketStore#setTicketSalesPublisher, invokeCount:1
  method:lotteryMachine#processNewTicketSale, invokeCount:1
  method:ticketStore#openStore, invokeCount:1
  method:lotteryMachine#setResultPublisher, invokeCount:1
  method:systemAuditor#publishStats, invokeCount:1
  method:ticketStore#buyTicket, invokeCount:2
-------------------------------------------------------------------------------------------
NODE STATS END
-------------------------------------------------------------------------------------------

27-Sept-23 21:10:07 [main] INFO LotteryApp - good luck with Ticket[number=365858, id=1cf81163-70b7-40fa-b3df-e7f8891a334e]
27-Sept-23 21:10:07 [main] INFO LotteryApp - good luck with Ticket[number=730012, id=22f12028-6c95-4a07-9c03-3d7d1fc66e51]
27-Sept-23 21:10:07 [main] INFO LotteryApp - invalid numbers Ticket[number=25, id=c19846a1-bbf7-4045-9251-ed663025b111]
27-Sept-23 21:10:07 [main] INFO LotteryApp - store shut - no tickets can be bought
27-Sept-23 21:10:07 [main] INFO LotteryApp - winning numbers:126556

-------------------------------------------------------------------------------------------
NODE STATS START
-------------------------------------------------------------------------------------------
Node stats:
  node:context, invokeCount:0
  node:callbackDispatcher, invokeCount:0
  node:subscriptionManager, invokeCount:0
  node:lotteryMachine, invokeCount:5
  node:ticketStore, invokeCount:9
Event stats:
  event:public void com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode.setResultPublisher(java.util.function.Consumer<java.lang.String>), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.auditor.SystemStatisticsAuditor.publishStats(), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.closeStore(), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.setTicketSalesPublisher(java.util.function.Consumer<java.lang.String>), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.openStore(), invokeCount:1
  event:public void com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode.selectWinningTicket(), invokeCount:1
  event:LifecycleEvent, invokeCount:3
  event:public boolean com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode.buyTicket(com.fluxtion.example.cookbook.lottery.api.Ticket), invokeCount:6
Node method stats:
  method:lotteryMachine#selectWinningTicket, invokeCount:1
  method:ticketStore#closeStore, invokeCount:1
  method:ticketStore#setTicketSalesPublisher, invokeCount:1
  method:ticketStore#openStore, invokeCount:1
  method:lotteryMachine#setResultPublisher, invokeCount:1
  method:systemAuditor#publishStats, invokeCount:1
  method:lotteryMachine#processNewTicketSale, invokeCount:3
  method:ticketStore#buyTicket, invokeCount:6
-------------------------------------------------------------------------------------------
NODE STATS END
-------------------------------------------------------------------------------------------
{% endhighlight %}

The detailed breakdown of the stats are for the reader to analyse, but you should be able to see that detailed
calculations can be carried out by the auditor to assess how the user code is being used at runtime by the container.
For the lotteryMachine instance the teardown stats tell us:

- The lotteryMachine node was invoked 5 times
  - lotteryMachine#selectWinningTicket, invokeCount:1
  - lotteryMachine#setResultPublisher, invokeCount:1
  - lotteryMachine#processNewTicketSale, invokeCount:3

- The external events that called the lotteryMachine
  - LotteryMachineNode.setResultPublisher(java.util.function.Consumer<java.lang.String>), invokeCount:1
  - LotteryMachineNode.selectWinningTicket(), invokeCount:1

# Conclusion
In this tutorial we have seen how a custom auditor can be injected into the container and used to monitor runtime 
performance without any changes required to the business code. With very little effort the following benefits are
realised:

- Applications can be monitored at runtime without any changes to app code
- A range of auditors with specific goals can be developed and re-used across multiple applications
- Auditors bound to the container can easily be changed independently of application code
- Alerts can be published from auditors if necessary giving early warning of problems
- The impact of auditing in aot mode is very low as it is statically compiled into the container

I hope you have enjoyed reading this tutorial, and it has given you a desire to try adding auditing to your applications
. Please send me any comments or suggestions to improve this tutorial

[next tutorial 4](tutorial-4.md)
{: .text-right }