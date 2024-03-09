---
title: Processing events
parent: Runtime execution
has_children: false
nav_order: 1
published: true
---

# Processing event streams
{: .no_toc }

This section documents the runtime event processing callback api and behaviour. 

Classes bound into an [EventProcessor](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/runtime/src/main/java/com/fluxtion/runtime/EventProcessor.java) register for event callbacks with annotations. The generated EventProcessor
implements the [StaticEventProcessor]({{site.fluxtion_src_runtime}}/StaticEventProcessor.java), with the onEvent method acting as 
a bridge between external event streams and bound processing logic. User code reads the event streams calling onEvent
with each new event received, the event processor then notifies annotated callback methods according to the [dispatch rules](../core-technology#event-dispatch-rules).

The source project for the examples can be found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/execution)

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

## Event processing requirements 
To process an event stream correctly the following requirements must be met:

-  **Call EventProcessor.init() before first use**
-  **EventProcessors are not thread safe** a single event should be processed at one time.

## Event input 
Sends an incoming even to the EventProcessor to trigger a new stream calculation. Any method annotated with 
`@OnEvent` receives the event from the event processor

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
received:TEST
{% endhighlight %}

## Handle multiple Event types
An event handler class can handle multiple event types. Add as many handler methods as required and annotate each method
with an `@OnEvent` annotation.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("String received:" + stringToProcess);
        return true;
    }

    @OnEventHandler
    public boolean handleIntEvent(int intToProcess) {
        System.out.println("Int received:" + intToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.onEvent("TEST");
    processor.onEvent(16);
}
{% endhighlight %}

Output
{% highlight console %}
String received:TEST
Int received:16
{% endhighlight %}

## Filtering events
User events can implement [Event]({{site.fluxtion_src_runtime}}/event/Event.java), which provides an optional filtering 
field. Event handlers can specify the filter value, so they only see events with matching filters


{% highlight java %}
public static class MyNode {
    @OnEventHandler(filterString = "CLEAR_SIGNAL")
    public boolean allClear(Signal<String> signalToProcess) {
        System.out.println("allClear [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler(filterString = "ALERT_SIGNAL")
    public boolean alertSignal(Signal<String> signalToProcess) {
        System.out.println("alertSignal [" + signalToProcess + "]");
        return true;
    }

    @OnEventHandler()
    public boolean anySignal(Signal<String> signalToProcess) {
        System.out.println("anySignal [" + signalToProcess + "]");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    processor.onEvent(new Signal<>("ALERT_SIGNAL", "power failure"));
    System.out.println();
    processor.onEvent(new Signal<>("CLEAR_SIGNAL", "power restored"));
    System.out.println();
    processor.onEvent(new Signal<>("HEARTBEAT_SIGNAL", "heartbeat message"));
}
{% endhighlight %}

Output
{% highlight console %}
alertSignal [Signal: {filterString: ALERT_SIGNAL, value: power failure}]
anySignal [Signal: {filterString: ALERT_SIGNAL, value: power failure}]

allClear [Signal: {filterString: CLEAR_SIGNAL, value: power restored}]
anySignal [Signal: {filterString: CLEAR_SIGNAL, value: power restored}]

anySignal [Signal: {filterString: HEARTBEAT_SIGNAL, value: heartbeat message}]
{% endhighlight %}

## Triggering children
Event notification is propagated to child instances of event handlers. The notification is sent to any method that is
annotated with an `@OnTrigger` annotation. Trigger propagation is in topological order.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleStringEvent(int intToProcess) {
        System.out.println("received:" + intToProcess);
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

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode(), new MyNode2()));
    processor.init();
    processor.onEvent("test");
    System.out.println();   
    processor.onEvent(200);
}
{% endhighlight %}

Output
{% highlight console %}
received:test
Child:triggered

received:200
Child:triggered
{% endhighlight %}

## Conditional triggering children
Event notification is propagated to child instances of event handlers if the event handler method returns a true value. 
A false return value will cause the event processor to swallow the triggering notification.


{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleStringEvent(int intToProcess) {
        boolean propagate = intToProcess > 100;
        System.out.println("conditional propagate:" + propagate);
        return propagate;
    }
}

public static class Child{
    private final MyNode myNode;
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
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
    processor.onEvent("test");
    System.out.println();   
    processor.onEvent(200);
    System.out.println();   
    processor.onEvent(50);
}
{% endhighlight %}

Output
{% highlight console %}
received:test
Child:triggered

conditional propagate:true
Child:triggered

conditional propagate:false
{% endhighlight %}

## Identify triggering parent
It is possible to identify the parent that has triggered a change by adding an `@OnParentUpdate` annotation to a child 
instance. The method must accept a single parameter of the type of the parent to observe. The OnParent callback gives
granular detail of which parent has changed, whereas OnTrigger callbacks signify that at least one parent is triggering.

The OnParent callbacks are guaranteed to be received before the OnTrigger callback.

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
    processor.onEvent("test");
    System.out.println();
    processor.onEvent(200);
    System.out.println();
    processor.onEvent(50);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode2 event received:test
2 - myNode2 updated
MyNode event received:test
1 - myNode updated
Child:triggered

MyNode2 conditional propagate:true
2 - myNode2 updated
Child:triggered

MyNode2 conditional propagate:false
{% endhighlight %}

## Identifying parent by name
When a child has multiple parents of the same type then name resolution can be used to identify the parent that has 
triggered the update. Add the variable name to the `@OnParentyUpdate` annotation to enforce name and type resolution.
The OnParent callback is invoked according to the same rules as conditional triggering. 

{% highlight java %}
public static class MyNode {
    private final String name;

    public MyNode(String name) {
        this.name = name;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println(name + " event received:" + stringToProcess);
        return stringToProcess.equals("*") | stringToProcess.equals(name);
    }
}

public static class Child{
    private final MyNode myNode_a;
    private final MyNode myNode_b;

    public Child(MyNode myNode_a, MyNode myNode_b) {
        this.myNode_a = myNode_a;
        this.myNode_b = myNode_b;
    }

    @OnParentUpdate(value = "myNode_a")
    public void node_a_Updated(MyNode myNode_a){
        System.out.println("Parent A updated");
    }

    @OnParentUpdate("myNode_b")
    public void node_b_Updated(MyNode myNode_b){
        System.out.println("Parent B updated");
    }

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode("A"), new MyNode("B")));
    processor.init();
    processor.onEvent("test");
    System.out.println();
    processor.onEvent("*");
    System.out.println();
    processor.onEvent("A");
    System.out.println();
    processor.onEvent("B");
}
{% endhighlight %}

Output
{% highlight console %}
A event received:test
B event received:test

A event received:*
Parent A updated
B event received:*
Parent B updated
Child:triggered

A event received:A
Parent A updated
B event received:A
Child:triggered

A event received:B
B event received:B
Parent B updated
Child:triggered
{% endhighlight %}

## Functional support
The Fluxtion compiler supports functional construction of event processing logic, this allows developers to bind functions
into the processor without having to construct classes. Functional building is accessed through the 
[DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) 
builder methods.

## Re-entrant events
Events can be added for processing from inside the graph for processing in the next available cycle. Internal events
are added to LIFO queue for processing in the correct order. The EventProcessor instance maintains the LIFO queue, any 
new input events are queued if there is processing currently acting. Support for internal event publishing is built 
into the streaming api.

Maps an int signal to a String and republishes to the graph
{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received [" + stringToProcess +"]");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
                DataFlow.subscribeToIntSignal("myIntSignal")
                        .mapToObj(d -> "intValue:" + d)
                        .console("republish re-entrant [{}]")
                        .processAsNewGraphEvent();
                cfg.addNode(new MyNode());
            }
    );
    processor.init();
    processor.publishSignal("myIntSignal", 256);
}
{% endhighlight %}

Output
{% highlight console %}
republish re-entrant [intValue:256]
received [intValue:256]
{% endhighlight %}

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

## After event callback
Register for a post event method callback with the `@AfterEvent` annotation. The callback will be executed whenever
any event is sent to the event processor. Unlike the `@AfterTrigger` which is only called if the containing instance has
been triggered.

{% highlight java %}
public static class MyNode {
    @Initialise
    public void init(){
        System.out.println("MyNode::init");
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent received:" + stringToProcess);
        return true;
    }

    @AfterEvent
    public void afterEvent(){
        System.out.println("MyNode::afterEvent");
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    System.out.println();
    processor.onEvent("TEST");
    System.out.println();
    processor.onEvent(23);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::init
MyNode::afterEvent

MyNode::handleStringEvent received:TEST
MyNode::afterEvent

MyNode::afterEvent
{% endhighlight %}

## After event callback
Register for a post trigger method callback with the `@AfterTrigger` annotation. The callback will only be executed if 
this class has been triggered on tby an incoming event. Unlike the `@AfterEvent` which is always called on any event.

{% highlight java %}
public static class MyNode {
    @Initialise
    public void init(){
        System.out.println("MyNode::init");
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent received:" + stringToProcess);
        return true;
    }

    @AfterTrigger
    public void afterTrigger(){
        System.out.println("MyNode::afterTrigger");
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();
    System.out.println();
    processor.onEvent("TEST");
    System.out.println();
    processor.onEvent(23);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::init

MyNode::handleStringEvent received:TEST
MyNode::afterTrigger
{% endhighlight %}

## Push trigger
Invert the trigger order so the instance holding the reference receives the event notification before the reference target 
and can push data into the target. Annotate the reference to be a push target with the `@PushReference` annotation.

The normal order is to trigger the target first, which can perform internal calculations if required. Then the instance 
holding the reference is triggered so it can pull calculated data from the target reference.

{% highlight java %}
public static class MyNode {
    @PushReference
    private final PushTarget pushTarget;

    public MyNode(PushTarget pushTarget) {
        this.pushTarget = pushTarget;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent " + stringToProcess);
        if (stringToProcess.startsWith("PUSH")) {
            pushTarget.myValue = stringToProcess;
            return true;
        }
        return false;
    }
}

public static class PushTarget {
    public String myValue;

    @OnTrigger
    public boolean onTrigger() {
        System.out.println("PushTarget::onTrigger -> myValue:'" + myValue + "'");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode(new PushTarget()));
    processor.init();
    processor.onEvent("PUSH - test 1");
    System.out.println();
    processor.onEvent("ignore me - XXXXX");
    System.out.println();
    processor.onEvent("PUSH - test 2");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::handleStringEvent PUSH - test 1
PushTarget::onTrigger ->  myValue:'PUSH - test 1'

MyNode::handleStringEvent ignore me - XXXXX

MyNode::handleStringEvent PUSH - test 2
PushTarget::onTrigger ->  myValue:'PUSH - test 2'
{% endhighlight %}

# To be completed
- Complex graphs


- No propagate
- No trigger reference
- Export service
- Collection support, parent update
- Trigger override
- Forking
- Dynamic filter
- Batch support
- Buffer/trigger

{% highlight java %}

{% endhighlight %}

Output
{% highlight console %}

{% endhighlight %}

