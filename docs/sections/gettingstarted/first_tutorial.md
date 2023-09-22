---
title: First tutorial
parent: Getting started
has_children: false
nav_order: 1
published: true
---

# Introduction

This tutorial is an introduction to writing event driven application logic using Fluxtion. The reader should be
proficient in Java, maven, git and possess a basic knowledge of Spring dependency injection. The project source can be
found [here.]({{site.cookbook_src}}/lottery)

Our goal is to build the logic for a simple lottery application that will be connected to request and response queues.
Serialising requests to a queue makes our application event driven and easier to scale in the future, the response queue
stores the output from the application. This example is focused on building event driven processing by wiring together
software components using Fluxtion and not the connection to real queues.

At the end of this tutorial you should understand how Fluxtion:

- Exposes service interfaces for managed components
- Calls lifecycle methods on managed components
- Triggers event logic between dependent components
- Wires components together

# The Lottery game

A lottery game sells tickets to customers from a ticket shop, the shop is either opened or closed. A customer receives a
receipt for a purchased ticket or a message that no ticket was purchased. Tickets must have six numbers and cannot be
bought when the shop is closed. A lottery machine picks the winning ticket number from the tickets purchased and
publishes the lucky number to a queue.

# Designing the components

Our application will be service driven and present api interfaces for the outside world to code against. We must first 
think about the design of our services and then the concrete implementations. Once this design is complete we will use
Fluxtion to wire up the components. Fluxtion is low touch allowing engineers and architects to concentrate on components.

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
TicketStoreNode also implements Supplier<Ticket> which allows any child component to access the last sold ticket without
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
an instance of Supplier<Ticket> and whenever processNewTicketSale is called, acquires a purchased ticket and adds it 
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

The lifecycle methods and how clients access the TicketStore interface are described later on.

# Building the application
Now we have our service interfaces designed and implemented we need to connect components together and make sure they provide
the functionality required in the expected manner. There are several problems to solve to deliver functionality:

- How do clients access the components via service interfaces
- How are the lifecycle methods called
- How is LotteryGameNode#processNewTicketSale called only when a ticket is successfully purchased
- How are the components wired together

Fluxtion solves these four problems for any event driven application. 

## Exporting services
We want clients to access components via service interface, this is simple to achieve by adding an **ExportService** 
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
Supplier interface that TicketStoreNode implements.

## Accessing exported services
Once the service interface has been marked for export client code can locate it through the EventProcessor instance that
holds the application components by calling EventProcessor#getExportedService. Client code invokes methods on the 
interface and Fluxtion container will take care of all method routing.

{% highlight java %}
public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
  EventProcessor lotteryEventProcessor = FluxtionSpring.interpret(
      new ClassPathXmlApplicationContext("com/fluxtion/example/cookbook/lottery/spring-lottery.xml"));
  LotteryMachine lotteryMachine = lotteryEventProcessor.getExportedService();
  TicketStore ticketStore = lotteryEventProcessor.getExportedService(); 
  lotteryMachine.setResultPublisher(resultsPublisher);
  ticketStore.setTicketSalesPublisher(ticketReceiptHandler);
}
{% endhighlight %}

## Event dispatch

## Wiring the components together

# Running the application


