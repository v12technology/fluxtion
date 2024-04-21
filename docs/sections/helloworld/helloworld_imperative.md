---
title: Hello fluxtion world
has_children: true
nav_order: 2
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/imperative-helloworld/src/main/java/com/fluxtion/example/imperative/helloworld
---

# 5 minute hello world
{: .no_toc }

Use Fluxtion to add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the current value from each event stream. The stream of events can be infinitely long,
calculations are run whenever a new event is received. For a functional implementation example see [Hello functional fluxtion world](helloworld_functional)

This example creates an event processor, initialises it and fires data events at the processor. If a breach occurs
a warning will be logged to console. All dispatch and change notification is handled by Fluxtion when an event is 
received. Business logic resides in the user functions/classes.

Code is available as a [maven project]({{page.example_src}})

## Processing graph
{: .no_toc }

```mermaid
flowchart TB

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA><b>InputEvent</b>::Event_A]:::eventHandler 
    EventB><b>InputEvent</b>::Event_B]:::eventHandler 
    HandlerA[Event_A_Handler\n<b>EventHandler</b>::Event_A]:::graphNode 
    HandlerB[Event_B_Handler\n<b>EventHandler</b>::Event_A]:::graphNode 
    DataSumCalculator:::graphNode
    BreachNotifier:::graphNode

    EventA --> HandlerA
    EventB --> HandlerB
    
    subgraph EventProcessor
      HandlerA --> DataSumCalculator
      HandlerB --> DataSumCalculator
      DataSumCalculator --> BreachNotifier
    end
    
```
## Processing logic
The Fluxtion event processor manages all the event call backs, the user code handles the business logic.

* An event handlers is notified when an event of the matching type is received.
* This in turn invokes the DataSumCalculator annotated trigger method which calculates the current sum extracting values from handler_A and handler_B.
* If the sum > 100 the DataSumCalculator returns true which propagates a notification to the BreachNotifier annotated trigger method.  
* The BreachNotifier trigger method prints a message to the console.


## Dependencies
{: .no_toc }

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'Maven')" id="defaultOpen">Maven dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')">Gradle dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'pom_xml')">Maven pom</button>
</div>

<div id="pom_xml" class="tabcontent">
<div markdown="1">
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <artifactId>example.master</artifactId>
    <groupId>com.fluxtion.example</groupId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>imperative-helloworld</artifactId>
  <name>imperative :: hello world</name>
  
  <dependencies>
      <dependency>
          <groupId>com.fluxtion</groupId>
          <artifactId>compiler</artifactId>
          <version>{{site.fluxtion_version}}</version>
      </dependency>
  </dependencies>
</project>
{% endhighlight %}
</div>
</div>

<div id="Maven" class="tabcontent">
<div markdown="1">
{% highlight xml %}
    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
    </dependencies>
{% endhighlight %}
</div>
</div>

<div id="Gradle" class="tabcontent">
<div markdown="1">
{% highlight groovy %}
implementation 'com.fluxtion:compiler:{{site.fluxtion_version}}'
{% endhighlight %}
</div>
</div>

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

# Step 1 - annotate event handling methods

There are two types of user classes employed at runtime. First, pojo's with event processing methods that are bound into
the
generated event processor. Secondly, record classes that defines the event types that are fed into the
BreachNotifierProcessor.
The event processor routes events to event handler methods on bound instances.

Annotated callback methods

- **@OnEventHandler** annotation declares the [entry point]({{page.example_src}}/Event_A_Handler.java) of an execution
  path, triggered by an external event.
- **@OnTrigger** annotated [methods]({{page.example_src}}/DataSumCalculator.java) indicate call back methods to be
  invoked if a parent propagates a change.

The return boolean flag from a trigger or event handler method indicates if event notification should be propagated.

## Event handlers

| Name              | Event handler | Trigger handler | Description                                                      |
|-------------------|---------------|-----------------|------------------------------------------------------------------|
| Event_A_Handler   | yes           | no              | Handles incoming events of type Event_A                          |
| Event_B_Handler   | yes           | no              | Handles incoming events of type Event_B                          |
| DataSumCalculator | no            | yes             | References DataHandler nodes and calculates the current sum      |
| BreachNotifier    | no            | yes             | References the DataSumCalculator and logs a warning if sum > 100 |

The event handler method is called when a matching event type is published to the container, the trigger handler is
called when a parent dependency haa been trigger or a parent event handler method has been called.

<div class="tab">
  <button class="tablinks2" onclick="openTab2(event, 'Handler A')" id="defaultExample">Handler A</button>
  <button class="tablinks2" onclick="openTab2(event, 'Handler B')">Handler B</button>
  <button class="tablinks2" onclick="openTab2(event, 'DataSumCalculator')">DataSumCalculator</button>
  <button class="tablinks2" onclick="openTab2(event, 'BreachNotifier')">BreachNotifier</button>
</div>

<div id="Handler A" class="tabcontent2">
<div markdown="1">
An entry point for processing events of type Event_A and stores the latest value as a member variable.
Annotate the event handler method with `@OnEventHandler` as follows:
{% highlight java %}
public class Event_A_Handler {
    private double value;

    @OnEventHandler
    public boolean data1Update(Event_A data1) {
        value = data1.value();
        return true;
    }

    public double getValue() {
        return value;
    }
}
{% endhighlight %}
</div>
</div>

<div id="Handler B" class="tabcontent2">
<div markdown="1">
An entry point for processing events of type Event_B and stores the latest value as a member variable.
Annotate the event handler method with `@OnEventHandler` as follows:
{% highlight java %}
public class Event_B_Handler {
    private double value;

    @OnEventHandler
    public boolean data1Update(Event_B data2) {
        value = data2.value();
        return true;
    }

    public double getValue() {
        return value;
    }
}
{% endhighlight %}
</div>
</div>

<div id="DataSumCalculator" class="tabcontent2">
<div markdown="1">
Calculates the current sum adding the values of Event_A_Handler and Event_B_Handler. Will be triggered when either handler
has its updated method invoked. Annotate the trigger method with **@OnTrigger** as follows:

{% highlight java %}
public class DataSumCalculator {
private final Event_A_Handler event_A_Handler;
private final Event_B_Handler event_B_Handler;
private double sum;

    public DataSumCalculator(Event_A_Handler event_A_Handler, Event_B_Handler event_B_Handler) {
        this.event_A_Handler = event_A_Handler;
        this.event_B_Handler = event_B_Handler;
    }

    public DataSumCalculator() {
        this(new Event_A_Handler(), new Event_B_Handler());
    }

    /**
     * The {@link OnTrigger} annotation marks this method to be called if any parents have changed
     *
     * @return flag indicating a change and a propagation of the event wave to child dependencies if the sum > 100
     */
    @OnTrigger
    public boolean calculate() {
        sum = event_A_Handler.getValue() + event_B_Handler.getValue();
        System.out.println("sum:" + sum);
        return sum > 100;
    }

    public double getSum() {
        return sum;
    }
}
{% endhighlight %}

The return flag indicates that the event notification should be propagated and any child nodes trigger methods
should be invoked.
</div>
</div>


<div id="BreachNotifier" class="tabcontent2">
<div markdown="1">
Logs to console when the sum breaches a value, BreachNotifier holds a reference to the DataSumCalculator instance.
The trigger method is only invoked if the DataSumCalculator propagates the notification, by returning true from its
trigger method. Annotate the trigger method with **@OnTrigger** as follows:

{% highlight java %}
public class BreachNotifier {
private final DataSumCalculator dataAddition;

    public BreachNotifier(DataSumCalculator dataAddition) {
        this.dataAddition = dataAddition;
    }

    public BreachNotifier() {
        this(new DataSumCalculator());
    }

    @OnTrigger
    public boolean printWarning() {
        System.out.println("WARNING DataSumCalculator value is greater than 100 sum = " + dataAddition.getSum());
        return true;
    }
}
{% endhighlight %}
</div>
</div>

## Events

Java records are used as events.

{% highlight java %}
public record Event_A(double value) {}
public record Event_B(double value) {}
{% endhighlight %}

# Step 2 - build the event processor

All the pojo classes required for processing are linked together using an imperative style in our main method and supplied 
to the `Fluxtion.interpreted` method to build the event processor. The Fluxtion interpreter interrogates the supplied instances 
and binds all the user pojos into the event processor. 

Any connected instance will be automatically discovered and added to the final event processor binding the whole user
object graph into the event processor.

{% highlight java %}
var eventProcessor = Fluxtion.interpret(new BreachNotifier());
{% endhighlight %}

# Step 3 - Integrate event processor and connect event stream

The example [Main method]({{page.example_src}}/Main.java) instantiates an event processor in interpreted mode, initialises it and submits events for
processing using the onEvent method. The init method must be called before submitting events.

Events are submitted for processing by calling `eventProcessor.onEvent()` with instances of Event_A or Event_B.

The code for instantiating, initializing and sending events:

{% highlight java %}
public class Main {
    public static void main(String[] args) {
        var eventProcessor = Fluxtion.interpret(new BreachNotifier());
        eventProcessor.init();
        eventProcessor.onEvent(new Event_A(34.4));
        eventProcessor.onEvent(new Event_B(52.1));
        eventProcessor.onEvent(new Event_A(105));//should create a breach warning
        eventProcessor.onEvent(new Event_A(12.4));
    }
}
{% endhighlight %}


## Example execution output

{% highlight console %}
sum:34.4
sum:86.5
sum:157.1
WARNING DataSumCalculator value is greater than 100 sum = 157.1
sum:64.5
{% endhighlight %}



<script>
document.getElementById("defaultOpen").click();
document.getElementById("defaultExample").click();
document.getElementById("aotBuilder").click();
</script>