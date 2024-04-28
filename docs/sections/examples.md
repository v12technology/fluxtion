---
title: Examples
has_children: true
nav_order: 10
published: true
---

# Introduction

A set of examples that explore the usage of Fluxtion in a variety of scenarios. All examples are on github as a single 
project, cloning the repo will help the reader explore the code locally and improve the learning experience.

## Executing and event processor

All projects that build a Fluxtion [EventProcessor]({{site.EventProcessor_link}}) at runtime follow similar steps

1. **Mark event handling methods with annotations or via functional programming**
  - Create a maven or gradle project adding the Fluxtion compiler dependency to the project runtime classpath
  - Write pojo's that will provide event driven logic, set references between the pojo's as per normal java
  - [Annotate]({{site.fluxtion_src_runtime}}/annotations/) a method to indicate it is an event handling entry pont or a callback trigger method
  - Export a service, any exported methods are entry points to the event processor
2.  **Build the event processor using fluxtion compiler utility**
   - Calling one of the [Fluxtion]({{site.Fluxtion_link}}) compile/interpret methods passing in the list of nodes to the builder method. 
   - An EventProcessor instance is returned ready to be used
3. **Integrate the event processor in the app and feed it events**
  - Call EventProcessor.init() to ensure the graph is ready to process events
  - To publish events to the processor call EventProcessor.onEvent(object)
  - To call exported service functions on the event processor
        - Lookup the exported service with EventProcessor.getExportedService()
        - Store the service reference and call methods on it
