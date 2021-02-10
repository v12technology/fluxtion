---
title: Learning
has_children: false
nav_order: 4
published: true
---

# Introduction

## Streaming api

Fluxtion offers a declarative coding style to create event processing logic. The build
statements create a class that extends StaticEventProcessor, which can be used in the
application.

### Select
In order to subscribe to a set of events, declare a java type and issue a [select](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/builder/src/main/java/com/fluxtion/ext/streaming/builder/factory/EventSelect.java#L35) statement.
The select statement creates a [Wrapper](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/extensions/streaming/api/src/main/java/com/fluxtion/ext/streaming/api/Wrapper.java) 
that acts as a monad. With a select the wrapper will hold the latest event that is received by the processor.

```java
select(MyDataType.class);
```

### Log wrapper values
Wrappers can be filtered, mapped, collected, grouped, windowed as desired by the user.
In addition a Wrapper provides a log() function, that logs the contents of the Wrapper 
when an update is received. Log actions are not mutative and can be added anywhere in the
graph

```java
select(MyDataType.class)
    .log("received:");
```

### Log individual values
The log method can accept method references to extract individual values for logging

```java
select(MyDataType.class)
    .log("received key:", MyDataType::getKey);
```

### Executing a processor
Build statements are invoked by calling one of the in-process [build methods](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/generator/src/main/java/com/fluxtion/generator/compiler/InprocessSepCompiler.java#L154)
or annotating a method and using the maven plugin to generate the event processor.
This example uses inprocess generation:

```java
public class TradeMonitor {

    public static void main(String[] args) throws Exception {
        StaticEventProcessor processor = reuseOrBuild(c -> {
            select(MyDataType.class)
                .log("received:");
        });
        processor.onEvent(new MyDataType("hello", "world");
    }
}
```



### Placeholder for:
- streaming api (declarative coding)
- user code integration (imperative coding)
- Monitoring
- Auditing
- Testing
- Use cases and examples
