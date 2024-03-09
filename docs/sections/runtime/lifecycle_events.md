---
title: Lifecycle callback
parent: Runtime execution
has_children: false
nav_order: 2
published: true
---

# Processing lifecycle callbacks
{: .no_toc }

The section documents the lifecycle api and its behaviour. 

Classes bound into an [EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java) register for lifecycle callbacks with annotations on methods. 
A [Lifecycle]({{site.fluxtion_src_runtime}}/lifecycle/Lifecycle.java) interface is implemented by the generated EventProcessor, lifecycle method calls are routed to 
annotated callback methods.

The source project for the examples can be found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/lifecycle)

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

## Lifecycle - init
`EventProcessor#init` Calls init on any node in the graph that has registered for an init callback. The init calls
are invoked in topological order.

## Lifecycle - teardown
`EventProcessor#tearDown` Calls tearDown on any node in the graph that has registered for an tearDown callback.
The tearDown calls are invoked reverse topological order.

## Lifecycle - start
`EventProcessor#start` Calls start on any node in the graph that has registered for an onStart callback. The start calls
are invoked in topological order. Start must be called after init

## Lifecycle - stop
`EventProcessor#stop` Calls stop on any node in the graph that has registered for an onStop callback.
The stop calls are invoked reverse topological order.

## Attaching a user node to lifecycle callback
User nodes that are added to the processing graph can attach to the lifecycle callbacks by annotating methods with 
the relevant annotations.

{% highlight java %}
public static class MyNode {

    @Initialise
    public void myInitMethod() {
        System.out.println("Initialise");
    }

    @Start
    public void myStartMethod() {
        System.out.println("Start");
    }

    @Stop
    public void myStopMethod() {
        System.out.println("Stop");
    }

    @TearDown
    public void myTearDownMethod() {
        System.out.println("TearDown");
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.start();
    processor.stop();
    processor.tearDown();
}
{% endhighlight %}

Output
{% highlight console %}
Initialise
Start
Stop
TearDown
{% endhighlight %}

