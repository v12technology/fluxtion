---
title: 1st tutorial - Spring
parent: Developer tutorials
has_children: false
nav_order: 2
published: true
---

# 1st tutorial - Spring
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

This tutorial is an introduction to writing event driven application logic using Fluxtion and Spring. The reader should be
proficient in Java, maven, git and possess a basic knowledge of Spring dependency injection. Spring is a very popular 
dependency injection container, this example demonstrates Fluxtion Spring integration.

Our goal is to build the logic for a simple lottery application that will be connected to request and response queues.

- Logic resides in user classes and functions
- Spring config declares which beans to wire together
- Fluxtion creates an event processor that manages the beans and dispatches events to the correct bean instance

This example is focused on building event driven processing logic and not the connection to real queues.

At the end of this tutorial you should understand how Fluxtion:

- Exposes service interfaces for managed components
- Calls lifecycle methods on managed components
- Triggers event logic between dependent components
- Wires components together using Spring configuration


# Example project
The [example project]({{site.getting_started}}/tutorial1-lottery) is referenced in this tutorial.

# The Lottery game

A lottery game sells tickets to customers from a ticket shop, the shop is either opened or closed. A customer receives a
receipt for a purchased ticket or a message that no ticket was purchased. Tickets must have six numbers and cannot be
bought when the shop is closed. A lottery machine picks the winning ticket number from the tickets purchased and
publishes the lucky number to a queue.

# Designing the components

Our application will be event driven through a service interface api for the outside world to code against. We must first 
think about the design of our services and then the concrete implementations. Once this design is complete we will use
Fluxtion to wire up the components. Fluxtion is low touch allowing engineers and architects to concentrate on design and 
components with no distraction.

## Processing logic
{: .no_toc }
Our design sketches show what we intend to integrate into our system

```mermaid

flowchart TB
    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}

    buyTicket><b>ServiceCalls</b>\n buyTicket, openStore, closeStore, setTicketSalesPublisher]:::eventHandler
    selectWinningTicket><b>ServiceCalls</b>\n selectWinningTicket, setResultPublisher]:::eventHandler


    LotteryMachine([<b>ServiceLookup</b>::LotteryMachine]):::exportedService
    TicketStore([<b>ServiceLookup</b>::TicketStore]):::exportedService
    
    TicketStoreNode[TicketStoreNode\n <b>ExportService</b>::TicketStore]:::graphNode
    LotteryMachineNode[LotteryMachineNode\n <b>ExportService</b>::LotteryMachine]:::graphNode

    selectWinningTicket ---> LotteryMachine
    buyTicket --> TicketStore
    
    LotteryMachine --> LotteryMachineNode
    TicketStore ---> TicketStoreNode
 
    subgraph EventProcessor
        TicketStoreNode --> LotteryMachineNode
    end

```
## Spring config
Spring config for our lottery application

{% highlight xml %}
<beans xmlns="">
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode"/>
    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>
</beans>
{% endhighlight %}

## Service api

From our business problem we have identified a concrete data type Ticket and two public services TicketStore and 
LotteryMachine. Now we have identified the top level concepts we can create a service api that client code will use to 
drive the system.

{% highlight java %}
public record Ticket(int number, UUID id) {
    public Ticket(int number){
        this(number, UUID.randomUUID());
    }
}

public interface TicketStore {
    boolean buyTicket(Ticket ticket);
    void openStore();
    void closeStore();
    void setTicketSalesPublisher(Consumer<String> ticketSalesPublisher);
}

public interface LotteryMachine {
    void selectWinningTicket();
    void setResultPublisher(Consumer<String> resultPublisher);
}
{% endhighlight %}

Our interfaces separate concerns logically making the api simple to work with. The methods
setTicketSalesPublisher and setResultPublisher connect the results of processing to output queues or a unit test. One
of our goals is to make the logic easy to test with the minimum of infrastructure.

## Implementing logic

We implement our two interfaces with concrete classes TicketStoreNode and LotteryGameNode using some lombok annotations
to remove boilerplate code. 

### TicketStoreNode

The TicketStoreNode implements TicketSore and supports logic to buy and sell tickets depending on the state of the store
. A lifecycle method start is created that checks the ticketSalesPublisher has been set before progressing any further.
TicketStoreNode also implements Supplier&lt;Ticket&gt; which allows any child component to access the last sold ticket without
accessing the concrete type. Making components reference each other through interfaces is good practice.

{% highlight java %}
@Slf4j
public class TicketStoreNode implements Supplier<Ticket>, TicketStore {

    private boolean storeOpen;
    private Consumer<String> ticketSalesPublisher;
    private Ticket ticket;

    @Override
    public void setTicketSalesPublisher(Consumer<String> ticketSalesPublisher) {
        this.ticketSalesPublisher = ticketSalesPublisher;
    }

    public void start() {
        Objects.requireNonNull(ticketSalesPublisher, "must have a ticketSalesPublisher set");
        storeOpen = false;
    }

    @Override
    public boolean buyTicket(Ticket ticket) {
        if (ticket.number() < 9_99_99 | ticket.number() > 99_99_99) {
            ticketSalesPublisher.accept("invalid numbers " + ticket);
            this.ticket = null;
        } else if (storeOpen) {
            ticketSalesPublisher.accept("good luck with " + ticket);
            this.ticket = ticket;
        } else {
            ticketSalesPublisher.accept("store shut - no tickets can be bought");
            this.ticket = null;
        }
        return this.ticket != null;
    }

    @Override
    public Ticket get() {
        return ticket;
    }

    @Override
    public void openStore() {
        log.info("store opened");
        storeOpen = true;
    }

    @Override
    public void closeStore() {
        log.info("store closed");
        storeOpen = false;
    }
}
{% endhighlight %}

### LotteryGameNode

The LotteryMachineNode implements LotteryMachine and supports logic to run the lottery. LotteryMachineNode holds a reference to 
an instance of Supplier&lt;Ticket&gt; and whenever processNewTicketSale is called, acquires a purchased ticket and adds it 
to the internal cache. A lifecycle method start is created that checks the resultPublisher has been set before 
progressing any further. 

{% highlight java %}
@Slf4j
@RequiredArgsConstructor
public class LotteryMachineNode implements LotteryMachine {

    private final Supplier<Ticket> ticketSupplier;
    private final transient List<Ticket> ticketsBought = new ArrayList<>();
    private Consumer<String> resultPublisher;

    @Override
    public void setResultPublisher(Consumer<String> resultPublisher) {
        this.resultPublisher = resultPublisher;
    }

    public void start(){
        Objects.requireNonNull(resultPublisher, "must set a results publisher before starting the lottery game");
        log.info("started");
    }

    public boolean processNewTicketSale() {
        ticketsBought.add(ticketSupplier.get());
        log.info("tickets sold:{}", ticketsBought.size());
        return false;
    }

    @Override
    public void selectWinningTicket() {
        if(ticketsBought.isEmpty()){
            log.info("no tickets bought - no winning ticket");
        }else {
            Collections.shuffle(ticketsBought);
            log.info("WINNING ticket {}", ticketsBought.get(0));
        }
        ticketsBought.clear();
    }
}
{% endhighlight %}

The lifecycle methods and how clients access the TicketStore and LotteryMachine services are described below.

# Building the application
Now we have our service interfaces designed and implemented we need to connect components together and make sure they provide
the functionality required in the expected manner. There are several problems to solve to deliver correct event driven 
functionality:

- How do clients access the components via service interfaces
- How are the lifecycle methods called
- How is LotteryGameNode#processNewTicketSale called only when a ticket is successfully purchased
- How are the components wired together

Fluxtion solves these four problems for any event driven application. 

## Exporting services
We want clients to access components via service interface, this is simple to achieve by adding an **@ExportService** 
annotation to the interface definitions on the concrete classes, as shown below.

{% highlight java %}
import com.fluxtion.runtime.annotations.ExportService;
public class LotteryMachineNode implements @ExportService LotteryMachine {
  //removed for clarity
}

import com.fluxtion.runtime.annotations.ExportService;
public class TicketStoreNode implements Supplier<Ticket>, @ExportService TicketStore {
  //removed for clarity
}
{% endhighlight %}

Fluxtion will only export annotated interfaces at the container level, in this case Fluxtion will not export the
Supplier&lt;Ticket&gt; interface that TicketStoreNode implements.

## Accessing exported services
Once the service interface has been marked for export client code can locate it through the EventProcessor instance that
holds the application components by calling EventProcessor#getExportedService. Client code invokes methods on the 
interface and Fluxtion container will take care of all method routing.

{% highlight java %}
public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
    EventProcessor lotteryEventProcessor = //removed for clarity
    LotteryMachine lotteryMachine = lotteryEventProcessor.getExportedService();
    TicketStore ticketStore = lotteryEventProcessor.getExportedService(); 
    lotteryMachine.setResultPublisher(resultsPublisher);
    ticketStore.setTicketSalesPublisher(ticketReceiptHandler);
}
{% endhighlight %}


## Event dispatch

When a ticket has been successfully purchased the LotteryMachineNode instance method processNewTicketSale is invoked by 
Fluxtion. The processNewTicketSale method grabs the last ticket sale from the Supplier&lt;Ticket&gt; reference and adds it to 
the cache. Fluxtion knows to trigger a method if it is annotated with **@OnTrigger** and one of its dependencies has been
triggered from an incoming client service call.


{% highlight java %}
public class LotteryMachineNode implements LotteryMachine {
  //code removed for clarity

  @OnTrigger
  public boolean processNewTicketSale() {
    ticketsBought.add(ticketSupplier.get());
    log.info("tickets sold:{}", ticketsBought.size());
    return false;
  }
}
{% endhighlight %}

How does Fluxtion know to invoke this method at the correct time? The container maps the dependency relationship between
TicketStoreNode and LotteryMachineNode, so when an exported service method is invoked on TicketStoreNode Fluxtion calls
the processNewTicketSale trigger method on LotteryMachineNode. This is great as it removes the need for the programmer 
to manually call the event dispatch call graph. 

The next problem is we only want the processNewTicketSale method called when a ticket is successfully purchased. If we
try to add a ticket when the openStore is called a null pointer exception will be thrown at runtime. How can the 
developer control the propagation of dependent trigger methods? 

Fluxtion manages exported service event propagation in two ways:

- boolean return type from the service method, false indicates no event propagation, true propagates the notification
- annotate the method with **@NoPropagateFunction** annotation

Both propagation controls are used in LotteryMachineNode ensuring the LotteryMachine is only triggered on successful 
ticket purchases. The TicketStoreNode#buyTicket is the only method that will trigger an event notification to 
LotteryMachineNode and only if the ticket passes basic validation and the store is open.

{% highlight java %}
public class TicketStoreNode implements Supplier<Ticket>, @ExportService TicketStore {
    //code removed for clarity
    
    @Override
    @NoPropagateFunction
    public void setTicketSalesPublisher(Consumer<String> ticketSalesPublisher) {}
    
    public void start() {}
    
    //return true -> triggers event propagation
    public boolean buyTicket(Ticket ticket) {
      if (ticket.number() < 9_99_99 | ticket.number() > 99_99_99) {
          ticketSalesPublisher.accept("invalid numbers " + ticket);
          this.ticket = null;
      } else if (storeOpen) {
          ticketSalesPublisher.accept("good luck with " + ticket);
          this.ticket = ticket;
      } else {
          ticketSalesPublisher.accept("store shut - no tickets can be bought");
          this.ticket = null;
      }
      return this.ticket != null;
    }
    
    public Ticket get() {}
    
    @NoPropagateFunction
    public void openStore() {}
    
    @NoPropagateFunction
    public void closeStore() {}
}
{% endhighlight %}


## Lifecycle methods
Applications often benefit from lifecycle methods such as init, start and stop, allowing checks to be carried out before
executing the application. Fluxtion supports init, start and stop by annotating a method with an annotation **@Start @Stop**
or **@Initialise**. We use
the start method in our application to check output receivers ticketSalesPublisher and resultPublisher have been set 
by the client code.


{% highlight java %}
public class TicketStoreNode implements Supplier<Ticket>, @ExportService TicketStore {
  //code removed for clarity
    @Start
    public void start() {
        Objects.requireNonNull(ticketSalesPublisher, "must have a ticketSalesPublisher set");
        storeOpen = false;
    }
}

public class LotteryMachineNode implements @ExportService LotteryMachine {
  //code removed for clarity
    @Start
    public void start(){
        Objects.requireNonNull(resultPublisher, "must set a results publisher before starting the lottery game");
        log.info("started");
    }
}
{% endhighlight %}

Client code invokes the lifecycle method on the container Fluxtion then calls all the lifecycle methods registered
by components in the right order.

{% highlight java %}
public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
    EventProcessor lotteryEventProcessor = //removed for clarity
    lotteryEventProcessor.init();
    LotteryMachine lotteryMachine = lotteryEventProcessor.getExportedService();
    TicketStore ticketStore = lotteryEventProcessor.getExportedService();
    lotteryMachine.setResultPublisher(resultsPublisher);
    ticketStore.setTicketSalesPublisher(ticketReceiptHandler);
    lotteryEventProcessor.start();
}
{% endhighlight %}

## Wiring the components together
The dependency injection container wires components depending upon the configuration supplied. As Fluxtion natively supports  
spring ApplicationContext we use a spring configuration file in this example to wire the TicketStore to the LotteryMachine.
We are using Spring in these tutorials because of its familiarity to readers, spring is not required by Fluxtion when using
other methods to specify container managed beans.

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="">
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode"/>

    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>
</beans>
{% endhighlight %}

Fluxtion provides a spring extension for building a container using static helper methods. The built container is free
of any spring dependencies, Fluxtion just reads the spring file to drive its own configuration. To build the container
the tutorial loads the spring file from the classpath:

{% highlight java %}
public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
  EventProcessor lotteryEventProcessor = FluxtionSpring.interpret(
    new ClassPathXmlApplicationContext("com/fluxtion/example/cookbook/lottery/spring-lottery.xml"));
  //removed for clarity
}
{% endhighlight %}

## Build system
The example use maven to build the application, the Fluxtion runtime dependency is pulled in transitively via the 
compiler. Lombok is added to reduce boilerplate code, spring-context enables reading the spring config file, 
both of these dependencies are optional in vanilla Fluxtion usage.

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.fluxtion.example</groupId>
<artifactId>getting-started-tutorial1</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>jar</packaging>
<name>getting-started :: tutorial 1 :: lottery</name>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <fluxtion.version>{{site.fluxtion_version}}</fluxtion.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>${fluxtion.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.26</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.29</version>
        </dependency>
    </dependencies>
</project>
{% endhighlight %}

# Running the application
Running the application requires the following from client code:

-  Building the container using the spring config file 
-  Call lifecycle methods on the container
-  Lookup container exported service interfaces and store the references for use in client code

The fact the components are managed by a container is completely hidden from the client code, this
makes integrating Fluxtion into an existing system extremely simple as no new programming models need to be adopted.

In our example the main method only interacts with the business logic via the service interfaces, in a real application
the methods would be invoked by taking commands from an incoming request queue.

{% highlight java %}
public class LotteryApp {

    private static LotteryMachine lotteryMachine;
    private static TicketStore ticketStore;

    public static void main(String[] args) {
        start(LotteryApp::ticketReceipt, LotteryApp::lotteryResult);
        //try and buy a ticket - store is closed
        ticketStore.buyTicket(new Ticket(12_65_56));

        //open store and buy ticket
        ticketStore.openStore();
        ticketStore.buyTicket(new Ticket(12_65_56));
        ticketStore.buyTicket(new Ticket(36_58_58));
        ticketStore.buyTicket(new Ticket(73_00_12));

        //bad numbers
        ticketStore.buyTicket(new Ticket(25));

        //close the store and run the lottery
        ticketStore.closeStore();

        //try and buy a ticket - store is closed
        ticketStore.buyTicket(new Ticket(12_65_56));

        //run the lottery
        lotteryMachine.selectWinningTicket();
    }

    public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
        var lotteryEventProcessor = FluxtionSpring.interpret(
                new ClassPathXmlApplicationContext("com/fluxtion/example/cookbook/lottery/spring-lottery.xml"));
        lotteryEventProcessor.init();
        lotteryMachine = lotteryEventProcessor.getExportedService();
        ticketStore = lotteryEventProcessor.getExportedService();
        lotteryMachine.setResultPublisher(resultsPublisher);
        ticketStore.setTicketSalesPublisher(ticketReceiptHandler);
        lotteryEventProcessor.start();
    }

    public static void ticketReceipt(String receipt){
        log.info(receipt);
    }

    public static void lotteryResult(String receipt){
        log.info(receipt);
    }
}
{% endhighlight %}

Executing our application produces the following output:

{% highlight console %}
[main] INFO LotteryMachineNode - started
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO TicketStoreNode - store opened
[main] INFO LotteryApp - good luck with Ticket[number=126556, id=77376783-3513-4f22-88be-5ace6cdf5839]
[main] INFO LotteryMachineNode - tickets sold:1
[main] INFO LotteryApp - good luck with Ticket[number=365858, id=05e6f44e-5938-4b28-a183-047c6e75c532]
[main] INFO LotteryMachineNode - tickets sold:2
[main] INFO LotteryApp - good luck with Ticket[number=730012, id=30af94d7-7aec-4e82-8159-2cade3b38b2b]
[main] INFO LotteryMachineNode - tickets sold:3
[main] INFO LotteryApp - invalid numbers Ticket[number=25, id=62afdb45-25f8-4a80-bfba-37c22bfe8bf2]
[main] INFO TicketStoreNode - store closed
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO LotteryMachineNode - WINNING ticket Ticket[number=365858, id=05e6f44e-5938-4b28-a183-047c6e75c532]

Process finished with exit code 0
{% endhighlight %}

# Conclusion
We have quite a lot of ground in a seemingly simple event driven application. Hopefully you can see the benefits of using 
Fluxtion to write event driven business logic:
-  Forcing client code to interact with components through interfaces
-  Formal lifecycle phases that are easy to plug in to
-  Dispatch of events to any bean managed instance exporting a service
-  Automatic dispatch of dependent trigger methods 
-  Removal of state and conditional dispatch logic from business code
-  Deterministic event handling that is well tested
-  Control of event propagation with simple boolean returns or annotations
-  More time spent writing logic and less time writing infrastructure
-  Simple programming model that leverages Spring for quick adoption

As applications grow and become more complex programming event driven logic becomes ever more expensive. The benefits 
of a tool like Fluxtion really shine during the growth and maintenance phase.

I hope you have enjoyed reading this tutorial, and it has given you an understanding of Fluxtion and a desire to use it 
in your applications. Please send me in any comments or suggestions to improve this tutorial

[next tutorial 2](tutorial-2.md)
{: .text-right }