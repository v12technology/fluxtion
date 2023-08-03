---
title: Overview
has_children: true
nav_order: 1
published: true
---

# Introduction to Fluxtion

Fluxtion is a java utility that builds embeddable dependency injection containers for use as a low latency event
processor within streaming applications. The Fluxtion DI container combines construction, instance lifecycle and event
dispatch, supporting:

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

{: .note }
Developers concentrate on developing and extending business logic, dependency injection and event dispatch is handled by the
container.

## Dependency injection container
Fluxtion builds a dependency injection container from configuration information given by the programmer. Functions 
supported by the container include: creating instances, injecting references between instance beans, setting properties
on beans, calling lifecycle methods, factory methods, singleton injection, named references, constructor and setter 
injection. Configuration data can be programmatic, spring xml config, yaml or custom data format.

The cointainer can be built in one of three ways:
- Interpreted - built and run in process, uses dynamic dispatch can handle millions of nodes
- Compiled - static analysis, code generated and compiled in process. handles thousands of nodes
- Compiled AOT - code generated at build time, zero cost start time when deployed

Fluxtion DI containers are very lightweight and designed to be run within an application. Multiple containers can be 
used within a single application providing specialised processing requirements. 

## Automatic event dispatch

The container exposes event consumer end-points, routing events as methods calls to beans within the container
via an internal dispatcher. The internal dispatcher propagates event notification through the object graph.

Fluxtion leverages the familiar dependency injection workflow for constructing the object graph. Annotated
event handler and trigger methods are dispatch targets. When building a container Fluxtion uses the annotations to
calculate the dispatch call trees for the internal dispatcher. A bean can export multiple service interfaces or just a
single method. For exported interfaces the container generates proxies that routes calls from the proxy handler methods
to the container's dispatcher.

## Event sourcing

If an event sourcing architectural style is followed the application behaviour will be completely reflected in the
test environment. Data driven clocks and audit logs tracing method call stacks are supported, when combined with event
replay this gives the developer a powerful and easy to use toolset for supporting a deployed system.

## Fluxtion dependencies

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

### Dependency description

| Fluxtion dependency | Example use                             | Description                                           | 3rd party<br/> dependencies |
|---------------------|-----------------------------------------|-------------------------------------------------------|-----------------------------|
| Compiler            | Fluxtion#interpret<br/>Fluxtion#compile | Generates the EventProcessor <br/> from a description | Many                        |
| Runtime             | EventProcessor#onEvent                  | Runtime dispatch of events and helper libraries       | None                        |

It is possible to use one of the [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) compileAOT methods to generate an
EventProcessor ahead of time. An aot generated event processor only requires the runtime library on the classpath. In 
this case set the scope of the compiler dependency to provided in maven.

<script>
document.getElementById("defaultOpen").click();
</script>