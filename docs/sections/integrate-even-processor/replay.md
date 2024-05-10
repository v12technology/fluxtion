---
title: Event sourcing and replay
parent: Integrate event processor
grand_parent: Reference documentation
has_children: false
nav_order: 2
published: true
example_project: https://github.com/v12technology/fluxtion-examples/tree/main/getting-started/developer-workflow
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/reference-examples/integration/src/main/java/com/fluxtion/example/reference/integration/replay
---

# Event sourcing and replay
{: .no_toc }
---

Fluxtion supports event sourcing and replay of event based inputs to an event processor. An event source style system
captures the event stream for later replay. If the only changes in the event processor occur from processing
events, then replaying events into an event processor with the same start state will recreate the exact response as at
the time of recording.

{: .info }
Event sourcing and replay is an excellent tool for recreating, diagnosing and solving problems offline. 
Vastly reducing expensive support costs
{: .fs-4 }

Fluxtion supports recording and replay with two utility classes:

* [YamlReplayRecordWriter]({{site.fluxtion_src_compiler}}/replay/YamlReplayRecordWriter.java) - creates the event log
  file when bound to an event processor
* [YamlReplayRunner]({{site.fluxtion_src_compiler}}/replay/YamlReplayRunner.java) - reads and relays an event log file
  into a test event processor

In order to replay a set of events we need to record them first in the live system. Once the event log file has been
created this file can be replayed into an event processor in a development environment. The replay of events can give
us two important capabilities in the test environment

* Why did the live system react in the way it did, no need to guess by inspecting logs
* How will an updated event processor behave with the same inputs

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Clock time

Time plays a critical element in recording and replay. Some business logic is time dependent and requires a repeatable time
reference during replay, otherwise results will be different during each replay run. The event processor has a Clock that
nodes can refer to which will return a repeatable deterministic time during replay. In record mode each event is recorded 
with the current time to the event log sink. In replay mode the standard clock is replaced with a data driven clock.
As a replay log record is read the processor clock is set to the recorded time of the event. This ensures 
time dependent features inside the event processor are deterministic and repeatable during replay. 

The audit log facility provided by Fluxtion is tied into the event processor clock so the audit logs at replay will 
exactly match the production audit log.

# Recording events

To record events in Fluxtion we use the YamlReplayRecordWriter to write events into an event sink. YamlReplayRecordWriter uses the
standard Auditor mechanism provided by Fluxtion to plug into the event flow. An auditor sees the event flow before any
business code and is the perfect injection point for recording an event log. Any registered auditor has access to the 
clock, YamlReplayRecordWriter reads and stores the current clock time with the event log record. 

## Recording event diagram

This is the diagram of our event log recording process for the code sample. 

```mermaid
flowchart TD

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA>InputEvent::PnlUpdate]:::eventHandler
    HandlerA[BookPnl\nEventHandler::PnlUpdate - book1]:::graphNode 
    HandlerB[BookPnl\nEventHandler::PnlUpdate - book AAA]:::graphNode
    
    GlobalPnl:::graphNode
    EventA ---> Auditor
    Auditor[<b>Auditor::YamlReplayRecordWriter</b>] ---->|log with time| id1[(<b>Event log sink</b>)]
    
    subgraph EventProcessor
      clock((clock)) ---|read time| Auditor
      Auditor -->  HandlerA & HandlerB --> GlobalPnl
    end

    
```

# Replaying events
To replay events in Fluxtion we use the YamlReplayRunner to read from an event source. The processor under test operates
with no changes receiving and responding to event input. The clock is replaced at the start of replay run and the time
is set for each replay record and then the event is submitted to the processor. 

## Replaying event diagram

This is the diagram of our event log recording process for the code sample.

```mermaid
flowchart TD

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}

    Replay[<b>YamlReplayRunner</b>] 
    store[(<b>Event log store</b>)]
    
    EventA>InputEvent::PnlUpdate]:::eventHandler
    HandlerA[BookPnl\nEventHandler::PnlUpdate - book1]:::graphNode 
    HandlerB[BookPnl\nEventHandler::PnlUpdate - book AAA]:::graphNode
    GlobalPnl:::graphNode
    
    store --> Replay --> EventA -->  HandlerA & HandlerB
    Replay --->|set time| clock((clock))
    
    subgraph EventProcessor
      clock  
      HandlerA & HandlerB --> GlobalPnl
    end
    
```
# Example
[The example project]({{page.example_src}}) has List of BookPnl nodes that are referenced by the GlobalPnl class. At startup the GlobalPnl is 
bound to the start lifecycle method and prints a csv header to screen:

`time,globalPnl`

Whenever a PnlUpdate is published one the BookPnl's may have changed and then recalculate. If there is a change in a
BookPnl then the GlobalPnl logs a new csv entry to console with the time of the change and the current aggregate Pnl total.
The GlobalPnl uses the Clock instance supplied by the processor which ensures predictable replay behaviour of time.

`14:40:58.794,200`

The goal is to record an event log and then replay this into a brand new instance of GlobalPnlProcessor and see the 
exact same messages printed console

## Recording the event log
The Example project creates an event processor and binds the YamlReplayRecordWriter as an auditor, with the id 
YamlReplayRecordWriter.DEFAULT_NAME. [YamlReplayRecordWriter]({{site.fluxtion_src_compiler}}/replay/YamlReplayRecordWriter.java)
supports blacklist, whitelist or all classes when recording the event log. In this case we are only recording PnlUpdate 
events:

### Bind YamlReplayRecordWriter at build time
The event log writer is bound into the graph at generation time with this code from the builder:

{% highlight java %}
public void buildGraph(EventProcessorConfig processorConfig) {
    processorConfig.addNode(
            new GlobalPnl(Arrays.asList(
                    new BookPnl("book1"),
                    new BookPnl("bookAAA"),
                    new BookPnl("book_XYZ")
            ))
    );
    //Inject an auditor will see events before any node
    processorConfig.addAuditor(
            new YamlReplayRecordWriter().classWhiteList(PnlUpdate.class),
            YamlReplayRecordWriter.DEFAULT_NAME);
}
{% endhighlight %}

This is the line the binds in the YamlReplayRecordWriter
{% highlight java %}
//Inject an auditor will see events before any node
processorConfig.addAuditor(
    new YamlReplayRecordWriter().classWhiteList(PnlUpdate.class),
    YamlReplayRecordWriter.DEFAULT_NAME);
{% endhighlight %}

### Set event log sink at run time
To generate an event log sink we assign a StringWriter to the YamlReplayRecordWriter. The contents of
the StringWriter will be used as an event log replay source. To configure the YamlReplayRecordWriter at runtime we
discover the auditor by name using the event processor api and then assign the writer to the YamlReplayRecordWriter 
instance

{% highlight java %}
YamlReplayRecordWriter yamlReplayRecordWriter = globalPnlProcessor.getAuditorById(YamlReplayRecordWriter.DEFAULT_NAME);
yamlReplayRecordWriter.setTargetWriter(writer);
{% endhighlight %}

## Replaying the event log
Replaying is achieved by passing a new instance of GlobalPnlProcessor and the replay log to [YamlReplayRunner]({{site.fluxtion_src_compiler}}/replay/YamlReplayRunner.java)
and then calling runReplay.

{% highlight java %}
private static void runReplay(String eventLog){
    YamlReplayRunner.newSession(new StringReader(eventLog), new GlobalPnlProcessor())
            .callInit()
            .callStart()
            .runReplay();
}
{% endhighlight %}

## Code sample
See [GeneraEventLogMain]({{page.example_src}}/GeneraEventLogMain.java) in the project for the full source
{% highlight java %}

public class GeneraEventLogMain {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        StringWriter eventLog = new StringWriter();
        //run the processor and capture event log
        System.out.println("CAPTURE RUN:");
        generateEventLog(eventLog);

        //run a replay
        System.out.println("\nREPLAY RUN:");
        runReplay(eventLog.toString());
    }

    private static void generateEventLog(Writer writer) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        GlobalPnlProcessor globalPnlProcessor = new GlobalPnlProcessor();
        globalPnlProcessor.init();

        YamlReplayRecordWriter yamlReplayRecordWriter = globalPnlProcessor.getAuditorById(YamlReplayRecordWriter.DEFAULT_NAME);
        yamlReplayRecordWriter.setTargetWriter(writer);

        globalPnlProcessor.start();
        globalPnlProcessor.onEvent(new PnlUpdate("book1", 200));
        Thread.sleep(250);
        globalPnlProcessor.onEvent(new PnlUpdate("bookAAA", 55));
    }

    private static void runReplay(String eventLog){
        YamlReplayRunner.newSession(new StringReader(eventLog), new GlobalPnlProcessor())
                .callInit()
                .callStart()
                .runReplay();
    }
}
{% endhighlight %}

## Sample log
When the sample is run we can see the output for the replay is exactly the same as the first run. The time is the same
as the clock referenced in GlobalPnl is data driven in the replay run. Lifecycle methods are called in both cases


{% highlight console %}
CAPTURE RUN:
time,globalPnl
14:40:58.794,200
14:40:59.075,255

REPLAY RUN:
time,globalPnl
14:40:58.794,200
14:40:59.075,255
{% endhighlight %}


# Custom record and replay formats

The [YamlReplayRecordWriter]({{site.fluxtion_src_compiler}}/replay/YamlReplayRecordWriter.java) and [YamlReplayRunner]({{site.fluxtion_src_compiler}}/replay/YamlReplayRunner.java) 
are not designed for all cases there are several limitations to these classes:

* Tied to yaml format for encoding, this is slow and creates garbage
* Presumes some sort of blob store exists for the event log
* The event log is not compressed in any way

For a specific use case, such as writing to a nosql db directly, the developer should extend the writer and replay classes 
adding their own implementation requirements. The audit mechanism will allow any classes to be plugged in, there is 
nothing special about the YamlReplayRecordWriter class.
