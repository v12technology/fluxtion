---
title: Dynamic filtering
parent: Cookbook functional
has_children: false
nav_order: 3
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook-functional/src/main/java/com/fluxtion/example/cookbook_functional/dynamicfilter
---

## Introduction

Dynamically filter an event stream with a value that updates at runtime. 

Apply a BiPredicate predicate to two streams:
- The stream of events to be filtered 
- An independent event stream of filtering values

## Example

[See the example code]({{page.example_src}}/Main.java), a stream of market updates is filtered by a subscription. Only 
MarketUpdate's that match the subscription id are logged to console. The subscription is dynamically updated

1. Build the event processor
   - Define two events, MarketUpdate and the Subscription
   - Create a stateless BiPredicate function that tests MarketUpdate and Subscription for a match on Id
   - Use the [EventFlow]({{site.fluxtion_src_compiler}}/builder/stream/EventFlow.java) builder to create an event stream
   - Construct a filter on the MarketUpdate stream using the BiPredicate and the Subscription stream 
5. Running the example
   - Build and initialise the event processor
   - Fire a set of MarketUpdate events at the event processor
   - Update the subscription in realtime and fire the same set of events

**Code for dynamic filtering:**

{% highlight java %}
public class Main {

    public static void main(String[] args) {

        var eventProcessor = Fluxtion.interpret(c -> {
            EventFlow.subscribe(MarketUpdate.class)
                    .filter(Main::isSubscribed,  EventFlow.subscribe(Subscription.class))
                    .console("Filtered :{}");
        });
        eventProcessor.init();
        System.out.println("No filtering - ignore all MarketUpdate's");
        sendMarketEvents(eventProcessor);
        //now set the filter for EURUSD and send the same events
        System.out.println("\nSet dynamically filter to id:10, should see EURUSD MarketUpdate's");
        eventProcessor.onEvent(new Subscription(10));
        sendMarketEvents(eventProcessor);
        //now set the filter EURCH and send the same events
        System.out.println("\nSet dynamically filter to id:11, should see EURCHF MarketUpdate's");
        eventProcessor.onEvent(new Subscription(11));
        sendMarketEvents(eventProcessor);
    }

    private static void sendMarketEvents(EventProcessor processor){
        processor.onEvent(new MarketUpdate(10, "EURUSD", 1.05));
        processor.onEvent(new MarketUpdate(11, "EURCHF", 1.118));
        processor.onEvent(new MarketUpdate(10, "EURUSD", 1.07));
        processor.onEvent(new MarketUpdate(11, "EURCHF", 1.11));
        processor.onEvent(new MarketUpdate(11, "EURCHF", 1.10));
        processor.onEvent(new MarketUpdate(15, "USDGBP", 1.12));
        processor.onEvent(new MarketUpdate(15, "USDGBP", 1.14));
        processor.onEvent(new MarketUpdate(11, "EURCHF", 1.06));
        processor.onEvent(new MarketUpdate(15, "USDGBP", 1.15));
    }

    record MarketUpdate(long id, String name, double mid){}

    record Subscription(long id){}

    public static boolean isSubscribed(MarketUpdate id1, Subscription id2){
        return id1.id() == id2.id();
    }
}
{% endhighlight %}


**Running the sample produces this output:**

{% highlight console %}
No filtering - ignore all MarketUpdate's

Set dynamically filter to id:10, should see EURUSD MarketUpdate's
Filtered :MarketUpdate[id=10, name=EURUSD, mid=1.05]
Filtered :MarketUpdate[id=10, name=EURUSD, mid=1.07]

Set dynamically filter to id:11, should see EURCHF MarketUpdate's
Filtered :MarketUpdate[id=11, name=EURCHF, mid=1.118]
Filtered :MarketUpdate[id=11, name=EURCHF, mid=1.11]
Filtered :MarketUpdate[id=11, name=EURCHF, mid=1.1]
Filtered :MarketUpdate[id=11, name=EURCHF, mid=1.06]
{% endhighlight %}






