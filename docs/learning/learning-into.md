---
title: Learning
has_children: false
nav_order: 4
published: true
---

# Streaming api 

Fluxtion offers a declarative coding api to express event processing logic that describes 
the real-time complex event processing needs of the application. The build
statements generate a class that extends [StaticEventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/api/src/main/java/com/fluxtion/api/StaticEventProcessor.java) 
encapsulating the required behaviour. A generated StaticEventProcessor is embedded in the application to 
process an event stream.

This guide is focused on the logical construction of processing. Integration 
of event streams is covered elsewhere (link to be provided when written).

| Term      | Description |
| ----------- | ----------- |
| Event    | An event is any valid java instance that is submitted to the event processor |
| Stream   | A stream is a set of events. An event instance can only appear once in a stream    |


## Select - subscribing to a stream
To subscribe to a stream of events declare a java type and issue a [select](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/builder/src/main/java/com/fluxtion/ext/streaming/builder/factory/EventSelect.java#L35) statement.
The select statement creates a [Wrapper](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/api/src/main/java/com/fluxtion/ext/streaming/api/Wrapper.java) 
that acts as a monad. With a select the wrapper will hold the latest event received by the processor that matches the java type.
See [SelectTest](https://github.com/v12technology/fluxtion/blob/develop/examples/learning-streaming/src/test/java/com/fluxtion/learning/streaming/SelectTest.java)
for code samples.

```java
Wrapper<MyDataType> dataStream = select(MyDataType.class);
```

### Logging events
Wrappers can be filtered, mapped, collected, grouped, windowed as desired by the user.
In addition a Wrapper provides a log() function, that logs the contents of the Wrapper 
when an update is received. Log actions are not mutative and can be added anywhere in the
graph

```java
Wrapper<MyDataType> dataStream = select(MyDataType.class)
    .log("received:");
```

### Logging individual values
The log method can accept method references to extract individual values for logging

```java
select(MyDataType.class)
    .log("received key:{} value:{}", MyDataType::getKey, MyDataType::getValue);
```

### Selecting multiple streams
There are no limits on the number of streams subscribed to, each wrapper will hold
the latest value of that stream. Streams can be processed separately or merged as desired, see later notes.

```java
Wrapper<MyDataType> myDataStream = select(MyDataType.class)
    .log("myDataStream received:");
Wrapper<Double> doubleStream = select(Double.class)
    .log("doubleStream received:");
```


### Executing a processor
Build statements are invoked by calling one of the in-process [build methods](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/generator/src/main/java/com/fluxtion/generator/compiler/InprocessSepCompiler.java#L154)
or [annotating a method](../starting/aot_compilation.md) and using the maven plugin to generate the event processor.
This example uses inprocess generation:

```java
public static void main(String[] args) throws Exception {
    StaticEventProcessor processor = reuseOrBuild(c -> {
        select(MyDataType.class)
            .log("received:");
    });
    processor.onTrigger(new MyDataType("hello", "world"));
}
```

## Filtering
A stream can be filtered. A filtered stream only propagates events that match the predicate. The wrapper
interface provides in place filtering methods. See [FilterTest](https://github.com/v12technology/fluxtion/blob/develop/examples/learning-streaming/src/test/java/com/fluxtion/learning/streaming/FilterTest.java)
for examples.

```java
select(String.class)
    .filter("warning"::equalsIgnoreCase)
    .log("warning received");
```

### Filtering with lambdas

```java
select(Double.class)
    .filter(d -> d > 10)
    .log("double {} gt 10");
```

### Filtering with method references
Method references can be used to apply more complex filtering rules, both static and instance methods are 
allowed.

```java
@Test
public void filterMethodRef(){
    sep(c -> {
        select(MyDataType.class)
            .filter(FilterTest::isValid)
            .log("warning received");
    });
}

public static boolean isValid(MyDataType myDataType){
    return myDataType.getKey().equals("hello") && myDataType.getValue().equals("world");
}
```

### Filter chains
Filters can be chained in a fluent style to produce more complex criteria

```java
select(Double.class)
    .filter(d -> d > 10)
    .log("input {} > 10")
    .filter(d -> d > 60)
    .log("input {} > 60");
```

### Else filter
A filter operation returns a [FilterWarpper](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/api/src/main/java/com/fluxtion/ext/streaming/api/FilterWrapper.java)
that gives access to an else branch for failed predicate processing via ```elseStream()``` 

```java
select(Double.class)
    .filter(d -> d > 10)
    .filter(d -> d > 60)
    .log("input {} > 60")
    .elseStream().log("input {} between 10 -> 60 ");
```

### Dynamic filtering
Filters are nodes on the graph and can process events. This allows the predicate to be updated in 
real-time. Fluxtion provides pre-built filters that can be dynamically controlled. In the example
below a greater than test has an initial value of 10 and is updated with FilterConfig event. Fluxtion
pre-built predicates are discussed later.

```java
sep(c -> {
select(Double.class)
    .filter(gt(10, "configKey"))
    .log("dynamic filter exceeded");
});
```

### Dynamic filtering with user function

User functions can provide dynamic filtering. The example below integrate an instance function as a filter and applies 
it to the event stream. A FilterGT instance is updated with a [Signal](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/api/src/main/java/com/fluxtion/api/event/Signal.java) 
event, that is filtered using key "myConfigKey".
The `@EventHandler` annotation routes events to the FilterGT instance in tha event processor. 

```java
@Test
public void dynamicUserFiltering(){
    sep(c -> {
        select(Double.class)
            .filter(new FilterGT(10)::gt)
            .log("dynamic filter exceeded val:{}", Double::intValue);
    });
    onTrigger(20.0);
    onTrigger(50.0);
    onTrigger(new Signal("myConfigKey", 25));
    onTrigger(20.0);
    onTrigger(50.0);
}
    
@Data
@AllArgsConstructor
@NoArgsConstructor
public static class FilterGT{
    private int minValue;
    
    public boolean gt(Number n){
        return n.longValue() > minValue;
    }
    
    @EventHandler(filterString = "myConfigKey", propagate = false)
    public void updateFilter(Signal<Number> filterSignal){
        minValue = filterSignal.getValue().intValue();
    } 
}
```
Output for the test:

{% highlight console %}
dynamic filter exceeded val:20
dynamic filter exceeded val:50
dynamic filter exceeded val:50
{% endhighlight %}

## Filter builder  
Filters can be constructed from higher order functions in the [FilterBuilder](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/builder/src/main/java/com/fluxtion/ext/streaming/builder/factory/FilterBuilder.java)
class. Implicitly Subscription and wrappers are created removing the need to explicitly declare a subscription. The 
previous filtering examples can be constructed more simply:

```java
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
...
FilterWrapper<String>     filter  = filter("warning"::equalsIgnoreCase);
FilterWrapper<Double>     filter1 = filter(Double.class, d -> d > 10);
FilterWrapper<MyDataType> filter2 = filter(MyDataType.class, FilterTest::isValid);
FilterWrapper<Double>     filter3 = filter(Double.class, gt(10, "configKey"));
FilterWrapper<Double>     filter4 = filter(Double.class, new FilterGT(10)::gt);
```
### Filtering with tests
Filters can apply predicates that reference multiple streams and not solely the current event. A test is any function
that returns a boolean. Inputs to the test function are streams, only when the test is valid will the event being filtered
propagate. In the example below only when the time is between min and max will MyDataType events be filtered. Tests are
built with utility functions provided by [TestBuilder](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/builder/src/main/java/com/fluxtion/ext/streaming/builder/factory/TestBuilder.java)

```java
@Test
public void filterBuilderTest() {
    sep((c) -> {
        filter(MyDataType.class,
            test(FilterTest::withinRange, TimeEvent::getTime, MinAge::getMin, MaxAge::getMax));
    });
}

public static boolean withinRange(int test, int min, int max) {
    return min < test && test < max;
}

@Data
public static class TimeEvent{
    int time;
}

@Data
public static class MinAge{
    int min;
}

@Data
public static class MaxAge{
    int max;
}
```

## Filtering from external notification
Events can be propagated when a notification is received from an independent stream. The [FilterByNotificationBuilder](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/builder/src/main/java/com/fluxtion/ext/streaming/builder/factory/FilterByNotificationBuilder.java)
provides several utility methods to support this. An example below demonstrates event propagation of the 
Doble occurs when a string event is processed that equals "tick". See [FilterByNotificationTest](https://github.com/v12technology/fluxtion/blob/develop/examples/learning-streaming/src/test/java/com/fluxtion/learning/streaming/FilterByNotificationTest.java)

```java
@Test
public void filterEvent() {
    sep(c -> {
        filterOnNotify(select(Double.class), filter("tick"::equalsIgnoreCase))
            .log("update:");
    });
    onTrigger(1.0);
    onTrigger(2.0);
    onTrigger(3.0);
    onTrigger("tick");
    onTrigger(4.0);
}
```

Output for the test:

{% highlight console %}
Running com.fluxtion.learning.streaming.FilterByNotificationTest
update: 3.0
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.528 sec
{% endhighlight %}

FilterByNotificationBuilder supports the following patterns:
- Propagate filter subject when both update in the same cycle
- Propagate filter subject when either update in the same cycle


### Placeholder for:
- streaming api (declarative coding)
  - select
  - filter
  - dynamic filters  
  - map
    - stateful functions
    - stateless  
    - reset  
  - test and conditional logic    
  - wrapper
    - foreach
    - reset and publish
    - valid on start
    - default value
    - id  
  - notification override
  - push to user nodes
  - stateful  
  - window functions  
    - sliding
    - tumbling
  - Library functions
  - window functions library  
- user code integration (imperative coding)
- Monitoring
- Auditing
- Testing
- Use cases and examples
