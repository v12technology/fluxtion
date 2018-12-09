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

### Capabilities
<details>
  <summary>Event processing support</summary>
  
*  
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
</details>

<details>
  <summary>High performance</summary>
  
*   
   * Process hundreds of millions of events per second per core
   * Optimal pre-calculated execution path generation.
   * Zero gc
   * Cache optimised
   * JIT friendly code
   * Type inference, no auto-boxing primitive access.
</details>

<details>
  <summary>Developer Friendly</summary>
  
*  
   * Processing inference, no error prone separate graph description required.
   * Easy to use annotation based api for build-time.
   * Multi-language targets from one model, eg C++ processor from Java model.
   * Seamlessly integrate declarative and imperative processing in one processor.
   * Supports dependency injection.
  </details>

<details>
  <summary>Auditing</summary>
  
* 
   *  Auditors record event and node execution paths for post processing analysis.
   *  graphml and png are generated as well as code. 
   *  Audit records are in a structured machine friendly form. 
   *  Graphml and audit records loaded into the visualiser for analysis.
   *  Dynamic property tracing using reflection.
   *  Auditors can record performance and profile systems or individual nodes.
  </details>

<details>
  <summary>Plugins</summary>
  
*  
   * Text processing
   * Csv processing
   * Complex event processing joins, group by, aggregates, windows
   * Statistical functions
   * State machine
   * Functional support
  </details>

<details>
  <summary>Deployment</summary>
  
*   
   * Designed to be embedded
   * Use within any java process from j2me to servers.
    </details>

<details>
  <summary>Multiple model definitions</summary>
  
*  
   * Imperative
   * Declarative
   * Dependency injection via annotation
   * Data driven configuration via yml, xml or spring.xml
   * Bespoke strategies
  </details>

<details>
  <summary>Source code as an asset</summary>
  
*  
   * Variable naming strategy for human readable code
   * Audit friendly, prevents runtime dynamism.
   * Simplifies problem resolution, no hidden libraries.
   * Explicit generated code combats concryption – encryption by configuration.
  </details>

<details>
  <summary>Dynamic programming</summary>
  
*  
   * Generated parsers
   * Optimised functions generated conditioned upon variants.
  </details>

<details>
  <summary>Generative programming</summary>
  
*  
   * Function generation
   * Type inference, no autoboxing for primitives.
   * Handler generation from processing inference.
   * Core template customisation.
   * Zero gc logger statically generated.
  </details>

<details>
  <summary>Tool support</summary>
  
*  
   * Maven plugin
   * GraphML xml output
   * Visualiser/analyser
  </details>

## Example
The steps to integrate fluxtion static event processor(SEP) into a system using an imperative form:

### Step 1 
Create events and processing nodes in code. Use annotations to mark callback methods. These classes will be used by your application.

<details>
  <summary>Show me</summary>
Todo
  
  
</details>

### Step 2 
Write a SEPConfig that binds instances together into an object graph, this class will be used by Fluxtion generator.

<details>
  <summary>Show me</summary>
Todo
  
  
</details>

### Step 3 
In your pom use the fluxtion maven plugin, specifying SEPConfig class, output package and class name. Inovkes the fluxtion generator to generate a SEP.
<details>
  <summary>Show me</summary>
Todo
  
  
</details>

### Step 4
Use the generated SEP in your code/tests
<details>
  <summary>Show me</summary>
Todo
  
  
</details>



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

## License
Fluxtion is licensed under the [Server Side Public License](https://www.mongodb.com/licensing/server-side-public-license). This license is created by MongoDb, for further info see [FAQ](https://www.mongodb.com/licensing/server-side-public-license/faq) and comparison with [AGPL v3.0](https://www.mongodb.com/licensing/server-side-public-license/faq).

## Philosophy
We generate code and not byte code for several reasons: 
* Most project costs are spent in maintenance which is easier and faster if the code is accessible. 
* There are no Fluxtion servers to deploy, the generated code easily integrates into existing applications.
* Fluxtion supports complex constructs, such as recursive compilation, that would be difficult to develop in byte code alone.


## Maintenance tools 

The visualiser tool can load any graphml file created by Fluxtion for inspection. 
![Visualiser image](images/visualiser_1.png)

## Contributing
We welcome contributions to the project. Detailed information on our ways of working can be found here. In brief:

* Sign the [Fluxtion Contributor Licence Agreement](https://github.com/v12technology/fluxtion/blob/master/contributorLicenseAgreement);
* Push your changes to a fork;
* Submit a pull request.


**This README is a work in progress and will be updating regularly**
