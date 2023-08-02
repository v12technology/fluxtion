---
title: Helloworld functional
parent: Overview
has_children: false
nav_order: 3
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/functional-helloworld/src/main/java/com/fluxtion/example/functional/helloworld
---

# 5 minute hello world - functional

Simple Fluxtion hello world stream example. Add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the value member variable from each event stream.

All the elements are joined together using a functional api provided by Fluxtion. The [EventFlow]({{site.fluxtion_src_compiler}}/builder/stream/EventFlow.java)
gives access to functions to create a stream functionally.

See the [imperative example](helloworld_imperative.html) for a comparison of using an imperative style.

Code is available as a [maven project]({{page.example_src}})

### Processing graph

Graphical representation of the processing graph that Fluxtion will generate.

![](../../images/helloworld_eventstream.png)

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

The example creates an EventProcessor using the streaming api. A subscription is made to
each class of event and the double value from each new event is extracted as a map operation.
Console operations are applied to introspect the state of the stream. Eventually the double value are supplied as
arguments to the binary function Double::sum. A filter checks the value is > 100 before pushing the sum value to 
next operation in the chain.


{% highlight java linenos %}
public class Main {
    public static void main(String[] args) {
        //builds the EventProcessor
        EventProcessor eventProcessor = Fluxtion.interpret(cfg -> {
            var data1Stream = EventFlow.subscribe(Data1.class)
                    .console("rcvd -> {}")
                    .mapToDouble(Data1::value);

            EventFlow.subscribe(Data2.class)
                    .console("rcvd -> {}")
                    .mapToDouble(Data2::value)
                    .mapBiFunction(Double::sum, data1Stream)
                    .filter(d -> d > 100)
                    .console("OUT: sum {} > 100");
        });
        //init and send events
        eventProcessor.init();
        //no output < 100
        eventProcessor.onEvent(new Data1(20.5));
        //no output < 100
        eventProcessor.onEvent(new Data2(63));
        //output > 100 - log to console
        eventProcessor.onEvent(new Data1(56.8));
    }

    public record Data1(double value) {
    }

    public record Data2(double value) {
    }
}
{% endhighlight %}

- **line 4-15** Builds an EventProcessor using Fluxtion.interpret() taking a lambda to build the graph
- **line 5-7** Subscribes to Data1 events, then maps to a primitive double, using Data1::value method reference
- **line 9-11** Subscribes to Data2 events, then maps to a primitive double, using Data2::value method reference
- **line 12** Combines both stream of doubles and applies a binary double function, Double::sum, if either stream updates
- **line 13** Filters the output, only triggers a notification if sum > 100
- **line 14** Console output of the triggered stream
- **line 17** Init the eventProcessor instance
- **line 19-23** Posts a stream of events to the eventProcessor instance

## Example execution output

{% highlight console %}
rcvd -> Data1[value=20.5]
rcvd -> Data2[value=63.0]
rcvd -> Data1[value=56.8]
OUT: sum 119.8 > 100
{% endhighlight %}


<script>
document.getElementById("defaultOpen").click();
</script>
