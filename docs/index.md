---
title: Overview
has_children: false
nav_order: 1
published: true
---

# Fluxtion automating event driven development

---

Fluxtion is a Java library and code generation utility designed for building high-performance, low-latency streaming
applications. It provides a lightweight framework for event-driven programming, particularly suited for applications
such as financial trading systems, real-time analytics, and sensor data processing. Fluxtion emphasizes simplicity,
efficiency, and ease of use in handling streaming data.

<div class="grid">
<div class="col-1-2">
<div class="content">
<ul>
  <li><strong>Streaming event processing</strong></li>
  <li><strong>AOT compilation for fast start</strong></li>
  <li><strong>Spring integration</strong></li>
</ul>
</div>
</div>
<div class="col-1-2">
<div class="content">
<ul>
  <li><strong>Low latency microsecond response</strong></li>
  <li><strong>Event sourcing compatible</strong></li>
  <li><strong>Optimised for zero gc to reduce running costs</strong></li>
</ul>
</div>
</div>
</div>

{: .info }
Overall, Fluxtion provides a powerful framework for building real-time streaming applications in Java, offering high
performance, flexibility, and ease of use for developers working with streaming data.
{: .fs-4 }

**Event-driven Programming**: Fluxtion is based on the concept of event-driven programming, where components react to
incoming events in real-time. Events can represent changes in data, user actions, or any other relevant triggers.

**Dependency Graphs**: Fluxtion models the processing logic of the application as a directed acyclic graph (DAG) of
nodes,
where each node represents a computation or transformation operation. Nodes can subscribe to input events, perform
calculations, and publish output events.

**Event Processing Pipelines**: Fluxtion allows developers to define event processing pipelines, where events flow
through a
series of interconnected nodes. This allows for complex data transformations and aggregations to be performed
efficiently.

**Annotation-Based Configuration**: Fluxtion provides an annotation-based configuration mechanism, allowing developers
to
define event processing logic using simple annotations. This simplifies the development
process and reduces boilerplate code.

**Code Generation**: Fluxtion employs code generation techniques to optimize the performance of event processing
pipelines.
During compilation, Fluxtion generates highly optimized Java code tailored to the specific event processing logic
defined by the developer.

**Low Latency**: Fluxtion is designed to achieve low latency and high throughput, making it suitable for applications
where
real-time responsiveness is critical, such as financial trading systems.

**Support for Complex Event Processing (CEP)**: Fluxtion includes support for complex event processing, allowing
developers
to define complex event patterns and rules using a high-level DSL (Domain-Specific Language).

**Integration with External Systems**: Fluxtion can easily integrate with external systems and libraries, allowing
developers to incorporate Fluxtion-based event processing logic into existing applications or frameworks.

**Developer Productivity**: Fluxtion has been designed to increases developer productivity when building and
supporting event driven applications

# Example

[This example]({{site.reference_examples}}/racing) tracks and calculates times for runners in a race. Start and finish
times are received as a stream of events,
when a runner finishes they receive their individual time. A call to `publishAllResults` will publish all current
results.

The developer writes the core business logic annotating any methods that should receive event callbacks or services that
are exported.
Fluxtion takes care of generating all the event dispatch code that is time consuming to write, error prone and adds
little value.
The generated event processor is used like any normal java class in the application.

## Processing graph
{: .no_toc }

```mermaid
flowchart TB
    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}

    RunnerStarted><b>InputEvent</b>::RunnerStarted]:::eventHandler 
    RunnerFinished><b>InputEvent</b>::RunnerFinished]:::eventHandler 
    ResultsPublisher([<b>ServiceLookup</b>::ResultsPublisher]):::exportedService 
    
    RaceTimeTracker[RaceTimeTracker\n<b>EventHandler</b>::RunnerStarted \n<b>EventHandler</b>::RunnerFinished]:::graphNode 
    ResultsPublisherImpl[ResultsPublisherImpl\n <b>ExportService</b>::ResultsPublisher]:::graphNode

    RunnerStarted --> RaceTimeTracker
    RunnerFinished --> RaceTimeTracker
    ResultsPublisher --> ResultsPublisherImpl
    
    subgraph EventProcessor
        RaceTimeTracker --> ResultsPublisherImpl
    end
    
```
## Processing logic
The Fluxtion event processor manages all the event call backs, the user code handles the business logic.

* The RaceTimeTracker is notified when a RunnerStarted event is received and records the start time for that runner
* The runnerStarted handler method does not propagate so the event is swallowed at that point
* The RaceTimeTracker is notified when a RunnerFinished event is received, calculates the runner race time and triggers the ResultsPublisherImpl
* The ResultsPublisherImpl annotated trigger method gets the finisher data from RaceTimeTracker and logs the individual runner's race time
* When the race is over the service method ResultsPublisher.publishAllResults is called, the event processor routes 
this call to ResultsPublisherImpl which publishes the final results.

## Three steps to using Fluxtion

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

<div class="tab">
  <button class="tablinks2" onclick="openTab2(event, 'Event logic')" >1 - Write logic. Mark event handler methods</button>
  <button class="tablinks2" onclick="openTab2(event, 'Binding functions')">2 - Build event processor</button>
  <button class="tablinks2" onclick="openTab2(event, 'App integration')" id="defaultExample">3 - Integrate event feed processing</button>
</div>

<div id="Event logic" class="tabcontent2">
<div markdown="1">
Custom business logic written by developer, annotated methods receive event callbacks. Here we use `@OnEventHandler`
and `@OnTrigger` for events and `@ExportService` to export a service interface.
{% highlight java %}
public class RaceCalculator {
    //streamed events
    public record RunnerStarted(long runnerId, Instant startTime) {}
    public record RunnerFinished(long runnerId, Instant finishTime) {}

    //service api
    public interface ResultsPublisher {
        void publishAllResults();
    }

    //event driven logic
    @Getter
    public static class RaceTimeTracker {
        private final transient Map<Long, RunningRecord> raceTimeMap = new HashMap<>();
        private RunningRecord latestFinisher;

        //FLUXTION ANNOTATION - lfecycle init callback
        @Initialise
        public void init(){
            raceTimeMap.clear();
        }

        //FLUXTION ANNOTATION - subscribe to RunnerStarted events, no propagation to child nodes
        @OnEventHandler(propagate = false)
        public boolean runnerStarted(RunnerStarted runnerStarted) {
            long runnerId = runnerStarted.runnerId();
            raceTimeMap.put(runnerId, new RunningRecord(runnerId, runnerStarted.startTime()));
            return false;
        }

        //FLUXTION ANNOTATION - subscribe to RunnerFinished events, propagates trigger to child nodes 
        @OnEventHandler
        public boolean runnerFinished(RunnerFinished runner) {
            latestFinisher = raceTimeMap.computeIfPresent(
                    runner.runnerId(),
                    (id, startRecord) -> new RunningRecord(id, startRecord.startTime(), runner.finishTime()));
            return true;
        }
    }

    @RequiredArgsConstructor
    public static class ResultsPublisherImpl implements 
        //FLUXTION ANNOTATION - exports the service api for this instance
        @ExportService ResultsPublisher {
        private final RaceTimeTracker raceTimeTracker;

        //FLUXTION ANNOTATION - triggered on a parent notification, from RunnerFinished event handler
        @OnTrigger
        public boolean sendIndividualRunnerResult(){
            var raceRecord = raceTimeTracker.getLatestFinisher();
            System.out.format("Crossed the line runner:%d time [%s]%n", raceRecord.runnerId(), raceRecord.runDuration());
            return false;
        }

        @Override
        public void publishAllResults() {
            System.out.println("\nFINAL RESULTS");
            raceTimeTracker.getRaceTimeMap().forEach((l, r) ->
                    System.out.println("id:" + l + " time [" + r.runDuration() + "]"));
        }
    }

    //internal record
    private record RunningRecord(Instant startTime, Instant finishTime) {
        public String runDuration() {
            Duration duration = Duration.between(startTime, finishTime);
            return duration.toHoursPart() + ":" + duration.toMinutesPart() + ":" + duration.toSecondsPart();
        }
    }
}

{% endhighlight %}
</div>
</div>

<div id="Binding functions" class="tabcontent2">
<div markdown="1">
Bind user functions to the event processor and build. This is an interpreted example there is also an 
[AOT project]({{site.reference_examples}}/racing-aot) version.

{% highlight java %}
var raceCalculator = Fluxtion.interpret( new ResultsPublisherImpl(new RaceTimeTracker()));
{% endhighlight %}

Pom.xml includes the fluxtion dependencies
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <groupId>com.fluxtion.example</groupId>
    <artifactId>reference-examples</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>reference-example :: racing</name>
    <artifactId>racing</artifactId>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

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


<div id="App integration" class="tabcontent2">
<div markdown="1">
Application feeds events to an instance of the generated event processor. The event processor dispatches 
events to business functions
{% highlight java %}
public class RaceCalculatorApp {
    public static void main(String[] args) {
        //build and initialise the raceCalculator event processor
        var raceCalculator = Fluxtion.interpret( new ResultsPublisherImpl(new RaceTimeTracker()));
        raceCalculator.init();

        //connect to event stream and process runner timing events
        raceCalculator.onEvent(new RunnerStarted(1, "2019-02-14T09:00:00Z"));
        raceCalculator.onEvent(new RunnerStarted(2, "2019-02-14T09:02:10Z"));
        raceCalculator.onEvent(new RunnerStarted(3, "2019-02-14T09:06:22Z"));

        raceCalculator.onEvent(new RunnerFinished(2, "2019-02-14T10:32:15Z"));
        raceCalculator.onEvent(new RunnerFinished(3, "2019-02-14T10:59:10Z"));
        raceCalculator.onEvent(new RunnerFinished(1, "2019-02-14T11:14:32Z"));

        //use the exported service interface ResultsPublisher to publish full results
        ResultsPublisher resultsPublisher = raceCalculator.getExportedService();
        resultsPublisher.publishAllResults();
    }
}
{% endhighlight %}

Output from executing the application

{% highlight console %}
Crossed the line runner:2 time:1:30:5
Crossed the line runner:3 time:1:52:48
Crossed the line runner:1 time:2:14:32

FINAL RESULTS
id:1 final time:2:14:32
id:2 final time:1:30:5
id:3 final time:1:52:48
{% endhighlight %}
</div>
</div>

<div id="Generated image" class="tabcontent2">
<div markdown="1">
Image is generated as part of the code generator

![](images/RaceCalculatorProcessor.png)
</div>
</div>

# Getting started

## Developers
For a quick introduction to programming Fluxtion visit the [hello world](sections/helloworld/helloworld_imperative) examples.

A [developer workflow document](gettingstarted/developer-workflow) describes the approach integrating Fluxtion into your work cycle

A series of tutorials are provided that a developer should follow to become familiar with the practical coding of
Fluxtion, start with [tutorial 1](sections/gettingstarted/tutorial-1.md).

## Architects

For a deeper understanding of the architecture, design and paradigms that underpin Fluxtion head over to the
[Fluxtion explored section](sections/fluxtion-explored).

## Reference documentation

* [Mark event handling](sections/runtime.md)
* [Build event processor](sections/build-event-processor)
* [Integrate event processor](sections/integrate-eventprocessor)
* [Quick reference](sections/quick-reference.md)

# Latest release

| component | maven central                                                                                                                                                                    |
|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Runtime   | [![Fluxtion runtime](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime)    |
| Compiler  | [![Fluxtion compiler](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/compiler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/compiler) |

## Build dependencies

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'Maven')">Maven</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')" id="defaultOpen">Gradle</button>
</div>
<div id="Maven" class="tabcontent">
<div markdown="1">
{% highlight xml %}
    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
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
implementation 'com.fluxtion:runtime:{{site.fluxtion_version}}'
implementation 'com.fluxtion:compiler:{{site.fluxtion_version}}'
{% endhighlight %}
</div>
</div>

# The cost of complexity problem

Increasing system complexity makes delivery of new features expensive and time-consuming to deliver. Efficiently
managing
complexity reduces both operational costs and time to market for new functionality, critical for a business to remain
profitable in a competitive environment.

Event driven systems have two types of complexity to manage:

- Delivering events to application components in a fault-tolerant predictable fashion.
- Developing application logic responses to events that meets business requirements

Initially all the project complexity centres on the event delivery system, but over time this system becomes stable and
the complexity demands are minimal. Pre-packaged event delivery systems are a common solution to control complexity and
cost of event distribution. The opposite is true for event driven application logic, functional requirements increase
over time and developing application logic becomes ever more complex and expensive to deliver. As more functionality is
added the danger of instability increases.

Fluxtion reduces the cost to develop and maintain business logic, while maintaining stability. Automating the expensive
and error-prone process of wiring application logic to multiple event streams is Fluxtion's target. For long term
development
projects the ROI of using Fluxtion increases exponentially.

<script>
document.getElementById("defaultOpen").click();
document.getElementById("defaultExample").click();
</script>