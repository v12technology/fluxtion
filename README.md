<p align="center">
  <img width="270" height="200" src="images/Fluxtion_logo.png">
</p>

[![Build Status](https://travis-ci.org/v12technology/fluxtion.svg?branch=master)](https://travis-ci.org/v12technology/fluxtion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api)
# Lightweight event stream processor
 - Pure java in memory fast data processing 
 - Ahead of time compiler means fast startup and small footprint
 - Batch or streaming
## See the code
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
# Introduction

Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further.


Fluxtion is a fully featured java based event stream processor that brings real-time data processing inside your application. If you need to build applications that react to complex events and make fast decisions then Fluxtion is for you. We build stream processing logic free from any messaging layer, there is no lock-in with Fluxtion.

Fluxtion is is easy to use and ultra fast, our sweet spot is either edge processing or single server applications. Whether you need to process tens of millions of events per second or write complex rule driven applications that make decisions in microseconds we can help. Retro fitting real-time calculations into an existing application without requiring wholesale changes to the infrastructure is a great fit. When you need to make decisions and not just calculate then you are in the right place.

Uniquely among stream processors Fluxtion employs ahead of time compilation to create a stream processing engine. Describe your processing and Fluxtion tailors a solution to your needs at build time. Ahead of time compilation offers several critical advantages over existing products, 
 - No vendor lock-in, the engine can be used within any java application
 - Compiler optimized code gives higher performance, lower running costs and quicker response times
 - Faster startup times for your application
 - Integrates client logic as a first class citizen 
 - Source code is generated that makes debugging and maintenance easy
 - Meta-data such as images and graphml are created to visualise the process graph
## Example
We have a five minute tutorial to dive into [here](https://github.com/v12technology/fluxtion-quickstart/tree/master). The excerpt below from the tutorial shows how a processing graph can be constructed in a few lines. 
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
A client instance, TempertureController that controls an external system, is integrated into the generated processor. When a filter is met, a method on the client instance is directly invoked. Notice that method references are used throughout, no positional parameters or generic tuples are required. The strongly typing makes the code easier to read, maintain and refactor.

A sample of the generated code and images for this example is [here](https://github.com/v12technology/fluxtion-quickstart/tree/master/src/main/resources/com/fluxtion/quickstart/roomsensor/generated).
## Philosophy
Our philosophy is to make delivering streaming applications in java simple by employing a clean modern api similar to the familiar Java streams api. The Fluxtion compiler carries the burden of generating simple efficient code that is optimised for your specific application. We pay the cost at compile time only once, so every execution of your stream processor sees benefits in reduced startup time and smaller running costs.

Why concentrate solely on the processing logic? There are many great messaging systems out there offering scale out to hundreds of millions of events per second. But many reactive applications do not need that scale, the problem is integrating the event streams from different messaging systems into a single decision making engine. In cases like these you want to concentrate on writing the logic. 
## Describing a processor
Deciding to apply stream processing to a problem leads you to your next decision, how to describe your processing requirements. Fluxtion constructs an intermediate representation for the ahead of time compiler to process. The intermediate representation can be built in a number of ways, each with their own advantages.
### Declarative or DSL
This is the most commonly seen in stream processors, a sql or stream like language is used to describe the requirements. Commonly found in numeric focused problems it is a good fit for real-time analytics.  The example [here](https://github.com/v12technology/fluxtion-quickstart/tree/master) is a declarative example using Fluxtion. When a problem is more rule based, such as constructing a tax calculation, then this is not a good approach.
### Imperative
Certain problems are more graph based and require conditional control of event propagation in the graph by user instances. Rule engines fall into this domain where there is a rich universe of events, multiple execution paths and many possible outputs. An imperative construction of the graph is a better fit. The engine provides the routing and connection logic between nodes.  **Example to be added**
### Data driven
Once a problem domain is well known and the degrees of freedom are constrained it is possible to construct a solution from a solely data definition. A scoring model from a machine learning analysis can be communicated purely as data for the execution engine to construct. **Example to be added**
### Injected reuse
Like any good system it is good to re-use existing well tested behaviour. This is the same for event processing systems. It is possible to "inject" a whole event process chain as a group. For example in trading application we can inject a volatility calculation, that comprises many node and and different event types. Injecting saves describing repeatable behaviours making systems quicker to build
**Example to be added**
## Integrating with client code
Traditional stream processors have an ingest, transform and publish cycle. When moving from analytics to actually taking actions there is a barrier to integrating the output with the client application. With Fluxtion client code is integrated into the generated processor and invoked directly. 
## Pipeline vs graph processing
Fluxtion is built as a graph processor and not a pipeline. A pipeline has a single entry point and single execution path, a graph processor has multiple entry points multiple execution paths. Handling heterogeneous event types in a unique fashion is the default behaviour. In fact the more complex the problem the greater the advantage that Fluxtion displays. 
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
