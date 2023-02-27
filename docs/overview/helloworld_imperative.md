---
title: Helloworld imperative
parent: Overview
has_children: false
nav_order: 1
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/imperative-helloworld/src/main/java/com/fluxtion/example/imperative/helloworld
---

# Example

Fluxtion hello world stream example. Add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the value member variable from each event stream. 

This example creates an event processor, initialises it and fires data events at the processor. If a breach occurs 
the warning will be logged to console.

Code is available as a [maven project]({{page.example_src}})

## Steps to build an EventProcessor
All projects that build a Fluxtion [EventProcessor]({{site.EventProcessor_link}}) at runtime follow similar steps
- Create a maven or gradle project adding the Fluxtion compiler dependency to the project runtime classpath
- Write pojo's that will be nodes in the graph
- [Annotate]({{site.fluxtion_src_runtime}}/annotations/) a method to indicate it is an event handling callback
- Create a collection of instances of the pojo's that will act as nodes in the EvenProcessor
- Set references between the pojos as per normal java. Constructor, getter/setter, public access etc.
- Use one of the [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) compile/interpret methods passing in a 
builder method that accepts [EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java)
- Add your the root node/s of your object instance graph using EventProcessorConfig.addNode in your builder method
- An EventProcessor instance is returned ready to be used
- Call EventProcessor.init() to ensure the graph is ready to process events
- To publish events to the processor call EventProcessor.onEvent(object)
- Fluxtion guarantees the dispatch of notifications to your pojo's is in topological order
- When you process ends you can optionally call EventProcessor.tearDown()

## Processing graph

Graphical representation of the processing graph that Fluxtion will generate.

![](../images/helloworld/helloworld_imperative.png)

## Dependencies

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'Maven')" id="defaultOpen">Maven</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')">Gradle</button>
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


## Java code

All the elements are joined together using an imperative style in the Fluxtion builder. There are two style of class in
the example, pojo nodes that hold processing logic and events that notify the EventProcessor of a change.

The example [Main method]({{page.example_src}}/Main.java) constructs an EvenProcessor, initialises it and fires events 
to the processor for processing

### Pojo classes

| Name              | Event handler | Description                                                      |
|-------------------|---------------|------------------------------------------------------------------|
| Data1Handler      | yes           | Handles incoming events of type InputDataEvent_1                 |
| Data2Handler      | yes           | Handles incoming events of type InputDataEvent_2                 |
| DataSumCalculator | no            | References DataHandler nodes and calcultes the current sum       |
| BreachNotifier    | no            | References the DataSumCalculator and logs a warning if sum > 100 |

####  [Data1Handler]({{page.example_src}}/Data1handler.java)

####  [Data2Handler]({{page.example_src}}/Data2handler.java)

####  [DataSumCalculator]({{page.example_src}}/DataSumCalculator.java)

####  [BreachNotifier]({{page.example_src}}/BreachNotifier.java)

### Event classes

####  [InputDataEvent_1]({{page.example_src}}/InputDataEvent_1.java)

####  [InputDataEvent_2]({{page.example_src}}/InputDataEvent_2.java)

### Building the EventProcessor

See [Main]({{page.example_src}}/Main.java)

### Publishing events


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
</script>