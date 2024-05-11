---
title: Functional hello world
has_children: false
parent: Hello fluxtion world
nav_order: 1
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/functional-helloworld/src/main/java/com/fluxtion/example/functional/helloworld
---

# 5 minute Functional hello world
---

Hello world using a functional programming style with Fluxtion DSL. Business logic resides in user functions removing
the need to write classes and annotate event handling methods with fluxtion annotations. 

Code is available as a [maven project]({{page.example_src}})

Add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the current value from each event stream. The stream of events can be infinitely long,
calculations are run whenever a new event is received.

This example creates an event processor, initialises it and fires data events at the processor. If a breach occurs
a warning will be logged to console. All dispatch and change notification is handled by Fluxtion when an event is
received.

For an imperative implementation example see [Hello fluxtion world](helloworld_imperative)

## Processing graph
{: .no_toc }
The functional approach has more nodes in the event processor compared to the imperative version, but the actual code 
written by the developer to create the algorithm is much shorter. Fluxtion DSL only requires the developer to write 
functions, any wrapping nodes are automatically added to the event processor.

```mermaid
flowchart TB

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA><b>InputEvent</b>::Event_A]:::eventHandler 
    EventB><b>InputEvent</b>::Event_B]:::eventHandler 
    
    HandlerA[<b>Subscriber</b>::Event_A]:::graphNode 
    HandlerB[<b>Subscriber</b>::Event_A]:::graphNode 
    
    MapData1[<b>Map</b> -> mapToDouble]:::graphNode 
    MapData2[<b>Map</b> -> mapToDouble]:::graphNode 
    
    MapDefaultData1[<b>Map</b> -> defaultValue]:::graphNode 
    MapDefaultData2[<b>Map</b> -> defaultValue]:::graphNode 
    
    BiMapSum[<b>BiMap</b> -> Double::sum]:::graphNode 
    
    Console1[<b>Peek</b> -> console]:::graphNode 
    Filter[<b>Filter</b> -> d > 100]:::graphNode 
    Console2[<b>Peek</b> -> console]:::graphNode 
    
    EventA --> HandlerA
    EventB --> HandlerB
    
    subgraph EventProcessor
      HandlerA --> MapData1 --> MapDefaultData1 --> BiMapSum
      HandlerB --> MapData2 --> MapDefaultData2 --> BiMapSum
      BiMapSum --> Console1 --> Filter --> Console2
    end
    
```

## Processing logic
The Fluxtion event processor manages all the event call backs, the user logic is a set of functions that are bound into
the event processor using the Fluxtion DSL.

* An event handlers is notified when an event of the matching type is received triggering the next item in the chain
* The event is mapped to a double using the Data1::value or Data2::value function
* A default double value of 0 is assigned to the output
* The two event streams are merged and passed to the bi map function. Double::sum is invoked when either input stream triggers
* A peek function logs the sum to the console
* A filter function is bound to the graph, if the sum > 100 the filter test passes and the next node is triggered
* A peek function logs the warning message to the console


## Dependencies
{: .no_toc }

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'Maven')" >Maven dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')" id="defaultOpen">Gradle dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'pom_xml')">Maven pom</button>
</div>

<div id="pom_xml" class="tabcontent">
<div markdown="1">
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>example.master</artifactId>
        <groupId>com.fluxtion.example</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>functional-helloworld</artifactId>
    <name>functional :: hello world</name>

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


# Step 1 - bind functions to events using Fluxtion DSL

The Fluxtion DSL is used to construct the algorithm chaining together functions that are triggered by an incoming event.
The algorithm is a graph not a simple pipeline that merges at the bi map function. 

By default, a bi map function is only invoked when both parents have triggered at least once. Supplying a default 
value removes the trigger check for the input to the bi map function.

{% highlight java %}
private static void bindFunctions(EventProcessorConfig cfg) {
    var data1Stream = DataFlow.subscribe(Data1.class)
            .mapToDouble(Data1::value)
            .defaultValue(0);

    DataFlow.subscribe(Data2.class)
            .mapToDouble(Data2::value)
            .defaultValue(0)
            .mapBiFunction(Double::sum, data1Stream)
            .console("sum:{}")
            .filter(d -> d > 100)
            .console("WARNING DataSumCalculator value is greater than 100 sum = {}");
}
{% endhighlight %}

## Events

Java records are used as events.

{% highlight java %}
public record Event_A(double value) {}
public record Event_B(double value) {}
{% endhighlight %}

# Step 2 - build the event processor

The functional DSL is used within the context of the `Fluxtion.interpreted` method to build the event processor. The
DSL processor binds all the user functions and required wrapping nodes into the event processor.

{% highlight java %}
var eventProcessor = Fluxtion.interpret(Main::bindFunctions);
{% endhighlight %}


# Step 3 - Integrate event processor and connect event stream

The example [Main method]({{page.example_src}}/Main.java) instantiates an event processor in interpreted mode, initialises it and submits events for
processing using the onEvent method. The init method must be called before submitting events. 

Events are submitted for processing by calling `eventProcessor.onEvent()` with instances of Event_A or Event_B.

The code for instantiating, initializing and sending events is:

{% highlight java %}
public class Main {
    public static void main(String[] args) {
        //build the EventProcessor and initialise it
        var eventProcessor = Fluxtion.interpret(Main::bindFunctions);
        eventProcessor.init();

        //send events
        eventProcessor.onEvent(new Data1(34));
        eventProcessor.onEvent(new Data2(52.1));
        eventProcessor.onEvent(new Data1(105));//should create a breach warning
        eventProcessor.onEvent(new Data1(12.4));
    }

    private static void bindFunctions(EventProcessorConfig cfg) {
        var data1Stream = DataFlow.subscribe(Data1.class)
                .mapToDouble(Data1::value)
                .defaultValue(0);
    
        DataFlow.subscribe(Data2.class)
                .mapToDouble(Data2::value)
                .defaultValue(0)
                .mapBiFunction(Double::sum, data1Stream)
                .console("sum:{}")
                .filter(d -> d > 100)
                .console("WARNING DataSumCalculator value is greater than 100 sum = {}");
    }
}
{% endhighlight %}

## Example execution output

{% highlight console %}
sum:34.0
sum:86.1
sum:157.1
WARNING DataSumCalculator value is greater than 100 sum = 157.1
sum:64.5
{% endhighlight %}


<script>
document.getElementById("defaultOpen").click();
document.getElementById("defaultExample").click();
</script>
