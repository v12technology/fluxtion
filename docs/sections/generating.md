---
title: Event processor building
has_children: true
nav_order: 6
published: true
---

# Introduction
Building and executing an event processor are independent functions that can run in separate processes. This section documents the  
binding of functions into an event processor and the generation of the processor for use at runtime. Integrating 
ahead of time compilation of an event processor with build tools is also described.

![](../images/integration_overview-binding_generating.drawio.png)


## Binding
The compiler analyses the configuration information provided by the programmer and builds a code model that provides all
the information required to generate the event processor. Several source types are supported for supplying the binding
information, Fluxtion transforms each of these sources into a common representation before generating the code model. 
Supported source config types:
* Programmatic
* Yaml based
* Spring based
* Functional

## Generating
To complete building an event processor the code model and a target runtime is passed to the compiler. The final event
processor binds in all user classes combined with pre-calculated event dispatch to meet the dispatch rules.

The supported target runtime are:

- In memory execution running in an interpreted mode
- In memory execution of compiled event processor
- Ahead of time generation of compiled event processor
