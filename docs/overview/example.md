---
title: Example processor
parent: Overview
has_children: false
nav_order: 1
published: true
---

# Example

Simple Fluxtion hello world stream example. Add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the value member variable from each event stream.


## Maven pom file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fluxtion.example</groupId>
    <modelVersion>4.0.0</modelVersion>
    <version>1.0.0-SNAPSHOT</version>
    <artifactId>addnumbers</artifactId>
    <name>example :: addnumbers</name>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
    </dependencies>
</project>
```

## Java code

```java
import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.runtime.EventProcessor;

import static com.fluxtion.compiler.builder.stream.EventFlow.subscribe;

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

- **line 19-28** User code describing the processing graph
- **line 18** Supplies the graph description to Fluxtion to generate an EventProcessor
- **line 312** Inits the generated EventProcessor
- **line 16-22** Posts a stream of events to the EventProcessor instance

## Example execution output
```text
rcvd -> Data1[value=20.5]
rcvd -> Data2[value=63.0]
rcvd -> Data1[value=56.8]
OUT: sum 119.8 > 100
```
