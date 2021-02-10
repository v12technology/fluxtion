---
title: Learning
has_children: false
nav_order: 4
published: true
---

# Introduction

##Streaming api

Fluxtion offers a declarative coding style to create event processing logic. The build
statements create a class that extends StaticEventProcessor, which can be used in the
application.

In order to subscribe to a set of events, declare a java type and issue a select statement.
The select statement creates a Wrapper<T> that acts as a monad. 


```java

select(MyDataType.class);

```

Wrappers can be filtered, mapped, collected, grouped, windowed as desired by the user.
In addition a Wrapper provides a log() function, that logs the contents of the Wrapper 
when an update is received.

```java

select(MyDataType.class)
    .log("received:");

```

Build statements are invoked by calling one of the build methods in process or
annotating a method and using the maven plugin to generate the event processor.
This example is using inprocess generation:

```java
public class TradeMonitor {

    public static void main(String[] args) throws Exception {
        StaticEventProcessor processor = reuseOrBuild(c -> {
            groupBySum(Trade::getSymbol, Trade::getAmount)
                select(MyDataType.class)
                    .log("received:");
        });
        processor.onEvent(new MyDataType("hello", "world");
    }
}
```

Placeholder for:
- streaming api (declarative coding)
- user code integration (imperative coding)
- Monitoring
- Auditing
- Testing
- Use cases and examples
