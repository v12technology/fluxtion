<p align="center">
  <img width="270" height="200" src="images/Fluxtion_logo.png">
</p>

[![Build Status](https://travis-ci.org/v12technology/fluxtion.svg?branch=master)](https://travis-ci.org/v12technology/fluxtion)

## Overview
Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further. 

Fluxtion is a code generator that automates the production of event stream processing logic. The generated code is self-contained and designed to sit within an application. The application delivers events to the Fluxtion generated Static Event Processor for stream processing.

Low latency, easy maintenance, zero gc, complex graph processing, simplified development and the "wow you can do that!!" reaction are the principles that guide our project. 

As a stretch goal we would like to be the [fastest single threaded java stream processor](https://github.com/v12technology/fluxtion-quickstart/blob/master/README.md#run) on the planet. 

## What are we solving
Fluxtion is focused on optimising the implementation of stream processing logic. Other stream processors support marshalling, distributed processing, event ordering and a multitude of other features. Fluxtion presumes there is an event queue it can drain, and concentrates solely on delivering correct and optimal execution of application logic. 

Want to upgrade your application logic without rewriting your infrastructure? Fluxtion is the perfect solution for you.

## Example
Quick start example to go here

### Capabilities
*  Event processing support
   * Batching or Streaming
   * Lifecycle – init, terminate, after event
   * Push and pull model
   * Configurable conditional branching
   * Handles complex graphs of thousands of nodes.
   * Event filtering
     * Event type
     * Event type and static annotation value
     * Event type and instance variable value
   * Parent change identification
   * Simple Integration of user functions
   * Stateful or stateless
*  High performance 
   * Process hundreds of millions of events per second per core
   * Optimal pre-calculated execution path generation.
   * Zero gc
   * Cache optimised
   * JIT friendly code
   * Type inference, no auto-boxing primitive access.
*  Developer Friendly
   * Processing inference, no error prone separate graph description required.
   * Easy to use annotation based api for build-time.
   * Multi-language targets from one model, eg C++ processor from Java model.
   * Seamlessly integrate declarative and imperative processing in one processor.
   * Supports dependency injection.
* Auditing
   *  Auditors record event and node execution paths for post processing analysis.
   *  graphml and png are generated as well as code. 
   *  The graphml can be loaded into the visualiser for analysis.
   *  Audit records are in a structured machine friendly form. 
   *  Dynamic property tracing using reflection.
   *  Auditors can record performance for events and profile systems or individual nodes. 
*  Plugins
   * Text processing
   * Csv processing
   * Complex event processing joins, group by, aggregates, windows
   * Statistical functions
   * State machine
   * Functional support
*  Deployment 
   * Designed to be embedded
   * Use within any java process from j2me to servers.
*  Multiple input definition
   * Imperative
   * Declarative
   * Dependency injection via annotation
   * Data driven configuration via yml, xml or spring.xml
   * Bespoke strategies
*  Source code as an asset
   * Variable naming strategy for human readable code
   * Audit friendly, prevents runtime dynamism.
   * Simplifies problem resolution, no hidden libraries.
   * Explicit generated code combats concryption – encryption by configuration.
*  Dynamic programming
   * Generated parsers
   * Optimised functions generated conditioned upon variants.
*  Generative programming
   * Function generation
   * Type inference, no autoboxing for primitives.
   * Handler generation from processing inference.
   * Core template customisation.
   * Zero gc logger statically generated.
*  Tool support
   * Maven plugin
   * GraphML xml output
   * Visualiser/analyser

## Graph processing primer

In a stream processor events are received and processed with predictable results. A set of dependent behaviours can be modelled as a directed acyclic graph. Each behaviour is a node on the graph and in our case these behaviours are functions. For predictable processing to hold true we can say the following:

*  The first node on an execution path is an event handler.
*  An execution path is formed of nodes that have the first event handler as a parent.
*  Functions are nodes on the execution path.
*  An event handler is a node that accepts an incoming event for processing.
*  Functions will always be invoked in execution path order.
*  Execution path order is topologically sorted such that all parent nodes are invoked before child nodes.
*  A child node will be given a valid reference to each parent dependency before any event processing occurs.


![example graph](images/Execution_graph_paths.png)

For the example above:
*  **Event graph:** Node 1, Node 2, Node 3, Node 4, Node 10, Node 11
*  **Event handlers:** Node 1, Node 10
*  **Execution Paths:**
   * Event A: Node 1, Node 2, Node 3 Node 4, Node 11
   * Event B: Node 10, Node 11

## Motivation
Fluxtion is a tool for generating high performance event stream processing applications. 
The ideas behind Fluxtion have been used extensively in the low latency high 
frequency trading space, where low response time for complex calculation graphs 
is the main requirement.

Uniquely among stream process applications Fluxtion interjects in the standard build 
process and seamlessly adds a second compiler stage. The generated code is optimised
for performance and low memory usage. 

If you need to process multiple event types, each with a unique execution path,
producing multiple outputs, Fluxtion is for you. It will reduce your development
time, ease your maintenance and cut your processing costs.

A maven plugin is provided that integrates the Fluxtion generator into a standard developer build process. [Quick start](https://github.com/v12technology/fluxtion-quickstart/blob/master/README.md) example fliuxtion java wc, faster than unix wc.

## Philosophy
We generate code and not byte code for several reasons: 
* Most project costs are spent in maintenance which is easier and faster if the code is accessible. 
* There are no Fluxtion servers to deploy, the generated code easily integrates into existing applications.
* Fluxtion supports complex constructs, such as recursive compilation, that would be difficult to develop in byte code alone.


## Maintenance tools 

![Visualiser image](images/visualiser_1.png)

This README is a work in progress and will be updating regularly


