---
title: First Fluxtion application
has_children: true
nav_order: 2
published: true
---
# First Fluxtion application
This example processes a stream of trade events calculating statisitics in a 5 second sliding window. 
Every second the three most active currency pairs by volume are logged to the screen. 
An understanding of Java and maven is required to complete this tutorial. 
The code for the example is located [here](https://github.com/v12technology/fluxtion/tree/2.10.11/examples/quickstart/lesson-1).

## Development process
Building a Fluxtion application requires three steps
1. Create a maven project with the required dependencies. 
1. Write processing logic using Fluxtion api's. 
1. Integrate fluxtion generated processor into an application.

### 1. Maven build

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.fluxtion.example</groupId>
    <artifactId>quickstart.lesson-1</artifactId>
    <version>{{site.fluxtion_version}}</version>
    <packaging>jar</packaging>
    <name>fluxtion :: quickstart :: lesson-1</name>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.deploy.skip>true</maven.deploy.skip>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.fluxtion.extension</groupId>
            <artifactId>fluxtion-streaming-builder</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
        </dependency>
    </dependencies>
</project>
```

Lombok reduces code noise in the example but is not required.

### 2. Fluxtion stream processing logic
Procesing logic is expressed using Fluxtin streaming api. The building logic is passed to the `reuseOrBuild` method 
which generates an event processor instance if one cannot be found in the local cache.

```java
public static void main(String[] args) throws Exception {
  StaticEventProcessor processor = reuseOrBuild(c -> {
    groupBySum(Trade::getSymbol, Trade::getAmount)
      .sliding(seconds(1), 5)
      .comparator(numberValComparator()).reverse()
      .top(3)
      .map(TradeMonitor::formatTradeList)
      .log();
  });
  TradeGenerator.publishTestData(processor);
}

public static String formatTradeList(List<Tuple<String, Number>> trades) {
  StringBuilder sb = new StringBuilder("Most active ccy pairs in past 5 seconds:");
  for (int i = 0; i < trades.size(); i++) {
    Tuple<String, Number> result = trades.get(i);
    sb.append(String.format("\n\t%2d. %5s - %.0f trades", i + 1, result.getKey(), result.getValue()));
  }
  return sb.toString();
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public static class Trade {
  private String symbol;
  private double amount;
}
```

- Line 2 Creates an event processor if one cannot be found on the class path for this builder.
- Line 3 Creates an aggregate sum of the trade amount, grouped by symbol name.
- Line 4 Defines a sliding window, publishing every second with a total window size of 5 seconds.
- Line 5 Applies a comparator function to the cumulative sum and then reverses the sort order.
- Line 6 Filters the list of trades to the top 3 by volume.
- Line 7 Applies a mapping function to pretty print the filtered list of trades.
- Line 8 Logs the pretty print function on every bucket expiry after first window, 5 seconds.

### 3. Application integration

An application feeds events into a Fluxtion generated event processor to execute business logic. 
All Fluxtion event processors implement the [StaticEventProcessor](https://github.com/v12technology/fluxtion/blob/{{site.fluxtion_version}}/api/src/main/java/com/fluxtion/api/StaticEventProcessor.java) interface. 
To post an event the application invokes `processor.onEvent(event)` on the processor instance.

```java
public class TradeGenerator {

  private static final String[] ccyPairs = new String[]{"EURUSD", "EURCHF", "EURGBP", "GBPUSD",
                             "USDCHF", "EURJPY", "USDJPY", "USDMXN", "GBPCHF", "EURNOK", "EURSEK"};

  static void publishTestData(StaticEventProcessor processor) throws InterruptedException {
    Random random = new Random();
    int numberPairs = ccyPairs.length;
    while (true) {
      processor.onEvent(new Trade(ccyPairs[random.nextInt(numberPairs)], random.nextInt(100) + 10));
      Thread.sleep(random.nextInt(10) + 10);
    }
  }
}
```

The `publishTestData` method generates random currency pair trade events and posts them to the supplied event processor.

## Running the application

Running the application will generate the event processor and then publish Trade events to the processor instance. 
After about 5 seconds output to the console will be similar to that below. After the first 5 seconds an update will be printed every second.

{% highlight console %}
mvn exec:java -Dexec.mainClass="com.fluxtion.example.quickstart.lesson1.TradeMonitor"
Most active ccy pairs in past 5 seconds:
	 1. EURGBP - 2390 trades
	 2. USDMXN - 2164 trades
	 3. USDJPY - 1921 trades
Most active ccy pairs in past 5 seconds:
	 1. USDMXN - 2447 trades
	 2. EURUSD - 1987 trades
	 3. EURGBP - 1913 trades
Most active ccy pairs in past 5 seconds:
	 1. USDMXN - 2262 trades
	 2. EURGBP - 2018 trades
	 3. EURUSD - 2002 trades
{% endhighlight %}
