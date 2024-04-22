---
title: Processor generation
parent: Event processor building
has_children: false
nav_order: 2
published: true

aot_advantage:
  "Fast execution<br/>
  Supports 100's nodes<br/>
  zero gc<br/>
  easy to debug"

compile_advantage:
  "Fast execution<br/>
  Supports 100's nodes<br/>
  Dynamic graph with optimal performance<br/>
  zero gc<br/>
  easy to debug
  "

interpreted_disadvantage:
  "Slower execution<br/>
  produces garbage<br/>
  More runtime libraries<br/>
  Harder to debug. map based dispatch"

compile_disadvantage:
  "More runtime libraries<br/>
  Limit on generated code size"
---

# Generating an event processor

{: .no_toc }

The Fluxtion compiler provides several tools for generating an event processor from a model supplied by the client.
This section documents the generation utilities and tools available for event processor generation and how to use them. 
The binding of nodes to the event processor is covered in [binding user classes](binding_user_classes)

**The [Fluxtion]({{site.Fluxtion_link}}) class gives static access to the compile methods.**

![TEST](../../images/integration_overview-generating.png)

## Generating modes

To complete building an event processor the code model and a generation mode is passed to the compiler. The final event
processor binds in all user classes combined with pre-calculated event dispatch to meet the dispatch rules.

Supported generation modes

- **Interpreted** In memory interpreted mode
- **In memory compilation** of an event processor
- **Ahead of time compilation** of an event processor

Regardless of which generation mode is used the generated event processor will behave the same with respect to the 
dispatch rules.

## Generation modes comparison

| Mode                      | Required libraries   | Advantage                                  | Disadvantages                     | Use case |
|---------------------------|----------------------|--------------------------------------------|-----------------------------------|----------|
| Interpreted               | compiler<br/>runtime | Supports 1000's nodes<br/>Quick to develop | {{page.interpreted_disadvantage}} |          |
| In memory compilation     | compiler<br/>runtime | {{page.compile_advantage}}                 | {{page.compile_disadvantage}}     |          |
| Ahead of time compilation | runtime              | {{page.aot_advantage}}                     | Limit on generated code size      |          |

# Interpreted
The interpreted mode implements the event processor with a map based dispatch. Generation and execution all happen in the
same process so running process requires access to both the runtime and compiler Fluxtion libraries. 

[Fluxtion.interpret]({{site.Fluxtion_link}}) methods is the entry point to generating an interpreted version of the 
event processor. 

A user can either supply a varargs list of objects to include in the final event processor, or configure a supplied [EventProcessorConfig]({{site.EventProcessorConfig_link}})
and imperatively add nodes. 

# Compiling
The compiling mode generates a source file that represents the event processor, ready for compilation and use within sn
application. The source generation and compilation process can happen either in process or as part of the build stage.

The generate source file is a serialised state of the event processor and all the instances it manages. This places stricter
requirements on the nodes than running in interpreted mode.

- Instances must have a public constructor or constructors
- Final non-transient fields must be assigned in the constructor
- Transient fields are not serialised to the event processor source file
- Serialized fields must be supported by Fluxtion
  - All standard types are supported
  - With a zero argument constructor
  - For complex construction add a custom serializer with [`EventProcessorConfig.addClassSerialize`]({{site.EventProcessorConfig_link}})

## To be documented

- AOT building
- Interpreted
- Programmatic api
- Spring support
- Yaml support
- Maven plugin
- Serialising AOT
    - final/transient
    - constructor
    - getter/setter
    - public
    - collection support
    - Default serialisers
    - Custom serialisers
    - New instance

