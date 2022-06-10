<p align="center">
    <a href="https://v12technology.github.io/fluxtion/">
        <img width="270" height="200" src="images/Fluxtion_logo.png">
    </a>
</p>

[![Github build](https://github.com/v12technology/fluxtion/workflows/MavenCI/badge.svg)](https://github.com/v12technology/fluxtion/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/runtime)


# Lightweight event stream processor
- Pure java in memory complex event processing
- Ultra fast [sub-microsecond response times](http://fluxtion.com/solutions/high-performance-flight-analysis/)
- Ahead of time compiler for fast startup and easy embedding

# Introduction
Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further.

Fluxtion is a fully featured java based event stream processor that brings real-time data processing
inside your application. If you need to build applications that react to complex events and make
fast decisions then Fluxtion is for you. We build stream processing logic free from any messaging
layer.

Whether you need to process tens of millions of events per
second or write complex rule driven applications that make decisions in microseconds Fluxtion can help.
Built to embed within an applications, invoking user functions as well as publishing data results.

Uniquely among stream processors Fluxtion employs ahead of time compilation to create a stream processing engine.
Describe your processing and Fluxtion tailors a solution to your needs at build time.
Ahead of time compilation offers several critical advantages over existing products,
- Faster startup times for your application, perfect for serverless architectures
- No vendor lock-in, the engine can be used within any java application
- Compiler optimized code gives higher performance and lower running costs
- Generated source code simplifies debugging and maintenance

# Uses
- Real-time analytics and processing
- ETL
- Rules engines
- Low response time requirements
- IoT processing

## Fluxtion application integration
![](docs/images/integration-overview.png)

A Fluxtion event processor embeds within a user application, processing events,
publishing events to sinks or interacting with user classes. Events are feed from
the application directly into the processor or into a pipeline. A pipeline provides
additional capabilities such as threading, scheduling, auditing, access control

## Philosophy
Our philosophy is to make delivering streaming applications in java simple by employing a
clean modern api similar to the familiar Java streams api. The Fluxtion compiler carries the
burden of generating simple efficient code that is optimised for your specific application.
We pay the cost at compile time only once, so every execution of your stream processor sees
benefits in reduced startup time and smaller running costs.

Why concentrate solely on the processing logic? There are many great messaging systems
out there offering scale out to hundreds of millions of events per second. But many reactive
applications do not need that scale, the problem is integrating the event streams from
different messaging systems into a single decision making engine. In cases like these
you want to concentrate on writing the logic.

## Example
We have a five minute tutorial to dive into [here](https://github.com/v12technology/fluxtion-quickstart/tree/master).

The sample below demonstrates the fluent functional api Fluxtion provides to describe data processing logic. The api
should be familiar to anyone who has coded with java 8 streams.

### Code sample
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

### Execution output
```text
rcvd -> Data1[value=20.5]
rcvd -> Data2[value=63.0]
rcvd -> Data1[value=56.8]
OUT: sum 119.8 > 100

Process finished with exit code 0
```


## Highlights
### Ahead of time compiler
Fluxtion constructs a model of the stream processor and generates a set of java classes
that meet the requirement. The compiled code is highly optimised for memory and cpu. Small,
compact and jit friendly flxution stream processors get the best out of the JVM, giving
unbeatable performance.
### Pipeline vs graph processing
Fluxtion is built as a graph processor and not a pipeline. A pipeline has a single entry
point and single execution path, a graph processor has multiple entry points multiple execution
paths. Handling heterogeneous event types in a unique fashion is the default behaviour.
In fact the more complex the problem the greater the advantage that Fluxtion displays.
### Integrating with client code
Traditional stream processors have an ingest, transform and publish cycle. When moving
from analytics to actually taking actions there is a barrier to integrating the output
with the client application. With Fluxtion client code is integrated into the generated
processor and invoked directly.
### Describing a processor
Fluxtion constructs an intermediate representation for the ahead of time compiler to process.
The intermediate representation can be built from a variety of forms each with their
own advantages. The following descriptions are supported:
- Declarative or DSL
- Imperative
- Data driven
- Dependency injection based

## Contributing
We welcome contributions to the project. Detailed information on our ways of working will
be written in time. In brief our goals are:

* Sign the [Fluxtion Contributor Licence Agreement](https://github.com/v12technology/fluxtion/blob/master/contributorLicenseAgreement).
* Author a change with suitabke test case and documentation.
* Push your changes to a fork.
* Submit a pull request.
## License
Fluxtion is licensed under the [Server Side Public License](https://www.mongodb.com/licensing/server-side-public-license).
This license is created by MongoDb, for further info see [FAQ](https://www.mongodb.com/licensing/server-side-public-license/faq)
and comparison with [AGPL v3.0](https://www.mongodb.com/licensing/server-side-public-license/faq).

