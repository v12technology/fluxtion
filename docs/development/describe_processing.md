---
title: Describe processing logic
parent: Development
has_children: false
nav_order: 2
published: true
---

# Building the processing graph

Once an event has been received then a set of operations are performed on that event. Operations, like map and filter  
are chained together into a pipeline. Pipelines can diverge and combine to create a directed acyclic graph of operations.
Multiple events types are handled in a processor creating more pipelines that combine to increase the complexity of
the processing graph.

Fluxtion provides two apis to describe the processing logic
- **Streaming api** A functional fluent style api similar to java 8 streams
- **Imperative construction** Annotations are used to mark user classes as memebers of the processing graph

Both apis can be combined within the same EventProcessor.

Injected instances are also supported with the imperative construction. 