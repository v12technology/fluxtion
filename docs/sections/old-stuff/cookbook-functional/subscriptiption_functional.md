---
title: EventFeed integration
parent: Cookbook functional
grand_parent: Old stuff
has_children: false
nav_order: 4
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/cook_subscription_example/cookbook/src/main/java/com/fluxtion/example/cookbook/subscription
---

## Introduction

This example demonstrates subscribing to data events with a functional approach using the Fluxtion streaming api. The
advantage of the streaming api is the reduction of user code written and the increased code re-use. 

***Please read the [EventFeed integration](../cookbook/subscription_imperative.html) cookbook article firste***

The goal of this example is to subscribe to [SharePriceEvent's]({{page.example_src}}/SharePriceEvent.java) without 
having to write [SharePriceNode's]({{page.example_src}}/imperative/SharePriceNode.java)

## Example
See the [Main method]({{page.example_src}}/functional/SubscriberFunctional.java) for the code example. 

There are no user written nodes to subscribe to SharePriceEvents, they are replaced with the calls like this:

{% highlight java %}
EventFlow.subscribe(SharePriceEvent.class, "MSFT").console("SharePriceNode:MSFT -> {}");
{% endhighlight %}

This call has the following side effects:
- A node is added to the graph that listens to events of type, SharePriceEvent
- The filter "MSFT" is applied to the eventhandler method of the generated node
- A [SubscriptionManager]({{site.fluxtion_src_runtime}}/input/SubscriptionManager.java)  instance is injected into the node, 
- The node creates An [EventSubscription]({{site.fluxtion_src_runtime}}/node/EventSubscription.java) with classtype: SharePriceEvent and filterString: MSFT 
- Subscribe is called on the injected SubscriptionManager with the EventSubscription created above
- Adds a teardown method that calls SubscriptionManager.unsubscribe() 
- The SubscriptionManager forwards the EventSubscription to any registered EventHandler

Full code:

{% highlight java %}
public class SubscriberFunctional {

    public static void main(String[] args) {
        var marketPriceProcessor = Fluxtion.interpret(c -> {
            EventFlow.subscribe(SharePriceEvent.class, "MSFT").console("SharePriceNode:MSFT -> {}");
            EventFlow.subscribe(SharePriceEvent.class, "AMZN").console("SharePriceNode:AMZN -> {}");
        });
        marketPriceProcessor.init();
    
        MarketDataFeed eventFeed = new MarketDataFeed();
        marketPriceProcessor.addEventFeed(eventFeed);
    
        System.out.println("\npublishing prices from MarketDataFeed:");
        eventFeed.publish("MSFT", 21.36);
        eventFeed.publish("MSFT", 22.11);
        eventFeed.publish("IBM", 25);
        eventFeed.publish("AMZN", 72.6);
        eventFeed.publish("GOOGL", 179);
    
        System.out.println("\ntear down marketPriceProcessor:");
        marketPriceProcessor.tearDown();
        eventFeed.publish("MSFT", 23.64);
    
        System.out.println("\nrestart marketPriceProcessor:");
        marketPriceProcessor.init();
        System.out.println("\npublishing prices from MarketDataFeed:");
        eventFeed.publish("MSFT", 22.51);
    }

}

{% endhighlight %}

### Execution output

Running the example main method produce this output:

{% highlight console %}
subscriber registered
MarketDataFeed adding new subscriber, count:1
MarketDataFeed subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=MSFT}
MarketDataFeed subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=AMZN}

publishing prices from MarketDataFeed:
SharePriceNode:MSFT -> SharePriceEvent[symbolId=MSFT, price=21.36]
SharePriceNode:MSFT -> SharePriceEvent[symbolId=MSFT, price=22.11]
SharePriceNode:AMZN -> SharePriceEvent[symbolId=AMZN, price=72.6]

tear down marketPriceProcessor:
remove subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=MSFT} subscriber:com.fluxtion.compiler.generation.targets.InMemoryEventProcessor@13c9d689
remove subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=AMZN} subscriber:com.fluxtion.compiler.generation.targets.InMemoryEventProcessor@13c9d689
MarketDataFeed removing subscriber, count:0

restart marketPriceProcessor:
MarketDataFeed adding new subscriber, count:1
MarketDataFeed subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=AMZN}
MarketDataFeed subscription:EventSubscription{eventClass=class com.fluxtion.example.cookbook.subscription.SharePriceEvent, filterString=MSFT}

publishing prices from MarketDataFeed:
SharePriceNode:MSFT -> SharePriceEvent[symbolId=MSFT, price=22.51]
{% endhighlight %}

