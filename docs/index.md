---
title: Overview
has_children: false
nav_order: 1
published: true
---

# Fluxtion is event driven Java 

---

Fluxtion is a java development productivity tool that makes writing and maintaining event driven business logic cheaper
and quicker. The Fluxtion dependency injection container exposes user beans as event driven service endpoints. A
container instance can be connected to any event delivery system freeing the business logic from messaging vendor lock-in.

{: .info }
Fluxtion minimises the cost of developing and maintaining event driven business logic
{: .fs-4 }

Developers concentrate on developing and extending business logic, dependency injection and realtime event dispatch is
handled by the container. The container supports:

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
  <li><strong>Functional and imperative construction</strong></li>
</ul>
</div>
</div>
</div>

# The cost of complexity problem

Increasing system complexity makes delivery of new features expensive and time-consuming to deliver. Efficiently managing
complexity reduces both operational costs and time to market for new functionality, critical for a business to remain
profitable in a competitive environment.

Event driven systems have two types of complexity to manage:

- Delivering events to application components in a fault-tolerant predictable fashion.
- Developing application logic responses to events that meets business requirements

Initially all the project complexity centres on the event delivery system, but over time this system becomes stable and
the complexity demands are minimal. Pre-packaged event delivery systems are a common solution to control complexity and
cost of event distribution. The opposite is true for event driven application logic, functional requirements increase
over time and developing application logic becomes ever more complex and expensive to deliver.

{: .info }
Fluxtion combines dependency injection and event dispatch increasing developer productivity
{: .fs-4 }

# Combining dependency injection and event processing

The introduction of dependency injection gave developers a consistent approach to linking application components. 
Fluxtion extends dependency injection to support container managed event driven beans. Extending a familiar development
pattern has the following benefits:
- Shallow learning curve for developers to use Fluxtion effectively
- Consistent programming model for event driven logic increases developer productivity
- Re-use of industrial quality and predictable event dispatch model

{: .info }
Fluxtion's familiar dependency injection programming model simplifies integration
{: .fs-4 }

## Dependency injection container

Fluxtion builds a dependency injection container from configuration information given by the programmer. Functions
supported by the container include: creating instances, injecting references between beans, setting properties, calling
lifecycle methods, factory methods, singleton injection, named references, constructor and setter injection.
Configuration data can be programmatic, spring xml config, yaml or custom data format.

There are three options for building a container:

- Interpreted - built and run in process, uses dynamic dispatch can handle millions of nodes
- Compiled - static analysis, code generated and compiled in process. handles thousands of nodes
- Compiled AOT - code generated at build time, zero cost start time when deployed

Fluxtion DI containers are very lightweight and designed to be run within an application. Multiple containers can be
used within a single application each container providing specialised business processing logic.

## Automatic event dispatch

The container exposes event consumer end-points, routing events as methods calls to beans within the container
via an internal dispatcher. The internal dispatcher propagates event notification through the object graph.

Fluxtion leverages the familiar dependency injection workflow for constructing the object graph. Annotated
event handler and trigger methods are dispatch targets. When building a container Fluxtion uses the annotations to
calculate the dispatch call trees for the internal dispatcher. A bean can export multiple service interfaces or just a
single method. For exported interfaces the container generates proxies that routes calls from the proxy handler methods
to the container's dispatcher.

# Latest release

| component | maven central                                                                                                                                                                    |
|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Runtime   | [![Fluxtion runtime](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime)    |
| Compiler  | [![Fluxtion compiler](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/compiler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/compiler) |

## Build dependencies

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

<script>
document.getElementById("defaultOpen").click();
</script>