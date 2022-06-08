---
title: Build an EventProcessor
has_children: false
nav_order: 3
published: true
---

# Build an EventProcessor

An instance of an 
[EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java)
is the binding point between event streams and processing logic, user code connects the EventProcessor to the 
application event sources.

To make a usable the EventProcessor instance the processing model described needs to be analysed and converted into
an EventProcessor. It is the job of the 
[Fluxtion](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/compiler/src/main/java/com/fluxtion/compiler/Fluxtion.java)
class to carry ou that conversion, through two utility methods:

**compile**: this generates a java source code version of the EventProcessor. The file is compiled in process and used
   to handle events. Total nodes are limited to the number of elements a source file can handle
```java
EventProcessor eventProcessor = Fluxtion.compile(cfg -> EventFlow.subscribe(String.class));
```
**interpret**: Creates an in memory model of the processing backed with data structures. Can support millions of nodes
```java
EventProcessor eventProcessor = Fluxtion.interpret(cfg -> EventFlow.subscribe(String.class));
```

## Meta-data
While generating the EventProcessor meta-data documents are generated in the src/resources directory of the project
using the fully qualified name of the builder class/builder method:
- A png file showing the processing graph
- A graphml document describing the processing graph structure