---
title: Combining imperative
parent: Cookbook functional
grand_parent: Old stuff
has_children: false
nav_order: 3
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook-functional/src/main/java/com/fluxtion/example/cookbook_functional/combineimperative
---

## Introduction

Combine imperative and functionally defined nodes in the same event processor. the event processor built will seamlessly
merge nodes regardless of how they are built.

## Example

[See the example code]({{page.example_src}}/Main.java) creates an EventProcessor with the following characteristics:

Functional node processes a stream of market updates filtered dynamically by subscription. Only MarketUpdate's that 
match the subscription id are propagated to child nodes.

An imperative node, [PriceStats]({{page.example_src}}/PriceStats.java) with a trigger method that is triggered when the parent updates. 
Holds a reference to the filtered node as a parent.

When the filter matches the trigger method of PriceStats is fired and PriceStats logs to console if the previous maximum price has been exceeded

1. Build the event processor
   - Define two events, MarketUpdate and the Subscription
   - Create a stateless BiPredicate function that tests MarketUpdate and Subscription for a match on Id
   - Use the [EventFlow]({{site.fluxtion_src_compiler}}/builder/stream/EventFlow.java) builder to create an event stream
   - Construct a filter on the MarketUpdate stream using the BiPredicate and the Subscription stream 
   - Get the [EventSupplier]({{site.fluxtion_src_runtime}}/stream/EventStream.java#L32) from the filtered stream by calling eventSupplier()
   - Create A [PriceStats]({{page.example_src}}/PriceStats.java), class that hold a reference to an EventSupplier<MarketUpdate> as a constructor argument
2. Running the example
   - Build and initialise the event processor
   - Fire a set of MarketUpdate events at the event processor
   - Update the subscription in realtime and fire the same set of events

**[Main Code]({{page.example_src}}/Main.java) for integrating functional and imperative nodes:**

{% highlight java %}
public class Main {

    public static void main(String[] args) {

        var eventProcessor = Fluxtion.compileAot(c -> {
            var marketUpdateEventSupplier = EventFlow.subscribe(MarketUpdate.class)
                    .filter(Main::isSubscribed, EventFlow.subscribe(Subscription.class))
                    .getEventSupplier();
            c.addNode(new PriceStats(marketUpdateEventSupplier));

        });
        eventProcessor.init();
        // ommited for clarity
    }
}
{% endhighlight %}

**Code for [PriceStats]({{page.example_src}}/PriceStats.java) class:**

{% highlight java %}
public class PriceStats {

    private final EventSupplier<MarketUpdate> marketUpdateEventStream;
    private double previousHigh;

    public PriceStats(EventSupplier<MarketUpdate> marketUpdateEventStream) {
        this.marketUpdateEventStream = marketUpdateEventStream;
    }

    @OnTrigger
    public boolean marketUpdated() {
        MarketUpdate marketUpdate = marketUpdateEventStream.get();
        boolean updated = marketUpdate.mid() > previousHigh;
        previousHigh = Math.max(marketUpdate.mid(), previousHigh);
        if (updated) {
            System.out.println("new high price:" + marketUpdate);
        }
        return updated;
    }

    @Initialise
    public void init() {
        previousHigh = 0;
    }
}
{% endhighlight %}


**Running the sample produces this output:**

{% highlight console %}
No filtering - ignore all MarketUpdate's

Set dynamically filter to id:10, should see EURUSD MarketUpdate's
new high price:MarketUpdate[id=10, name=EURUSD, mid=1.05]
new high price:MarketUpdate[id=10, name=EURUSD, mid=1.07]

Set dynamically filter to id:11, should see EURCHF MarketUpdate's
new high price:MarketUpdate[id=11, name=EURCHF, mid=1.118]
{% endhighlight %}






