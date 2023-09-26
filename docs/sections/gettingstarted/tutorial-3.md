---
title: 3rd tutorial - auditor
parent: Getting started
has_children: false
nav_order: 3
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
This tutorial is an introduction to monitoring the dependency injection container at runtime, we call this **auditing**. 
The reader should be proficient in Java, maven, git, Spring and have completed the [second lottery tutorial](tutorial-2.md) before 
starting this tutorial. The project source can be found [here.]({{site.getting_started}}/tutorial2-lottery-aot)

Our goal is to create a custom monitoring class that will observe the event processing without any changes to the 
application code, and record the following statistics:

- Node stats, method invocation count grouped by bean instance
- Event stats, method invocation count grouped by exported method
- Node method stats, method invocation count grouped by bean instance and method

At the end of this tutorial you should understand:

- The role of the Auditor interface in Fluxtion
- How the auditor is notified by the container
- How to implement a custom auditor
- How to load a custom auditor in the container


# Auditing concepts
As event systems grow more complex monitoring tools are useful to monitor critical metrics giving early
warning of potential problems. It is preferable to develop monitoring separately to application code so that we do not
accidentally add bugs to business functionality and we can add or remove monitoring to the application without affecting
changing functional behaviour.

In Fluxtion we achieve this through the Auditor interface. Client code implements the Auditor interface, register the
auditor instance at build time, the instance receives container notifications with event meta data attached. The client 
code is free to process the event metadata in anyway it wants.

## Auditor notifications
The Auditor interface is copied below with javadoc comments removed

{% highlight java %}
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;

public interface Auditor extends Lifecycle {

  default boolean auditInvocations() {
    return false;
  }

  @Override
  default void init() {}

  void nodeRegistered(Object node, String nodeName);

  default void eventReceived(Event event) {}

  default void eventReceived(Object event) {}

  default void nodeInvoked(Object node, String nodeName, String methodName, Object event) {}

  default void processingComplete() {}

  @Override
  default void tearDown() {}

}
{% endhighlight %}

Methods in the interface are called by the container at runtime except auditInvocations, which is called at build time.
The callback nodeInvoked is called for every method invoked on every bean, and could have adverse effects on performance,
auditInvocations controls the whether this monitoring methiod is bound at runtime.

Javadoc is attached to the class for detail reading the important highlights are:

-  nodeRegistered called before any lifecycle methods, allows the auditor to build a map at startup
-  eventReceived called when a service call or event is received by the container. Precedes any bean method calls
-  nodeInvoked called before any event or trigger method in the event processing cycle

## Implementing and auditor

## Loading an auditor into the container

# Building the application

# Running the application

# Conclusion

