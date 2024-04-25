---
title: Integrate event processor
has_children: true
nav_order: 7
published: true
---

# Introduction
{: .no_toc }

Building and executing an event processor are independent functions that can run in separate processes. This section
documents integrating an event processor into an application and unit testing the event processor.

There are three steps to use Fluxtion, step 3 is covered here:

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - **Integrate the event processor in the app and feed it events**
{: .fs-4 }

## Using an event processor
{: .no_toc }

Once the event processor has been generated with user methods bound in it can be used by the application. An instance of an
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the bridge between event streams and processing logic, user code connects
the EventProcessor to the application event sources. An application can contain multiple EventProcessors instances, and
routes events to an instance.

{: .warning }
**EventProcessors are not thread safe** a single event should be processed at one time. Application code is responsible 
for synchronizing thread access to an event processor instance. 
{: .fs-4 }

User code interacts with an event processor instance in one of five ways
1. Creating a new processor
2. Input to a processor
2. Output from a processor
3. Control of a processor
4. Query into a processor

![](../images/integration_overview-running.drawio.png)

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Creating a new processor
Creating a new processor is simply an instance of creating a new instance of an AOT processor or using one the in process
methods` Fluxtion.interpret` or `Fluxtion.compile`. 

## Factory method
An event processor instance provides a factory method that creates new instances of the same event processor class. This
can be useful when a multiple instances of the same processor are required but each with different state. Event streams
are sometimes partitioned, each instance of the event processor can be bound to that partitioned stream.

### Code sample
{: .no_toc }

{% highlight java %}

public class FactoryExample {
    public static void main(String[] args) {
        var processor = Fluxtion.compile(new MyPartitionedLogic());
    
        var processor_A = processor.newInstance();
        var processor_B = processor.newInstance();
    
        //Factory method to create new event processor instances
        processor_A.init();
        processor_B.init();
    
        //user partitioning event flow logic
        processor_A.onEvent("for A");
        processor_B.onEvent("for B");
    }
    
    public static class MyPartitionedLogic {
        @OnEventHandler
        public boolean onString(String signal) {
            System.out.println(signal);
            return true;
        }
    }
}

{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
for A
for B
{% endhighlight %}

### Compiled event processor only support
{: .no_toc }
The compiled event processors create bound nodes using their constructors so each compiled event processor has no shared 
references. As an interpreted instance refers to the nodes it has been generated with, it does create new instance of bound nodes,
it cannot create a segregated instance of an event processor. 

{: .warning }
Factory method processor.newInstance() is only supported for compiled event processors
{: .fs-4 }
 
## Partitioning
Fluxtion provides an automatic [partitioning function]({{site.fluxtion_src_runtime}}/partition/Partitioner.java) 
that can be used to query event flow, partition it and assign a new event processor instance to that flow. The partitioner
takes a factory method to create new event processor instances and a key function to separate event flow.

The partitioner will create and initialise a new event processor when the key function returns a previously unseen key. 

### Code sample
{: .no_toc }

{% highlight java %}

public class AutomaticPartitionExample {

    public record DayEvent(String day, int amount) {}

    public static void main(String[] args) {
        var processorFactory = Fluxtion.compile(new DayEventProcessor());

        //Create a partitioner that will partition data based on a property
        Partitioner<StaticEventProcessor> partitioner = new Partitioner<>(processorFactory::newInstance);
        partitioner.partition(DayEvent::day);

        //new processor for Monday
        partitioner.onEvent(new DayEvent("Monday", 2));
        partitioner.onEvent(new DayEvent("Monday", 4));

        //new processor for Tuesday
        partitioner.onEvent(new DayEvent("Tuesday", 14));

        //new processor for Friday
        partitioner.onEvent(new DayEvent("Friday", 33));

        //re-use processor for Monday
        System.out.println();
        partitioner.onEvent(new DayEvent("Monday", 999));
    }

    public static class DayEventProcessor {
        @Initialise
        public void initialise() {
            System.out.println("\nDayEventProcessor::initialise");
        }

        @OnEventHandler
        public boolean onDayaEvent(DayEvent signal) {
            System.out.println(signal);
            return true;
        }
    }
}


{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
DayEventProcessor::initialise
DayEvent[day=Monday, amount=2]
DayEvent[day=Monday, amount=4]

DayEventProcessor::initialise
DayEvent[day=Tuesday, amount=14]

DayEventProcessor::initialise
DayEvent[day=Friday, amount=33]

DayEvent[day=Monday, amount=4]
{% endhighlight %}

# Inputs to a processor
An event processor responds to input by triggering a calculation cycle. Functions are bound to the calculation
cycle to meet the business requirements. Triggering a calculation cycle is covered in this section. To process inputs 
the event processor instance it must be initialised before any input is submitted.

{: .info }
1 - Call EventProcessor.init() before submitting any input<br/>
2 - Each new input triggers a graph calculation cycle
{: .fs-4 }

## Events

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}

{% endhighlight %}

## Exported service

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Signals

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}
### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Batch support

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}
### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

# Outputs from a processor

## Sink

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Audit

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

# Control of a processor

## Lifecycle
## Clocks and time
## Audit log level
## Context parameters

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Runtime inject

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

# Query into a processor

## Node lookup by id

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Streaming node lookup by id

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}

## Auditor lookup by id

### Code sample
{: .no_toc }
{% highlight java %}
{% endhighlight %}

### Sample log
{: .no_toc }

{% highlight console %}
{% endhighlight %}