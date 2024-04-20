---
title: Library functions
parent: Event handling
has_children: false
nav_order: 5
published: true
---

# Runtime library functions
{: .no_toc }

The runtime environment provides several library functions that bound classes can use. This section documents the runtime
environment and how to access the library functions.


## Processing output
An application can register for output from the EventProcessor by supplying a consumer
to addSink. Support for publishing to a sink is built into the streaming api, `[builder_type]#sink`.
A consumer has a string key to partition outputs.

{% highlight java %}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg ->
    DataFlow.subscribeToIntSignal("myIntSignal")
        .mapToObj(d -> "intValue:" + d)
        .sink("mySink"));
    processor.init();
    processor.addSink("mySink", (Consumer<String>) System.out::println);
    processor.publishSignal("myIntSignal", 10);
    processor.publishSignal("myIntSignal", 256);
}

{% endhighlight %}

Output
{% highlight console %}
intValue:10
intValue:256
{% endhighlight %}

An application can remove sink using the call `EventProcessor#removeSink`

## Buffer and trigger calculation
An event processor can buffer multiple events without causing any triggers to fire, and at some point in the future 
cause all potentially dirty trigger to fire. This is known as buffering and triggering it is achieved by call 
`EventProcessr.bufferEvent` multiple times and then following it with a call `EventProcessor.triggerCalculation`

{% highlight java %}

public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode event received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleIntEvent(int intToProcess) {
        boolean propagate = intToProcess > 100;
        System.out.println("MyNode2 conditional propagate:" + propagate);
        return propagate;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode2 event received:" + stringToProcess);
        return true;
    }
}

public static class Child{
    private final MyNode myNode;
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
    }

    @OnParentUpdate
    public void node1Updated(MyNode myNode1){
        System.out.println("1 - myNode updated");
    }

    @OnParentUpdate
    public void node2Updated(MyNode2 myNode2){
        System.out.println("2 - myNode2 updated");
    }

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode(), new MyNode2()));
    processor.init();
    processor.bufferEvent("test");
    System.out.println();
    processor.bufferEvent(200);
    System.out.println();
    processor.bufferEvent(50);
    System.out.println();
    processor.triggerCalculation();
}
{% endhighlight %}

Output
{% highlight console %}
MyNode2 event received:test
2 - myNode2 updated
MyNode event received:test
1 - myNode updated

MyNode2 conditional propagate:true
2 - myNode2 updated

MyNode2 conditional propagate:false

Child:triggered
{% endhighlight %}

## To be documented
- Time/clock
- Alarms
- Context parameters
- Event processor context
  - Callback and re-entrancy
  - Dirty state
  - Name lookups
  - Event subscriptions
- Callback nodes
- Partitioning
- Auditing
- Event audit logging
