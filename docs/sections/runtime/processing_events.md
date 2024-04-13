---
title: Processing events
parent: Event handling
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

## Handle event input 
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

## Handle multiple event types
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

## Filter variables
The filter value on the event handler method can be extracted from an instance field in the class. Annotate the event
handler method with an attribute that points to the filter variable `@OnEventHandler(filterVariable = "[class variable]")`


{% highlight java %}
public static class MyNode {
    private final String name;

    public MyNode(String name) {
        this.name = name;
    }


    @OnEventHandler(filterVariable = "name")
    public boolean handleIntSignal(Signal.IntSignal intSignal) {
        System.out.printf("MyNode-%s::handleIntSignal - %s%n", name, intSignal.getValue());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode("A"), new MyNode("B"));
    processor.init();

    processor.publishIntSignal("A", 22);
    processor.publishIntSignal("B", 45);
    processor.publishIntSignal("C", 100);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode-A::handleIntSignal - 22
MyNode-B::handleIntSignal - 45
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

## After trigger callback
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

## No propagate event handler
An event handler method can prevent its method triggering a notification by setting the propagate attribute to false 
on any event handler annotation, `@OnEventHandler(propagate = false)`

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent received:" + stringToProcess);
        return true;
    }

    @OnEventHandler(propagate = false)
    public boolean handleIntEvent(int intToProcess) {
        System.out.println("MyNode::handleIntEvent received:" + intToProcess);
        return true;
    }
}

public static class Child {
    private final MyNode myNode;

    public Child(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean triggered(){
        System.out.println("Child:triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(new MyNode()));
    processor.init();
    processor.onEvent("test");
    System.out.println();
    processor.onEvent(200);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::handleStringEvent received:test
Child:triggered

MyNode::handleIntEvent received:200
{% endhighlight %}

## No trigger reference
A child can isolate itself from a parent's event notification by marking the reference with a `@NoTriggerReference`
annotation. This will stop the onTrigger method from firing even when the parent has triggered.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleIntEvent(int intToProcess) {
        System.out.println("MyNode2::handleIntEvent received:" + intToProcess);
        return true;
    }
}


public static class Child {
    private final MyNode myNode;
    @NoTriggerReference
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
    }


    @OnTrigger
    public boolean triggered() {
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
MyNode::handleStringEvent received:test
Child:triggered

MyNode2::handleIntEvent received:200
{% endhighlight %}

## Override trigger reference
A child can force only a single parent to fire its trigger, all other parents will be treated as if they were annotated with 
`@NoTriggerReference` and removed from the event notification triggers for this class.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::handleStringEvent received:" + stringToProcess);
        return true;
    }
}

public static class MyNode2 {
    @OnEventHandler
    public boolean handleIntEvent(int intToProcess) {
        System.out.println("MyNode2::handleIntEvent received:" + intToProcess);
        return true;
    }
}


public static class Child {
    private final MyNode myNode;
    @TriggerEventOverride
    private final MyNode2 myNode2;

    public Child(MyNode myNode, MyNode2 myNode2) {
        this.myNode = myNode;
        this.myNode2 = myNode2;
    }


    @OnTrigger
    public boolean triggered() {
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
MyNode::handleStringEvent received:test

MyNode2::handleIntEvent received:200
Child:triggered
{% endhighlight %}


## Non-dirty triggering
The condition that causes a trigger callback to fire can be inverted so that an indication of no change from the parent
will cause the trigger to fire.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(int intToProcess) {
        boolean propagate = intToProcess > 100;
        System.out.println("conditional propagate:" + propagate);
        return propagate;
    }
}


public static class Child {
    private final MyNode myNode;

    public Child(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean triggered() {
        System.out.println("Child:triggered");
        return true;
    }
}

public static class NonDirtyChild {
    private final MyNode myNode;

    public NonDirtyChild(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger(dirty = false)
    public boolean triggered() {
        System.out.println("NonDirtyChild:triggered");
        return true;
    }
}

public static void main(String[] args) {
    MyNode myNode = new MyNode();
    var processor = Fluxtion.interpret(new Child(myNode), new NonDirtyChild(myNode));
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
conditional propagate:true
Child:triggered

conditional propagate:false
NonDirtyChild:triggered
{% endhighlight %}

## Collection support
Collections or arrays of references are supported, if any element in the collection fires a change notification the 
trigger method will be called. The trigger method is invoked only once per event cycle whatever the number of 
parent's updating. 

Parent change identity can be tracked using the `@OnParentUpdate` annotation.

{% highlight java %}
public static class MyNode {
    @FilterId
    private final String filter;
    private final String name;

    public MyNode(String filter, String name) {
        this.filter = filter;
        this.name = name;
    }

    @OnEventHandler
    public boolean handleIntSignal(IntSignal intSignal) {
        System.out.printf("MyNode-%s::handleIntSignal - %s%n", filter, intSignal.getValue());
        return true;
    }
}

public static class Child {
    private final MyNode[] nodes;
    private int updateCount;

    public Child(MyNode... nodes) {
        this.nodes = nodes;
    }

    @OnParentUpdate
    public void parentUpdated(MyNode updatedNode) {
        updateCount++;
        System.out.printf("parentUpdated '%s'%n", updatedNode.name);
    }

    @OnTrigger
    public boolean triggered() {
        System.out.printf("Child::triggered updateCount:%d%n%n", updateCount);
        updateCount = 0;
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Child(
            new MyNode("A", "a_1"),
            new MyNode("A", "a_2"),
            new MyNode("B", "b_1")));
    processor.init();
    processor.publishIntSignal("A", 10);
    processor.publishIntSignal("B", 25);
    processor.publishIntSignal("A", 12);
    processor.publishIntSignal("C", 200);
}
{% endhighlight %}

Output
{% highlight console %}
MyNode-A::handleIntSignal - 10
parentUpdated 'a_1'
MyNode-A::handleIntSignal - 10
parentUpdated 'a_2'
Child::triggered updateCount:2

MyNode-B::handleIntSignal - 25
parentUpdated 'b_1'
Child::triggered updateCount:1

MyNode-A::handleIntSignal - 12
parentUpdated 'a_1'
MyNode-A::handleIntSignal - 12
parentUpdated 'a_2'
Child::triggered updateCount:2
{% endhighlight %}

## Export service
Exporting a service interface from a bound class is supported. The generated event processor implements the interface and 
routes calls to bound instances exporting the interface. The normal dispatch rules apply child instances receive trigger
callbacks on a change notification. Steps to export a service

- Create the interface
- Implement the interface in a bound class
- Mark the interface to export with an `@ExportService` annotation
- Lookup the interface on the container using `<T> serviceT = processor.getExportedService()`

The methods on an exported service must either be a boolean or void return type. The return value is used to notify 
a signal change, void is equivalent to returning true.

{% highlight java %}
public interface MyService {
    void addNumbers(int a, int b);
}

public static class MyServiceImpl implements @ExportService MyService, IntSupplier {
    private int sum;

    @Override
    public void addNumbers(int a, int b) {
        System.out.printf("adding %d + %d %n", a, b);
        sum = a + b;
    }

    @Override
    public int getAsInt() {
        return sum;
    }
}

public static class ResultPublisher {
    private final IntSupplier intSupplier;

    public ResultPublisher(IntSupplier intSupplier) {
        this.intSupplier = intSupplier;
    }

    @OnTrigger
    public boolean printResult() {
        System.out.println("result - " + intSupplier.getAsInt());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new ResultPublisher(new MyServiceImpl()));
    processor.init();

    //get the exported service
    MyService myService = processor.getExportedService();
    myService.addNumbers(30, 12);
}
{% endhighlight %}

Output
{% highlight console %}
adding 30 + 12
result - 42
{% endhighlight %}

## No propagate service methods
An individual exported method can prevent a triggering a notification by adding `@NoPropagateFunction` to an interface
method

{% highlight java %}

public interface MyService {
    void cumulativeSum(int a);
    void reset();
}

public static class MyServiceImpl implements @ExportService MyService, IntSupplier {

    private int sum;

    @Override
    public void cumulativeSum(int a) {
        sum += a;
        System.out.printf("MyServiceImpl::adding %d cumSum: %d %n", a, sum);
    }

    @Override
    @NoPropagateFunction
    public void reset() {
        sum = 0;
        System.out.printf("MyServiceImpl::reset cumSum: %d %n", sum);
    }

    @Override
    public int getAsInt() {
        return sum;
    }
}

public static class ResultPublisher {
    private final IntSupplier intSupplier;

    public ResultPublisher(IntSupplier intSupplier) {
        this.intSupplier = intSupplier;
    }

    @OnTrigger
    public boolean printResult() {
        System.out.println("ResultPublisher::result - " + intSupplier.getAsInt());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new ResultPublisher(new MyServiceImpl()));
    processor.init();

    //get the exported service
    MyService myService = processor.getExportedService();
    myService.cumulativeSum(11);
    myService.cumulativeSum(31);
    System.out.println();

    myService.reset();
}

{% endhighlight %}

Output
{% highlight console %}
MyServiceImpl::adding 11 cumSum: 11
ResultPublisher::result - 11
MyServiceImpl::adding 31 cumSum: 42
ResultPublisher::result - 42

MyServiceImpl::reset cumSum: 0
{% endhighlight %}

## No propagate service
An entire service can be marked with the  `@ExportService(propagate = false)` resulting in all methods on the interface 
swallowing event notifications 

{% highlight java %}

public class NopPropagateService {
    public interface MyService {
    void cumulativeSum(int a);
    void reset();
}

public static class MyServiceImpl
        implements
        @ExportService(propagate = false) MyService,
        @ExportService Runnable,
        IntSupplier {

    private int sum;

    @Override
    public void cumulativeSum(int a) {
        sum += a;
        System.out.printf("MyServiceImpl::adding %d cumSum: %d %n", a, sum);
    }

    @Override
    public void reset() {
        sum = 0;
        System.out.printf("MyServiceImpl::reset cumSum: %d %n", sum);
    }

    @Override
    public int getAsInt() {
        return sum;
    }

    @Override
    public void run() {
        System.out.println("running calculation - will trigger publish");
    }
}

public static class ResultPublisher {
    private final IntSupplier intSupplier;

    public ResultPublisher(IntSupplier intSupplier) {
        this.intSupplier = intSupplier;
    }

    @OnTrigger
    public boolean printResult() {
        System.out.println("ResultPublisher::result - " + intSupplier.getAsInt());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new ResultPublisher(new MyServiceImpl()));
    processor.init();

    //get the exported service - no triggering notifications fired on this service
    MyService myService = processor.getExportedService();
    myService.cumulativeSum(11);
    myService.cumulativeSum(31);
    System.out.println();

    //will cause a trigger notification
    processor.consumeServiceIfExported(Runnable.class, Runnable::run);

    System.out.println();
    myService.reset();
}

{% endhighlight %}

Output
{% highlight console %}
MyServiceImpl::adding 11 cumSum: 11
MyServiceImpl::adding 31 cumSum: 42

running calculation - will trigger publish
ResultPublisher::result - 42

MyServiceImpl::reset cumSum: 0
{% endhighlight %}



## Forking trigger methods
Forking trigger methods is supported. If multiple trigger methods are fired from a single parent they can be forked to 
run in parallel using the fork join pool. Only when all the forked trigger methods have completed will an event notification
be propagated to their children. 

To for a trigger callback use `@OnTrigger(parallelExecution = true)` annotation on the callback method.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.printf("%s MyNode::handleStringEvent %n", Thread.currentThread().getName());
        return true;
    }
}

public static class ForkedChild {
    private final MyNode myNode;
    private final int id;

    public ForkedChild(MyNode myNode, int id) {
        this.myNode = myNode;
        this.id = id;
    }

    @OnTrigger(parallelExecution = true)
    public boolean triggered() {
        int millisSleep = new Random(id).nextInt(25, 200);
        String threadName = Thread.currentThread().getName();
        System.out.printf("%s ForkedChild[%d]::triggered - sleep:%d %n", threadName, id, millisSleep);
        try {
            Thread.sleep(millisSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("%s ForkedChild[%d]::complete %n", threadName, id);
        return true;
    }
}

public static class ResultJoiner {
    private final ForkedChild[] forkedTasks;

    public ResultJoiner(ForkedChild[] forkedTasks) {
        this.forkedTasks = forkedTasks;
    }

    public ResultJoiner(int forkTaskNumber){
        MyNode myNode = new MyNode();
        forkedTasks = new ForkedChild[forkTaskNumber];
        for (int i = 0; i < forkTaskNumber; i++) {
            forkedTasks[i] = new ForkedChild(myNode, i);
        }
    }

    @OnTrigger
    public boolean complete(){
        System.out.printf("%s ResultJoiner:complete %n%n", Thread.currentThread().getName());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new ResultJoiner(5));
    processor.init();

    Instant start = Instant.now();
    processor.onEvent("test");

    System.out.printf("duration: %d milliseconds%n", Duration.between(start, Instant.now()).toMillis());
}

{% endhighlight %}

Output
{% highlight console %}
main MyNode::handleStringEvent
ForkJoinPool.commonPool-worker-1 ForkedChild[0]::triggered - sleep:135
ForkJoinPool.commonPool-worker-2 ForkedChild[1]::triggered - sleep:85
ForkJoinPool.commonPool-worker-3 ForkedChild[2]::triggered - sleep:58
ForkJoinPool.commonPool-worker-4 ForkedChild[3]::triggered - sleep:184
ForkJoinPool.commonPool-worker-5 ForkedChild[4]::triggered - sleep:112
ForkJoinPool.commonPool-worker-3 ForkedChild[2]::complete
ForkJoinPool.commonPool-worker-2 ForkedChild[1]::complete
ForkJoinPool.commonPool-worker-5 ForkedChild[4]::complete
ForkJoinPool.commonPool-worker-1 ForkedChild[0]::complete
ForkJoinPool.commonPool-worker-4 ForkedChild[3]::complete
main ResultJoiner:complete

duration: 184 milliseconds
{% endhighlight %}

## Batch support
Batch callbacks are supported through the BatchHandler interface that the generated EventHandler implements. Any methods 
that are annotated with, `@OnBatchPause` or `@OnBatchEnd` will receive calls from the matching BatchHandler method. 

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode event received:" + stringToProcess);
        return true;
    }

    @OnBatchPause
    public void batchPause(){
        System.out.println("MyNode::batchPause");
    }

    @OnBatchEnd
    public void batchEnd(){
        System.out.println("MyNode::batchEnd");
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.init();

    processor.onEvent("test");

    //use BatchHandler service
    BatchHandler batchHandler = (BatchHandler)processor;
    batchHandler.batchPause();
    batchHandler.batchEnd();
}
{% endhighlight %}

Output
{% highlight console %}
MyNode event received:test
MyNode::batchPause
MyNode::batchEnd
{% endhighlight %}

