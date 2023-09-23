<p align="center">
    <a href="https://v12technology.github.io/fluxtion/">
        <img width="270" height="200" src="images/Fluxtion_logo.png">
    </a>
</p>

[![Github build](https://github.com/v12technology/fluxtion/workflows/MavenCI/badge.svg)](https://github.com/v12technology/fluxtion/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime)

## [User documentation](https://v12technology.github.io/fluxtion/)



# Fluxtion is event driven Java

---

Fluxtion is a java development productivity tool that makes writing and maintaining event driven business logic cheaper
and quicker. The Fluxtion dependency injection container exposes user beans as event driven service endpoints. A
container instance can be connected to any event delivery system freeing the business logic from messaging vendor lock-in.

**Fluxtion minimises the cost of developing and maintaining event driven business logic**

Developers concentrate on developing and extending business logic, dependency injection and realtime event dispatch is
handled by the container. The container supports:

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

# Fluxtion components
There are two major components provided by Fluxtion the developer uses to develop with.

## Compiler
The compiler analyses the configuration information provided by the programmer and builds a dependency injection container
that houses all the user components or beans combined with pre-calculated event dispatch. Outputs from the compiler 
are either 
- In memory di container running in an interpreted mode
- A container generated ahead of time and serialised to code 

## Runtime
The runtime provides the dependency injection container with a core set of libraries required at runtime. An AOT generated
container only requires the runtime to function. The compiler is only required

# Philosophy
Our philosophy is to make delivering streaming applications in java simple by employing a
clean modern api that requires very little integration effort. The Fluxtion compiler carries the
burden of generating simple efficient code that is optimised for your specific application.
We pay the cost at compile time only once, so every execution of your stream processor sees
benefits in reduced startup time and smaller running costs.

# The cost of complexity problem

Increasing system complexity makes delivery of new features expensive and time-consuming to deliver. Efficiently managing
complexity reduces both operational costs and time to market for new functionality, critical for a business to remain
profitable in a competitive environment.

Event driven systems have two types of complexity to manage:

- Delivering events to application components in a fault-tolerant predictable fashion.
- Developing application logic responses to events that meets business requirements

Initially all the project complexity centres on the event delivery system, but over time this system becomes stable and
the complexity demands are minimal. Pre-packaged event delivery systems are a common solution to control complexity and
cost of event distribution. The opposite is true for event driven application logic, functional requirements increase
over time and developing application logic becomes ever more complex and expensive to deliver.

**Fluxtion combines dependency injection and event dispatch increasing developer productivity**

# Combining dependency injection and event processing

The introduction of dependency injection gave developers a consistent approach to linking application components.
Fluxtion extends dependency injection to support container managed event driven beans. Extending a familiar development
pattern has the following benefits:
- Shallow learning curve for developers to use Fluxtion effectively
- Consistent programming model for event driven logic increases developer productivity
- Re-use of industrial quality and predictable event dispatch model

**Fluxtion's familiar dependency injection programming model simplifies integration**

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
used within a single application each container providing specialised business processing logic.

## Automatic event dispatch

The container exposes event consumer end-points, routing events as methods calls to beans within the container
via an internal dispatcher. The internal dispatcher propagates event notification through the object graph.

Fluxtion leverages the familiar dependency injection workflow for constructing the object graph. Annotated
event handler and trigger methods are dispatch targets. When building a container Fluxtion uses the annotations to
calculate the dispatch call trees for the internal dispatcher. A bean can export multiple service interfaces or just a
single method. For exported interfaces the container generates proxies that routes calls from the proxy handler methods
to the container's dispatcher.




## Code sample
```java
/**
 * Simple Fluxtion hello world stream example. Add two numbers and log when sum > 100
 * <ul>
 *     <li>Subscribe to two event streams, Data1 and Data1</li>
 *     <li>Map the double values of each stream using getter</li>
 *     <li>Apply a stateless binary function {@link Double#sum(double, double)}</li>
 *     <li>Apply a filter that logs to console when the sum > 100</li>
 * </ul>
 */
public class HelloWorld {
    public static void main(String[] args) {
        //builds the EventProcessor
        EventProcessor eventProcessor = Fluxtion.interpret(cfg -> {
            var data1Stream = subscribe(Data1.class)
                    .console("rcvd -> {}")
                    .mapToDouble(Data1::value);

            subscribe(Data2.class)
                    .console("rcvd -> {}")
                    .mapToDouble(Data2::value)
                    .map(Double::sum, data1Stream)
                    .filter(d -> d > 100)
                    .console("OUT: sum {} > 100");
        });
        //init and send events
        eventProcessor.init();
        //no output < 100
        eventProcessor.onEvent(new Data1(20.5));
        //no output < 100
        eventProcessor.onEvent(new Data2(63));
        //output > 100 - log to console
        eventProcessor.onEvent(new Data1(56.8));
    }

    public record Data1(double value) {
    }

    public record Data2(double value) {
    }
}
```

## Execution output
```text
rcvd -> Data1[value=20.5]
rcvd -> Data2[value=63.0]
rcvd -> Data1[value=56.8]
OUT: sum 119.8 > 100

Process finished with exit code 0
```





# Contributing
We welcome contributions to the project. Detailed information on our ways of working will
be written in time. In brief our goals are:

* Sign the [Fluxtion Contributor Licence Agreement](https://github.com/v12technology/fluxtion/blob/master/contributorLicenseAgreement).
* Author a change with suitabke test case and documentation.
* Push your changes to a fork.
* Submit a pull request.
# License
Fluxtion is licensed under the [Server Side Public License](https://www.mongodb.com/licensing/server-side-public-license).
This license is created by MongoDb, for further info see [FAQ](https://www.mongodb.com/licensing/server-side-public-license/faq)
and comparison with [AGPL v3.0](https://www.mongodb.com/licensing/server-side-public-license/faq).

