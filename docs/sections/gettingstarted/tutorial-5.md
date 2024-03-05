---
title: 5th tutorial - adding logic
parent: Getting started
has_children: false
nav_order: 5
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

This tutorial covers extending an existing application with new functionality and support for new event types. The reader 
should be proficient in Java, maven, git, Spring and have completed the [first lottery tutorial](tutorial-1.md) before starting
this tutorial. The project source can be found [here]({{site.getting_started}}/tutorial5-lottery-extended).

Our goal is to demonstrate how Fluxtion code generation makes it easy and cost-effective to add new functionality to 
an existing application with confidence.

At the end of this tutorial you should understand:

- How to add new event types and behaviours to user functions
- How to re-generate the container with new functionality

# Extending the lottery game
We will extend the lottery game to include a power lottery game and some additional reporting interfaces the application
can use. The new java classes are built in the same style as previous tutorials with annotations marking the 
callbacks. To bind the new classes into the event processor we update the spring config file and run the build 
to regenerate the [LotteryProcessor]({{site.getting_started}}/tutorial5-lottery-extended/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java)

## Spring config
The [spring config]({{site.getting_started}}/tutorial5-lottery-extended/src/main/resources/spring-lottery.xml) is 
updated to include the new beans in the event processor

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <bean id="ticketStore" class="com.fluxtion.example.cookbook.lottery.nodes.TicketStoreNode">
    </bean>
    <bean id="lotteryMachine" class="com.fluxtion.example.cookbook.lottery.nodes.LotteryMachineNode">
        <constructor-arg ref="ticketStore"/>
    </bean>
    <bean id="powerMachine" class="com.fluxtion.example.cookbook.lottery.nodes.PowerLotteryMachine">
        <constructor-arg ref="ticketStore"/>
    </bean>

    <bean id="gameReport" class="com.fluxtion.example.cookbook.lottery.nodes.GameReportNode">
        <constructor-arg index="0" ref="lotteryMachine"/>
        <constructor-arg index="1" ref="powerMachine"/>
    </bean>
</beans>
{% endhighlight %}


## Additional java classes
Our additional classes to add are:

{% highlight java %}
public interface GameResultStore {
    boolean isTicketSuccessful(Ticket ticket, Consumer<Boolean> responseReceiver);
    boolean publishReport(Consumer<String> reportReceiver);
}

public record WinningTicketReport(Ticket powerLotteryTicket, int winningNumbers) { }

@Slf4j
public class PowerLotteryMachine extends LotteryMachineNode implements @ExportService LotteryMachine {
    public PowerLotteryMachine(Supplier<Ticket> ticketSupplier) {
        super(ticketSupplier);
    }

    @Override
    public void selectWinningTicket() {
        //removed for clarity...
    }
}


public class GameReportNode implements @ExportService GameResultStore, @ExportService LotteryMachine {
    private final LotteryMachineNode lotteryMachine;
    private final PowerLotteryMachine powerLotteryMachine;
    private Consumer<String> resultPublisher;
    private int gameCount;

    public GameReportNode(
            @AssignToField("lotteryMachine") LotteryMachineNode lotteryMachine,
            @AssignToField("powerLotteryMachine") PowerLotteryMachine powerLotteryMachine) {
        this.lotteryMachine = lotteryMachine;
        this.powerLotteryMachine = powerLotteryMachine;
    }

    @Override
    public boolean isTicketSuccessful(Ticket ticket, Consumer<Boolean> responseReceiver) {//removed for clarity}

    @Override
    public boolean publishReport(Consumer<String> reportReceiver) {//removed for clarity}

    @Override
    public void selectWinningTicket() {// store results}

    @Override
    public void setResultPublisher(Consumer<String> resultPublisher) {//publish report}

    @Override
    public void newGame() {
        gameCount++;
    }
}

{% endhighlight %}

## Extended application
Now the new functionality and services are bound in to the generated event processor, the application can be extended
to use the new features in the sample main. Client code is completely hidden from the updates and the new wiring
that has been generated. All the previous functionality works as before and the application behaviour is extended reliably
and with teh minimum of effort. 

{% highlight java %}
public class LotteryApp {

    private static LotteryMachine lotteryMachine;
    private static TicketStore ticketStore;
    private static GameResultStore resultStore;

    public static void main(String[] args) {
        start(LotteryApp::ticketReceipt, LotteryApp::lotteryResult);

        lotteryMachine.newGame();
        ticketStore.openStore();

        //open store and buy ticket
        Ticket myGoldenTicket = new Ticket(12_65_56);
        ticketStore.buyTicket(myGoldenTicket);
        ticketStore.buyTicket(new Ticket(36_58_58));
        ticketStore.buyTicket(new Ticket(73_00_12));

        //run the lottery
        lotteryMachine.selectWinningTicket();

        //our new functionality
        resultStore.isTicketSuccessful(
                myGoldenTicket,
                b -> System.out.println( "\n" + myGoldenTicket + " is a " + (b ? "WINNER :)\n" : "LOSER :(\n")));
        resultStore.publishReport(System.out::println);
    }

    public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
        var lotteryEventProcessor = new LotteryProcessor();
        lotteryEventProcessor.init();

        //get service interfaces
        lotteryMachine = lotteryEventProcessor.getExportedService();
        ticketStore = lotteryEventProcessor.getExportedService();
        resultStore = lotteryEventProcessor.getExportedService();

        //register listeners via service interface
        lotteryMachine.setResultPublisher(resultsPublisher);
        ticketStore.setTicketSalesPublisher(ticketReceiptHandler);

        //start the processor
        lotteryEventProcessor.start();
    }

    public static void ticketReceipt(String receipt){
        System.out.println(receipt);
    }

    public static void lotteryResult(String receipt){
        System.out.println(receipt);
    }
}
{% endhighlight %}

## Running the application
Executing the application produces the following output:

{% highlight console %}
good luck with Ticket[number=126556, id=c5ad2b74-f9cf-4bbb-92d7-4ac9eb76e129]
good luck with Ticket[number=365858, id=a4f3dfa7-2e6d-4689-94e3-72678be3c68b]
good luck with Ticket[number=730012, id=ea6f5b9c-8571-4547-93f7-8752ee6b19df]
winning numbers:730012
POWER-LOTTERY winning numbers:126556

Ticket[number=126556, id=c5ad2b74-f9cf-4bbb-92d7-4ac9eb76e129] is a WINNER :)

GAME REPORT gameNumber:1
lottery winner:Ticket[number=730012, id=ea6f5b9c-8571-4547-93f7-8752ee6b19df]
POWER-LOTTERY  winner:Ticket[number=126556, id=c5ad2b74-f9cf-4bbb-92d7-4ac9eb76e129]

Process finished with exit code 0
{% endhighlight %}

# Conclusion
Hopefully you have learnt that extending an application with Fluxtion is simple and saves a lot of developer time.
Existing behaviour is left untouched the automated generation removes many potential errors. All the developer effort 
is focused on the adding business logic and creating value.

Comparing the previous generated version with the new version gives the developer to validate the new application 
classes are wired in as expected. 

[next tutorial 6](tutorial-6.md)
{: .text-right }