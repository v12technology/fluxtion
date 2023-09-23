---
title: Overview
has_children: false
nav_order: 1
published: false
---

# Introduction

Fluxtion is a java utility that builds embeddable dependency injection containers for use as a low latency event
processor within streaming applications. Developers concentrate on developing and extending business logic, dependency 
injection and event dispatch is handled by the container.

{: .info }
Fluxtion = Dependency injection + Event dispatch
{: .fs-8 }

The Fluxtion container combines construction, instance lifecycle and event dispatch, supporting:

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
used within a single application each container providing specialised processing. 

## Automatic event dispatch

The container exposes event consumer end-points, routing events as methods calls to beans within the container
via an internal dispatcher. The internal dispatcher propagates event notification through the object graph.

Fluxtion leverages the familiar dependency injection workflow for constructing the object graph. Annotated
event handler and trigger methods are dispatch targets. When building a container Fluxtion uses the annotations to
calculate the dispatch call trees for the internal dispatcher. A bean can export multiple service interfaces or just a
single method. For exported interfaces the container generates proxies that routes calls from the proxy handler methods
to the container's dispatcher.
