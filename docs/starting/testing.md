---
title: Unit testing
parent: Getting started
has_children: false
nav_order: 1
published: true
---

# Introduction

Unit testing of any system is critical. The previous example creates an event processor at runtime, but applies no tests. This example demonstrates how to write unit tests that validate event processing logic. Fluxtion provides Junit utilities for generating and testing event processors. The example is located [here](https://github.com/v12technology/fluxtion/tree/master/examples/quickstart/lesson-2).

## Testing process
Fluxtion integrates unit testing into the developer workflow as follows:
1. Add maven dependencies for testing and create a test class that extends the [BaseSeInprocessTest.java](https://github.com/v12technology/fluxtion/blob/2.10.9/generator/src/test/java/com/fluxtion/generator/util/BaseSepInprocessTest.java).
1. Extract processor construction into a separate builder method for use in a unit test.   
1. Write a test case that uses the builder method to create an event processor for testing. Send events into the generated processor and validate outputs or state of nodes using asserts/expectations. 

### 1. Maven dependencies
Add the fluxtion test jar to the project and Junit 4 dependency.

```xml
<dependencies>
    <dependency>
        <groupId>com.fluxtion</groupId>
        <artifactId>generator</artifactId>
        <type>test-jar</type>
        <scope>test</scope>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.hamcrest</groupId>
        <artifactId>hamcrest-all</artifactId>
        <version>1.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2. Introduce builder method
The app is refactored to separate processor consruction logic into a builder method. The builder method can be tested directly in a unit test.
To help testing a node can be given a unique identifier by appending  `.id("name")` during construction. The BaseSepInprocessTest provides helper methods to access a processor node by id, with `getField("name")`.

```java
public class TradeMonitor {

  public static void main(String[] args) throws Exception {
    publishTestData(reuseOrBuild(TradeMonitor::build));
  }

  public static void build(SEPConfig cfg) {
    groupBySum(Trade::getSymbol, Trade::getAmount)
      .sliding(seconds(1), 5)
      .comparator(numberValComparator()).reverse()
      .top(3).id("top3")
      .map(TradeMonitor::formatTradeList)
      .log();
  }

}
```

### 3. Write Junit test case
A complete unit test is shown below that validates the behaviour of the event processor. The processor is constructed by supplying the builder method to the test super class with `sep(TradeMonitor::build)`

Events are sent to the processor under test with the onEvent method e.g.`onEvent(new Trade("EURUSD", 5_000))`. A data driven clock can be adjusted in the test using the `tick("new time")` method to simulate the passing of time.

A reference to the "top3" node is gained using the id set in the bulder method, with: `WrappedList<Tuple<String, Number>> top3 = getField("top3")`. 

Normal Junit asserts validate the expected behavior of the processor by assering the state of a node.

```java
public class TradeMonitorTest extends BaseSepInprocessTest {

    @Rule
    public SystemOutResource sysOut = new SystemOutResource();

    String window1_log = "Most active ccy pairs in past 5 seconds:\n"
        + "	 1. EURUSD - 5150 trades\n"
        + "	 2. USDCHF - 500 trades\n"
        + "	 3. EURJPY - 100 trades";
    String window2_sysout = "Most active ccy pairs in past 5 seconds:\n"
        + "	 1. USDCHF - 500 trades\n"
        + "	 2. EURUSD - 150 trades\n"
        + "	 3. EURJPY - 100 trades";
    
    @Test
    public void testTradeMonitor() {
        sep(TradeMonitor::build);
        sysOut.clear();
        tick(1);
        onEvent(new Trade("EURUSD", 5_000));
        tick(1200);
        onEvent(new Trade("EURUSD", 150));
        onEvent(new Trade("EURJPY", 100));
        tick(2100);
        onEvent(new Trade("USDCHF", 500));
        tick(4000);
        onEvent(new Trade("GBPUSD", 25));
        WrappedList<Tuple<String, Number>> top3 = getField("top3");
        assertThat(top3.size(), is(0));

        //advance to 5 seconds
        tick(5500);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(top3.get(0).getKey(), is("EURUSD"));
        assertThat(top3.get(0).getValue(), is(5_150d));
        assertThat(top3.get(1).getKey(), is("USDCHF"));
        assertThat(top3.get(1).getValue(), is(500d));
        assertThat(top3.get(2).getKey(), is("EURJPY"));
        assertThat(top3.get(2).getValue(), is(100d));
        assertThat(sysOut.asString().trim(), is(window1_log));

        //advance time but within a bucket, nothing will happen unless a bucket expires
        sysOut.clear();
        tick(5999);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(sysOut.asString().trim(), is(""));

        //advance time expires bucket, removes first EURUSD trade triggering a publish with new top 3
        sysOut.clear();
        tick(6000);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(top3.get(0).getKey(), is("USDCHF"));
        assertThat(top3.get(0).getValue(), is(500d));
        assertThat(top3.get(1).getKey(), is("EURUSD"));
        assertThat(top3.get(1).getValue(), is(150d));
        assertThat(top3.get(2).getKey(), is("EURJPY"));
        assertThat(top3.get(2).getValue(), is(100d));
        assertThat(sysOut.asString().trim(), is(window2_sysout));
    }
}
```
