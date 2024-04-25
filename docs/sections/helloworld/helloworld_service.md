---
title: Service Hello world
has_children: false
parent: Hello fluxtion world
nav_order: 2
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/service-helloworld/src/main/java/com/fluxtion/example/imperative/helloworld



---

# 5 minute Service hello world 

Hello world using a service programming style with Fluxtion. Event handler methods are replaced with service methods that
are defined in an interface. The bound class exports the service interface with an `@ExportService` annotation. A bound
service implementation is registered in the event processor and is discoverable by client code using the service interface
class. 

Code is available as a [maven project]({{page.example_src}})

Add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the current value from each event stream. The stream of events can be infinitely long,
calculations are run whenever a new event is received.

This example creates an event processor, initialises it, looks up the service interface and calls service methods
with instances of data tuples. If a breach occurs a warning will be logged to console. All dispatch and change
notification is handled by Fluxtion when a service method is invoked.


For an event based implementation example see [Hello fluxtion world](helloworld_imperative)

## Processing graph
{: .no_toc }
The service approach has less nodes bound in the event processor compared to the event driven version. This is because
there is no need to add explicit event handler nodes, any exported service method acts as an entry point for triggering
a process cycle. 


```mermaid
flowchart TB

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA><b>ServiceCall</b> updateA#Event_A]:::eventHandler 
    EventB><b>ServiceCall</b> updateB#Event_B]:::eventHandler 
    DataSumCalculator([<b>ServiceLookup</b>::DataSumCalculator]):::exportedService 
    DataSumCalculatorImpl[DataSumCalculatorImpl\n <b>ExportService</b>::DataSumCalculator]:::graphNode
    BreachNotifier:::graphNode
    
    EventA & EventB --> DataSumCalculator --> DataSumCalculatorImpl
        
    subgraph EventProcessor
      DataSumCalculatorImpl --> BreachNotifier
    end
    
```

## Processing logic
The Fluxtion event processor manages all the event call backs, the user code handles the business logic, triggered from 
service calls.

* The client looks up DataSumCalculator service interface and invokes an update method
* The event processor triggers a processing cycle and calls the implementing update method in DataSumCalculatorImpl
* The service method calculates the current sum extracting and storing values from Event_A and Event_B.
* If the sum > 100 the DataSumCalculatorImpl returns true which propagates a notification to the BreachNotifier annotated trigger method.
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
1 - Create and implement interface, mark the implementing class with `@ExportedService` <br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }


# Step 1 - Export service interface of bound classes

Create the DataSumCalculator interface and implement it with DataSumCalculatorImpl. DataSumCalculator marks the
DataSumCalculator interface for exports it by marking the declaration with `@ExportsService`.

<div class="tab">
  <button class="tablinks2" onclick="openTab2(event, 'DataSumCalculator')" id="defaultExample">DataSumCalculator</button>
  <button class="tablinks2" onclick="openTab2(event, 'DataSumCalculatorImpl')">DataSumCalculatorImpl</button>
  <button class="tablinks2" onclick="openTab2(event, 'BreachNotifier')">BreachNotifier</button>
</div>

<div id="DataSumCalculator" class="tabcontent2">
<div markdown="1">
Create a DataSumCalculator service interface that user code can call. The DataSumCalculatorImpl bound to the 
event processor implements and export this interface.

{% highlight java %}
public interface DataSumCalculator {
    boolean updateA(Event_A eventA);
    boolean updateB(Event_B eventA);
}
{% endhighlight %}
</div>
</div>

<div id="DataSumCalculatorImpl" class="tabcontent2">
<div markdown="1">
Calculates the current sum adding the latest values of Event_A and Event_B. Implements the DataSumCalculator interface 
and exports it by marking the declaration with `@ExportsService`. 

Exported service methods are invoked when client code looks up the service interface and calls one of the update methods. 

The boolean return of the service method determines if the BreachNotifier is triggered.

{% highlight java %}
public class DataSumCalculatorImpl implements @ExportService DataSumCalculator {

    @Getter
    private double sum;
    private double aValue = 0;
    private double bValue = 0;

    @Override
    public boolean updateA(Event_A eventA) {
        aValue = eventA.value();
        return checkSum();
    }

    @Override
    public boolean updateB(Event_B eventB) {
        bValue = eventB.value();
        return checkSum();
    }

    private boolean checkSum() {
        sum = aValue + bValue;
        System.out.println("sum:" + sum);
        return sum > 100;
    }
}
{% endhighlight %}
</div>
</div>

<div id="BreachNotifier" class="tabcontent2">
<div markdown="1">
Logs to console when the sum breaches a value, BreachNotifier holds a reference to the DataSumCalculator instance.
The trigger method is only invoked if the DataSumCalculator propagates the notification, by returning true from its
trigger method. Annotate the trigger method with **@OnTrigger** as follows:

{% highlight java %}
public class BreachNotifier {
    private final DataSumCalculatorImpl dataAddition;

    public BreachNotifier(DataSumCalculatorImpl dataAddition) {
        this.dataAddition = dataAddition;
    }

    public BreachNotifier() {
        this(new DataSumCalculatorImpl());
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

If any bound instance exports a service, this will be registered by the event processor and the interface will be
discoverable by client code calling `DataSumCalculator dataSumCalculator = eventProcessor.getExportedService();`


{% highlight java %}
var eventProcessor = Fluxtion.interpret(new BreachNotifier());
{% endhighlight %}


# Step 3 - Integrate event processor and connect event stream

The example [Main method]({{page.example_src}}/Main.java) instantiates an event processor in interpreted mode, initialises it and submits events for
processing using the onEvent method. The init method must be called before submitting events.

The DataSumCalculator service is discovered by calling `DataSumCalculator dataSumCalculator = eventProcessor.getExportedService();`
on the event processor. An Event processing cycle is triggered by calling methods on the service interface reference.

The code for instantiating, initializing and call service methods

{% highlight java %}
public static void main(String[] args) {
    var eventProcessor = Fluxtion.interpret(new BreachNotifier());
    eventProcessor.init();

    //use the service api to trigger sum calculations
    DataSumCalculator dataSumCalculator = eventProcessor.getExportedService();
    dataSumCalculator.updateA(new Event_A(34.4));
    dataSumCalculator.updateB(new Event_B(52.1));
    dataSumCalculator.updateA(new Event_A(105));
    dataSumCalculator.updateA(new Event_A(12.4));
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
</script>
