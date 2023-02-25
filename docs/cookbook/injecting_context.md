---
title: Injecting user context
parent: Cookbook
has_children: false
nav_order: 1
published: true
---

## Introduction

Inject user context into the generated event processor. The context is available in 
[EventProcessorContext]({{site.fluxtion_src_runtime}}/EventProcessorContext.java)
for consumption wihtin a node. 

1. Inject the EventProcessorContext into the user node using the [@Inject]({{site.fluxtion_src_runtime}}/annotations/builder/Inject.java) annotation
2. Build the event processor using one of the [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) build methods
3. Provide a Map with user properties to the newly created EventProcessor, calling [EventProcessor.setContextParameterMap]({{site.fluxtion_src_runtime}}/StaticEventProcessor.java#L80)
4. Call init on the EventProcessor
5. The node's initialise method is called and the context will be guaranteed to be populated with the user map


{% highlight java %}
public static class Sample {
    public static void main(String[] args) {
        var eventProcessor = Fluxtion.compile(c -> c.addNode(new MyStringHandler()));
        var now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        eventProcessor.setContextParameterMap(Map.of(
            "started", now,
            "key1", "value1"
        ));
        eventProcessor.init();
        eventProcessor.onEvent("key1");
    }
    
    public static class MyStringHandler {
        @Inject
        public EventProcessorContext context;
        public String in;
    
        @OnEventHandler
        public void stringKeyUpdated(String in) {
            System.out.println("mapping " + in + " -> '" + context.getContextProperty(in) + "'");
        }
    
        @Initialise
        public void init() {
            System.out.println("started: '" + context.getContextProperty("started") + "'");
        }
    }
}
{% endhighlight %}


Running the sample produces this output:

{% highlight console %}
started: '09:02:48.759'
mapping key1 -> 'value1'
{% endhighlight %}






