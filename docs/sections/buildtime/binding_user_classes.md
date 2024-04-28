---
title: Binding user classes
parent: Build event processor
has_children: false
nav_order: 1
published: true
---

# Binding user classes to an event processor
{: .no_toc }

The Fluxtion compiler generates an event processor from a model supplied by the client. A model represents the set of user classes
that are bound into the event processor. This section documents the various ways the model can be created by the developer. 
[EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java) is the class that acts as the model,
a number of options are available for adding user classes to an EventProcessorConfig instance:

* Imperative api
* Functional dsl api
* Spring based
* Yaml based

These examples use `Fluxtion.interpret` which executes the event processor as an in-process interpretation, the 
available output types of the generated event processor are described in [Processor generation](processor_generation.md).

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Example project
The source project for the examples can be found [here]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/binding)

# Imperative model building
Call one of the static Fluxtion [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) build methods with a
`Consumer<EventProcessorConfig>`. User classes are bound into the model by invoking methods on the supplied 
EventProcessorConfig instance in the consumer. Fluxtion implicitly creates the EventProcessorConfig instance, which is then passed into the generator.

**Only nodes in the model that are annotated with a fluxtion annotation are bound into an event processor**

## Imperatively add a node
Bind an instance of MyNode into the supplied EventProcessorConfig using `Fluxtion.interpret(cfg -> cfg.addNode(new MyNode()))`. 

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> cfg.addNode(new MyNode()));
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
received:TEST
{% endhighlight %}

## Imperatively add multiple nodes
Bind multiple object instances into the model. 

{% highlight java %}
public static class MyNode {
    private final String name;

    public MyNode(String name) {
        this.name = name;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println(name + " received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        cfg.addNode(new MyNode("node_1"));
        cfg.addNode(new MyNode("node_2"));
        cfg.addNode(new MyNode("node_3"));
    });
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
node_1 received:TEST
node_2 received:TEST
node_3 received:TEST
{% endhighlight %}

## Implicitly adding nodes
imperatively added instances are root nodes for the model. The references of a node are recursively analysed, if a 
reference points to an instance that has a fluxtion annotation that node is added to the model implicitly. Any implicitly
added node will be analysed for implicitly adding more nodes, and so on recursively.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::received:" + stringToProcess);
        return true;
    }
}

public static class Root1 {
    private final MyNode myNode;

    public Root1(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println("Root1::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> cfg.addNode(new Root1(new MyNode())));
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::received:TEST
Root1::triggered
{% endhighlight %}


## Varargs adding nodes
Add root nodes to the model using the varargs derivative of Fluxtion builder method. Can be useful if no customisation
of the model is needed and only nodes need to be included in the generated processor.

{% highlight java %}
public static class MyNode {
        private final String name;

        public MyNode(String name) {
            this.name = name;
        }

        @OnEventHandler
        public boolean handleStringEvent(String stringToProcess) {
            System.out.println(name + " received:" + stringToProcess);
            return true;
        }
    }

    public static void main(String[] args) {
        var processor = Fluxtion.interpret(
                new MyNode("node_1"),
                new MyNode("node_2"),
                new MyNode("node_3"));
        processor.init();
        processor.onEvent("TEST");
    }
{% endhighlight %}

Output
{% highlight console %}
node_1 received:TEST
node_2 received:TEST
node_3 received:TEST
{% endhighlight %}



## Adding shared references
If two nodes point to a shared instance, that instance will only be added once to the model. The shared node will
trigger both children when propagating event notification.

{% highlight java %}
public static class MyNode {
    private final String name;

    public MyNode(String name) {
        this.name = name;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println(name + "::received:" + stringToProcess);
        return true;
    }
}

public static class Root1 {
    private final String name;
    private final MyNode myNode;

    public Root1(String name, MyNode myNode) {
        this.name = name;
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println(name + "::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        MyNode myNode1 = new MyNode("myNode_1");
        cfg.addNode(new Root1("root_1", myNode1));
        cfg.addNode(new Root1("root_2", myNode1));
    });
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
myNode_1::received:TEST
root_1::triggered
root_2::triggered
{% endhighlight %}

## Adding references that are equal
The model acts like a set, checking equality when a node is added imperatively or implicitly. If the instance to add is
equal to a node already in the model it is substituted so only one instance is in the model. References to the duplicate 
will be re-directed to point at the existing node.

{% highlight java %}
public static class MyNode {
    private final String name;
    int identifier;

    public MyNode(String name, int identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println(name + " identifier:" + identifier + " received:" + stringToProcess);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyNode myNode = (MyNode) o;
        return name.equals(myNode.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "MyNode{" +
                "name='" + name + '\'' +
                ", identifier=" + identifier +
                '}';
    }
}

public static class Root1 {
    private final String name;
    private final MyNode myNode;

    public Root1(String name, MyNode myNode) {
        this.name = name;
        this.myNode = myNode;
        System.out.println(name + "::new " + myNode);
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println(name + "::triggered " + myNode);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        MyNode myNode1 = new MyNode("myNode_1", 999);
        MyNode myNode2 = new MyNode("myNode_1", 444);
        cfg.addNode(new Root1("root_1", myNode1));
        cfg.addNode(new Root1("root_2", myNode2));
    });
    processor.init();
    System.out.println();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
root_1::new MyNode{name='myNode_1', identifier=999}
root_2::new MyNode{name='myNode_1', identifier=444}

myNode_1 identifier:999 received:TEST
root_1::triggered MyNode{name='myNode_1', identifier=999}
root_2::triggered MyNode{name='myNode_1', identifier=999}
{% endhighlight %}

## Naming nodes 
Nodes can be given a name when they are added to the graph, if a node has been previously added with the same name the
generation process will fail with a name clash. A node can be accessed by calling `EventProcessor.getById`. There are 
two ways to give a node an addressable identifier:

* Implement the Named interface on a bound node
* Call `EventProcessorConfig.addnode` with a name as the second argument


{% highlight java %}
public static class MyNode {
    private final String name;

    public MyNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

public static class MyNamedNode implements NamedNode {
    private final String name;

    public MyNamedNode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

public static void main(String[] args) throws NoSuchFieldException {
    var processor = Fluxtion.interpret(cfg -> {
        cfg.addNode(new MyNode("customName"), "overrideName");
        cfg.addNode(new MyNamedNode("name1"));
    });
    processor.init();

    System.out.println(processor.<MyNode>getNodeById("overrideName").getName());
    System.out.println(processor.<MyNamedNode>getNodeById("name1").getName());
}
{% endhighlight %}

Output
{% highlight console %}
customName
name1
{% endhighlight %}

## Using name as equality
The string name can be used as the equality test removing the need for users to implement custom equals and hashcode 
methods. A utility class [SingleNamedNode]({{site.fluxtion_src_runtime}}/node/SingleNamedNode.java) can be extended to 
simplify implementation. As equals, hashcode and getName are all synchronised name clashes are avoided and single instance
is in the model. This allows user building code to use name as a key to create a shared reference when building.

{% highlight java %}
public static class MyNode extends SingleNamedNode {
    public MyNode(String name) {
        super(name);
    }
}

public static void main(String[] args) throws NoSuchFieldException {
    var processor = Fluxtion.interpret(new MyNode("name1"));
    processor.init();

    System.out.println(processor.<MyNode>getNodeById("name1").getName());
}
{% endhighlight %}

Output
{% highlight console %}
name1
{% endhighlight %}

## Inject a reference
Fluxtion supports injecting a reference with the `@Inject` annotation, a new instance will be created by the model 
with the default constructor and added implicitly to the model. The injected instance will analysed for implicit
nodes to add to the model.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::received:" + stringToProcess);
        return true;
    }
}

public static class Root1 {
    @Inject
    private final MyNode myNode;

    public Root1() {
        myNode = null;
    }

    public Root1(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println("Root1::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> cfg.addNode(new Root1()));
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::received:TEST
Root1::triggered
{% endhighlight %}

## Inject a singleton
Fluxtion supports injecting a singleton reference with the `@Inject(singleton = true)` annotation, the same reference
is used throughout the model so only one instance is present in the generated processor.

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::received:" + stringToProcess);
        return true;
    }
}

public static class Root1{
    @Inject(singleton = true)
    private final MyNode myNode;
    private final String id;

    public Root1(String id) {
        this(null, id);
    }

    public Root1(MyNode myNode, String id) {
        this.myNode = myNode;
        this.id = id;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println(id + "::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Root1("r1"), new Root1("r2"), new Root1("r3"));
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::received:TEST
r1::triggered
r2::triggered
r3::triggered
{% endhighlight %}

## Inject from factory
A factory can supply the injected instance to the model. A user implements the [NodeFactory]({{site.fluxtion_src_compiler}}/builder/factory/NodeFactory.java)
and returns the instance to the model. Factories can be registered programmatically with EventProcessorConfig but the
easiest method is to use the ServiceLoader pattern to register factories. In this example the google auto service 
annotation to remove all the boiler plate code `@AutoService(NodeFactory.class)`.

Configuration key/values can be supplied at the inject site e.g.

`@Config(key = "filter", value = "red")`

Tuples are wrapped in a map and passed to the NodeFactory.createNode as an argument,  the factory creates 
custom instances using the map as required.

{% highlight java %}
public static class MyNode {
    private final String filter;

    public MyNode(String filter) {
        this.filter = filter;
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        boolean match = stringToProcess.equals(filter);
        System.out.println(toString() +  " match:" + match + " for:" + stringToProcess);
        return match;
    }

    @Override
    public String toString() {
        return "MyNode{" +
                "filter='" + filter + '\'' +
                '}';
    }
}

public static class Root1 {
    @Inject
    @Config(key = "filter", value = "red")
    public MyNode myNodeRed;

    @Inject
    @Config(key = "filter", value = "blue")
    public MyNode myNodeBlue;

    @OnParentUpdate
    public void parentUpdated(Object parent){
        System.out.println("Root1::parentUpdated " + parent);
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println("Root1::triggered");
        return true;
    }
}

@AutoService(NodeFactory.class)
public static class MyNodeFactory implements NodeFactory<MyNode>{
    @Override
    public MyNode createNode(Map<String, Object> config, NodeRegistry registry) {
        return new MyNode((String) config.get("filter"));
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        cfg.addNode(new Root1());
    });
    processor.init();

    processor.onEvent("red");
    System.out.println();
    processor.onEvent("ignored");
    System.out.println();
    processor.onEvent("blue");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode{filter='red'} match:true for:red
Root1::parentUpdated MyNode{filter='red'}
MyNode{filter='blue'} match:false for:red
Root1::triggered

MyNode{filter='red'} match:false for:ignored
MyNode{filter='blue'} match:false for:ignored

MyNode{filter='red'} match:false for:blue
MyNode{filter='blue'} match:true for:blue
Root1::parentUpdated MyNode{filter='blue'}
Root1::triggered
{% endhighlight %}

## Add an auditor
An [Auditor]({{site.fluxtion_src_runtime}}/audit/Auditor.java) can be bound into the generated event processor. An 
auditor receives meta-data callbacks that allows tracking of the event processing as notifications propagate through
the event processor. Implement the Auditor interface and bind it in the processor with:

`cfg.addAuditor(new MyAuditor(), "myAuditor")`

{% highlight java %}
public static class MyNode extends SingleNamedNode {
    public MyNode(String name) {
        super(name);
    }

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        return true;
    }

    @Override
    public String toString() {
        return "MyNode{}";
    }
}


public static class Root1 {
    private final MyNode myNode;

    public Root1(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        return true;
    }

    @Override
    public String toString() {
        return "Root1{" +
                "myNode=" + myNode +
                '}';
    }
}

public static class MyAuditor implements Auditor{
    @Override
    public void nodeRegistered(Object node, String nodeName) {
        System.out.printf("nodeRegistered  nodeName:'%s'  node:'%s'%n", nodeName, node);
    }

    @Override
    public void eventReceived(Object event) {
        System.out.println("eventReceived " + event);
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object event) {
        System.out.printf("nodeInvoked  nodeName:'%s' invoked:'%s' node:'%s'%n", nodeName, methodName, node);
    }

    @Override
    public boolean auditInvocations() {
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        cfg.addNode(new MyNode("unlinked"), new Root1(new MyNode("linked")));
        cfg.addAuditor(new MyAuditor(), "myAuditor");
    });
    processor.init();

    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
nodeRegistered  nodeName:'linked'  node:'MyNode{}'
nodeRegistered  nodeName:'unlinked'  node:'MyNode{}'
nodeRegistered  nodeName:'root1_0'  node:'Root1{myNode=MyNode{}}'
nodeRegistered  nodeName:'callbackDispatcher'  node:'CallbackDispatcherImpl(eventProcessor=com.fluxtion.compiler.generation.targets.InMemoryEventProcessor@79ad8b2f, myStack=[], dispatching=false)'
nodeRegistered  nodeName:'subscriptionManager'  node:'com.fluxtion.runtime.input.SubscriptionManagerNode@59fa1d9b'
nodeRegistered  nodeName:'context'  node:'MutableEventProcessorContext{map={}}'
eventReceived Init
eventReceived TEST
nodeInvoked  nodeName:'linked' invoked:'handleStringEvent' node:'MyNode{}'
nodeInvoked  nodeName:'root1_0' invoked:'trigger' node:'Root1{myNode=MyNode{}}'
nodeInvoked  nodeName:'unlinked' invoked:'handleStringEvent' node:'MyNode{}'
{% endhighlight %}

## Exclude a node from binding
A user node can be excluded from binding into the model by adding the annotation `@ExcludeNode` on a user class. An 
excluded class can be used as a holder for complex construction logic when the user does not want to use a NodeFactory.


{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::received:" + stringToProcess);
        return true;
    }
}

@ExcludeNode
public static class Root1 {
    private final MyNode myNode;

    public Root1(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println("Root1::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new Root1(new MyNode()));
    processor.init();
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
MyNode::received:TEST
{% endhighlight %}


## Inject runtime instance
Instances can be injected at runtime to a node using the `@Inject(instanceName = "startData")` annotation on a 
[InstanceSupplier]({{site.fluxtion_src_runtime}}/node/InstanceSupplier.java)  data member. The instance has to be injected at 
runtime to a built event processor before calling init with:

`processor.injectNamedInstance(new Date(1000000), "startData")`

Instances can be updated once the processor is running by injecting a new instance with the same name.

{% highlight java %}
public static class MyNode{
    @Inject(instanceName = "startData")
    public InstanceSupplier<Date> myDate;

    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("runtime injected:" + myDate.get());
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(new MyNode());
    processor.injectNamedInstance(new Date(1000000), "startData");

    processor.init();
    processor.onEvent("TEST");

    processor.injectNamedInstance(new Date(999000000), "startData");
    processor.onEvent("TEST");
}
{% endhighlight %}

Output
{% highlight console %}
runtime injected:Thu Jan 01 01:16:40 GMT 1970
runtime injected:Mon Jan 12 14:30:00 GMT 1970
{% endhighlight %}

# Functional model building
Fluxtion supports binding functions into the event processor using functional programming. The [DataFlow]({{site.fluxtion_src_compiler}}/builder/dataflow/DataFlow.java) class 
provides static method that subscribe to events. Once a flow has been built map/filter/grouping functions can be applied
as chained calls. The flow must be built within the [Fluxtion]({{site.fluxtion_src_compiler}}/Fluxtion.java) build method,
DataFlow will add all the functions and classes to the model automatically.


## Bind functions to events 
To bind functions to a flow of events the subscription must first be created with:

`DataFlow.subscribe([event class])`

A lambda or a method reference can be bound as the next item in the function flow. A full description of the functional
api is in [Functional programming](../runtime/functional_event_processing.md)

{% highlight java %}
public static String toUpper(String incoming){
    return incoming.toUpperCase();
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
                .console("input: '{}'")
                .map(FunctionalStatic::toUpper)
                .console("transformed: '{}'");
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
input: 'hello world'
transformed: 'HELLO WORLD'
{% endhighlight %}

## Bind instance functions
Instance functions can be bound into the event processor using method references

{% highlight java %}
public static class PrefixString{
    private final String prefix;

    public PrefixString(String prefix) {
        this.prefix = prefix;
    }

    public String addPrefix(String input){
        return prefix + input;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
                .console("input: '{}'")
                .map(new PrefixString("XXXX")::addPrefix)
                .console("transformed: '{}'");
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
input: 'hello world'
transformed: 'XXXXhello world'
{% endhighlight %}

## Combining imperative and functional binding
Both imperative and functional binding can be used in the same build consumer. All the user classes and functions will
be added to the model for generation.

{% highlight java %}
public static String toUpper(String incoming){
    return incoming.toUpperCase();
}

public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("IMPERATIVE received:" + stringToProcess);
        return true;
    }
}

public static void main(String[] args) {
    var processor = Fluxtion.interpret(cfg -> {
        DataFlow.subscribe(String.class)
                .console("FUNCTIONAL input: '{}'")
                .map(CombineFunctionalAndImperative::toUpper)
                .console("FUNCTIONAL transformed: '{}'");

        cfg.addNode(new MyNode());
    });

    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

Output
{% highlight console %}
FUNCTIONAL input: 'hello world'
FUNCTIONAL transformed: 'HELLO WORLD'
IMPERATIVE received:hello world
{% endhighlight %}

# Build with spring configuration
Spring configuration is natively supported by Fluxtion. Any beans in the spring ApplicationContext will be bound into 
the model and eventually the generated event processor. Pass the spring ApplicationContext into fluxtion with 

`FluxtionSpring.interpret(ApplicationContext)`

{% highlight java %}
public static class MyNode {
    @OnEventHandler
    public boolean handleStringEvent(String stringToProcess) {
        System.out.println("MyNode::received:" + stringToProcess);
        return true;
    }
}

public static class Root1 {
    private final MyNode myNode;

    public Root1(MyNode myNode) {
        this.myNode = myNode;
    }

    @OnTrigger
    public boolean trigger() {
        System.out.println("Root1::triggered");
        return true;
    }
}

public static void main(String[] args) {
    var context = new ClassPathXmlApplicationContext("com/fluxtion/example/reference/spring-example.xml");
    var processor = FluxtionSpring.interpret(context);
    processor.init();

    processor.onEvent("TEST");
}
{% endhighlight %}

The spring application config

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="
http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myNode" class="com.fluxtion.example.reference.SpringConfigAdd.MyNode">
    </bean>

    <bean id="root1" class="com.fluxtion.example.reference.SpringConfigAdd.Root1">
        <constructor-arg ref="myNode"/>
    </bean>
</beans>
{% endhighlight %}

Output
{% highlight console %}
MyNode::received:TEST
Root1::triggered
{% endhighlight %}


# To be documented

- yaml


