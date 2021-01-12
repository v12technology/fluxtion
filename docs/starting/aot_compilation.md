---
title: Buildtime generation
parent: Getting started
has_children: false
nav_order: 2
published: true
---

# Introduction
Fluxtion provides a maven plugin that can generate an event processor as part of the build cycle. 
This makes a system more predictable at runtime as the behaviour is statically generated before deployment and can be fully tested.
The example is located [here](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/examples/quickstart/lesson-3).

## Development process
To statically generate the event processor at buildtime three steps are required
1. Add the Fluxtion maven plugin to the build.
1. Annotate any Fluxtion builder methods with `@SepBuilder` providing the fully qualified name of the generated processor as a parameter.
1. Remove any calls to dynamically build a processor at runtime and use the fqn above to instantiate a statically generated processor, including test cases.

### 1. Maven Build
Add the fluxtion maven plugin using the scan goal. It is usally better to add the generation as a maven profile 
as once the solution is generated then the build should be stable. Skipping tests for the generation phase is usually preferable.

```xml
    <profiles>
        <profile>
            <id>fluxtion-generate</id>
            <properties>
                <skipTests>true</skipTests>
            </properties>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.fluxtion</groupId>
                        <artifactId>fluxtion-maven-plugin</artifactId>
                        <version>{{site.fluxtion_version}}</version>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>scan</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

### 2. Annotate builder methods
The scan goal searches the project code base for any builder methods that have the `@SepBuilder` annotation. 
The parameters for name and package name are combined to make the fully quaified name of the generated event processor.

```java
    @SepBuilder(name = "TradeEventProcessor", packageName = "com.fluxtion.example.quickstart.lesson3.generated")
    public static void build(SEPConfig cfg) {
        groupBySum(TradeMonitor.Trade::getSymbol, TradeMonitor.Trade::getAmount)
            .sliding(seconds(1), 5)
            .comparator(numberValComparator()).reverse()
            .top(3).id("top3")
            .map(TradeMonitor::formatTradeList)
            .log();
    }
```

Running maven with the fluxtion plugin scan goal will generate the event processor at buildtime, with this command:

{% highlight console %}
mvn install -Pfluxtion-generate
{% endhighlight %}

After the build completes generated artefacts are located in the package directory provided in the annotation. 
See [here](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/examples/quickstart/lesson-3/src/main/java/com/fluxtion/example/quickstart/lesson3/generated) 
for an example of the output. Graphical representation of the processing graph is also generated
by the plugin [here](https://github.com/v12technology/fluxtion/blob/{{site.fluxtion_version}}/examples/quickstart/lesson-3/src/main/resources/com/fluxtion/example/quickstart/lesson3/generated/TradeEventProcessor.png)

### 3. Use buildtime generated processor
Update the main file to use the ahead of time generated processor, TradeEventProcessor.

```java
public class TradeMonitor {

    public static void main(String[] args) throws Exception {
        publishTestData(new TradeEventProcessor());
    }

//omitted 
}
```

The generated processor implements the `Lifecycle` interface and `init()` must be
called before the processor can accept events.

```java
public class TradeGenerator {

    private static final String[] ccyPairs = new String[]{"EURUSD", "EURCHF", "EURGBP", "GBPUSD",
        "USDCHF", "EURJPY", "USDJPY", "USDMXN", "GBPCHF", "EURNOK", "EURSEK"};

    static void publishTestData(TradeEventProcessor processor) throws InterruptedException {
        processor.init();
        Random random = new Random();
        while (true) {
            processor.handleEvent(new TradeMonitor.Trade(ccyPairs[random.nextInt(ccyPairs.length)], random.nextInt(100) + 10));
            Thread.sleep(random.nextInt(10) + 10);
        }
    }
}
```

Update the test case to use the statically generated event processor, by passing in the class name.

```java
    @Test
    public void testTradeMonitor() {
        sep(TradeEventProcessor.class);
        //omitted
    }
```

## Running the application

run the application as before:

{% highlight console %}
mvn exec:java -Dexec.mainClass="com.fluxtion.example.quickstart.lesson3.TradeMonitor"
 Most active ccy pairs in past 5 seconds:
         1. EURNOK - 2477 trades
         2. GBPCHF - 2194 trades
         3. EURCHF - 2191 trades
 Most active ccy pairs in past 5 seconds:
         1. EURNOK - 2985 trades
         2. EURCHF - 2173 trades
         3. USDMXN - 1992 trades
{% endhighlight %}