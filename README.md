<p align="center">
  <img width="270" height="200" src="images/Fluxtion_logo.png">
</p>

[![Build Status](https://travis-ci.org/v12technology/fluxtion.svg?branch=master)](https://travis-ci.org/v12technology/fluxtion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fluxtion/fluxtion-api)

# Introduction
Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further.

Fluxtion is a fully featured stream processor that is easy to use, ultra fast, embeds and is built to make both maintenance and development quick and easy. Uniquely among stream processors Fluxtion employs ahead of time compilation to create the processing engine. Ahead of time compilation offers several critical advantages over existing products, the standout feature is no product lock-in. We separate the processing logic from the infrastructure allowing clients to bind stream processing logic to any event delivery system.

The sweet spot for Fluxtion is either edge processing or single server applications that process upto a few hundred million events per second. Retro fitting real-time calculations into an existing application without requiring wholesale changes to the infrastructure is a great fit.  Integrating stream processing into an environment with multiple messaging systems is easily accomplished.

A Fluxtion generated event processor is designed be embedded in a client application. After generating the stream processor the Fluxtion ahead of time compiler is not required in the running application.
## Philosophy
Our philosophy is to make delivering streaming applications in java simple. Employing a clean modern api similar to the familiar Java streams api. The Fluxtion compiler carries the burden of generating clean efficient code that is optimised for memory and cpu to reduce running costs.

Traditional stream processors have an ingest, transform and publish cycle. When moving from analytics to actually taking actions there is a barrier to integrating the output with the client application. With Fluxtion client code is integrated into the generated processor and invoked directly. 

Fluxtion is built as a graph processor and not a pipeline, handling heterogeneous event types in a unique fashion is the default behaviour. In fact the more complex the problem the greater the advantage that Fluxtion displays.
## Example
We have a five minute tutorial to dive into [here](https://github.com/v12technology/fluxtion-quickstart/tree/1.0.0). The excerpt below from the tutorial shows how a processing graph can be constructed in a few lines. A client instance, TempertureController that controls an external system, is integrated into the generated processor. When a filter is met, a method on the client instance is directly invoked.
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
Notice that method references are used throughout, no positional parameters or generic tuples are required. The strongly typing makes the code easier to read, maintain and refactor.
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
