<p align="center">
    <a href="https://v12technology.github.io/fluxtion/">
        <img width="270" height="200" src="images/Fluxtion_logo.png">
    </a>
</p>

[![Github build](https://github.com/v12technology/fluxtion/workflows/MavenCI/badge.svg)](https://github.com/v12technology/fluxtion/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api)

### [Documentation](https://v12technology.github.io/fluxtion/)
# Lightweight event stream processor
 - Pure java in memory fast data processing 
 - Ultra quick sub micro second processing
 - Ahead of time compiler means fast startup and small footprint
 - Built to embed perfect for use in microservices 
 - Batch or streaming
## Code sample
```java
public static void buildSensorProcessor(SEPConfig cfg) {
    //merge csv marshller and SensorReading instance events
    Wrapper<SensorReading> sensorData = merge(select(SensorReading.class),
            csvMarshaller(SensorReading.class).build()).console(" -> \t");
    //group by sensor and calculate max, average
    GroupBy<SensorReadingDerived> sensors = groupBy(sensorData, SensorReading::getSensorName, 
             SensorReadingDerived.class)
            .init(SensorReading::getSensorName, SensorReadingDerived::setSensorName)
            .max(SensorReading::getValue, SensorReadingDerived::setMax)
            .avg(SensorReading::getValue, SensorReadingDerived::setAverage)
            .build();
    //tumble window (count=3), warning if avg > 60 && max > 90 in the window for a sensor
    tumble(sensors, 3).console("readings in window : ", GroupBy::collection)
            .map(SensorMonitor::warningSensors, GroupBy::collection)
            .filter(c -> c.size() > 0)
            .console("**** WARNING **** sensors to investigate:")
            .push(new TempertureController()::investigateSensors);
}
```
From our [five minute tutorial](https://github.com/v12technology/fluxtion-quickstart/tree/master). 
Method references are used throughout, no positional parameters or generic tuples are required. The strongly typing makes the code easier to read, maintain and refactor. See the [generated code and images](https://github.com/v12technology/fluxtion-quickstart/tree/master/src/main/resources/com/fluxtion/quickstart/roomsensor/generated) from the ahead of time compiler.
# Uses
 - Real-time analytics and processing
 - ETL
 - Rules engines
 - Low response time requirements
 - IoT processing
# Introduction
Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further.

Fluxtion is a fully featured java based event stream processor that brings real-time data processing inside your application. If you need to build applications that react to complex events and make fast decisions then Fluxtion is for you. We build stream processing logic free from any messaging layer, there is no lock-in with Fluxtion.

Fluxtion is is easy to use and ultra fast, our sweet spot is either edge processing or single server applications. Whether you need to process tens of millions of events per second or write complex rule driven applications that make decisions in microseconds we can help. Retro fitting real-time calculations into an existing application without requiring wholesale changes to the infrastructure is a great fit. When you need to make decisions and not just calculate then you are in the right place.

Uniquely among stream processors Fluxtion employs ahead of time compilation to create a stream processing engine. Describe your processing and Fluxtion tailors a solution to your needs at build time. Ahead of time compilation offers several critical advantages over existing products, 
 - Ultra fast [sub-microsecond response times](http://fluxtion.com/solutions/high-performance-flight-analysis/)
 - No vendor lock-in, the engine can be used within any java application
 - Compiler optimized code gives higher performance, lower running costs and quicker response times
 - Faster startup times for your application
 - Integrates client logic as a first class citizen 
 - Source code is generated that makes debugging and maintenance easy
 - Meta-data such as images and graphml are created to visualise the process graph
## Example
We have a five minute tutorial to dive into [here](https://github.com/v12technology/fluxtion-quickstart/tree/master).  A client written instance that controls an external system is integrated directly into the generated processor. 
## Philosophy
Our philosophy is to make delivering streaming applications in java simple by employing a clean modern api similar to the familiar Java streams api. The Fluxtion compiler carries the burden of generating simple efficient code that is optimised for your specific application. We pay the cost at compile time only once, so every execution of your stream processor sees benefits in reduced startup time and smaller running costs.

Why concentrate solely on the processing logic? There are many great messaging systems out there offering scale out to hundreds of millions of events per second. But many reactive applications do not need that scale, the problem is integrating the event streams from different messaging systems into a single decision making engine. In cases like these you want to concentrate on writing the logic. 
## Highlights
### Ahead of time compiler
Fluxtion constructs a model of the stream processor and generates a set of java classes that meet the requirement. The compiled code is highly optimised for memory and cpu. Small, compact and jit friendly flxution stream processors get the best out of the JVM, giving unbeatable performance. 
### Pipeline vs graph processing
Fluxtion is built as a graph processor and not a pipeline. A pipeline has a single entry point and single execution path, a graph processor has multiple entry points multiple execution paths. Handling heterogeneous event types in a unique fashion is the default behaviour. In fact the more complex the problem the greater the advantage that Fluxtion displays. 
### Integrating with client code
Traditional stream processors have an ingest, transform and publish cycle. When moving from analytics to actually taking actions there is a barrier to integrating the output with the client application. With Fluxtion client code is integrated into the generated processor and invoked directly. 
### Describing a processor
Fluxtion constructs an intermediate representation for the ahead of time compiler to process. The intermediate representation can be built from a variety of forms each with their own advantages. The following descriptions are supported:
 - Declarative or DSL
 - Imperative
 - Data driven
 - Dependency injection based

## Documentation
Check out detailed documentation at [gitbook](https://fluxtion.gitbook.io/docs/).
This is undergoing active development so please check regularly.
## Contributing
We welcome contributions to the project. Detailed information on our ways of working will be written in time. In brief our goals are:

* Sign the [Fluxtion Contributor Licence Agreement](https://github.com/v12technology/fluxtion/blob/master/contributorLicenseAgreement).
* Author a change with suitabke test case and documentation.
* Push your changes to a fork.
* Submit a pull request.
## License
Fluxtion is licensed under the [Server Side Public License](https://www.mongodb.com/licensing/server-side-public-license). This license is created by MongoDb, for further info see [FAQ](https://www.mongodb.com/licensing/server-side-public-license/faq) and comparison with [AGPL v3.0](https://www.mongodb.com/licensing/server-side-public-license/faq).

**This README is a work in progress and will be updating regularly**
