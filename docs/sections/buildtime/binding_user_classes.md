---
title: Binding user classes
parent: Event processor building
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

* imperative
* declarative
* config driven
* spring config

These examples use `Fluxtion.interpret` which executes the event processor as an in-process interpretation, the 
available output types of the generated event processor are described in [Processor generation](processor_generation.md).

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


## To be documented
- implicit add
- naming nodes
- uniqueness
- factory/inject
- Config for factory
- Including/excluding classes
- Injecting nodes by reference
- Declarative/functional
  - Spring
  - yaml
- Combining declarative and imperative

