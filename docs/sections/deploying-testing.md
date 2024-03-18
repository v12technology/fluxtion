---
title: Integration and testing
has_children: true
nav_order: 7
published: true
---

# Introduction

All projects that build a Fluxtion [EventProcessor]({{site.EventProcessor_link}}) at runtime follow similar steps

- Create a maven or gradle project adding the Fluxtion compiler dependency to the project runtime classpath
- Write pojo's that will provide event driven logic, set references between the pojo's as per normal java
- [Annotate]({{site.fluxtion_src_runtime}}/annotations/) a method to indicate it is an event handling entry pont or a callback trigger method
- Build the EventProcessor containg user pojo's by either:
    - Calling one of the [Fluxtion]({{site.Fluxtion_link}}) compile/interpret methods passing in the list of nodes to the builder method
    - Add the Fluxtion maven plugin to your pom.xml for ahead of time compilation(AOT) of builder methods
- An EventProcessor instance is returned ready to be used
- Call EventProcessor.init() to ensure the graph is ready to process events
- To publish events to the processor call EventProcessor.onEvent(object)


**To be completed**