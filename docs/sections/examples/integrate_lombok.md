---
title: Lombok integration
parent: Examples
has_children: false
nav_order: 2
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/lombok
---

## Introduction

Lombok is a utility that removes the need to write java boiler plate code for getters / setters / constructors etc. 
integrating Fluxtion with Lombok reduces the amount of code to write. **As of Fluxtion 8.0.5 this integration is now 
operational**.

Fluxtion generates constructors for each node in the event processor. When constructor arguments share the same type 
an annotation of @AssignToField is required to enable Fluxtion to resolve members correctly.

With Lombok the constructor is generated automatically, and can be configured to copy field annotations 
to the generated constructor :

## **Add to lombok.config file in the root of the project**
```lombok.config
lombok.copyableAnnotations += com.fluxtion.runtime.annotations.builder.AssignToField
```
[Sample lombok.config](https://github.com/v12technology/fluxtion-examples/blob/main/lombok.config)

Create lombok.config if it is missing from your project.

## Code example

[See the example here]({{page.example_src}}/LombokedNode.java)

{% highlight java %}
@Value
public class LombokedNode {

    @AssignToField("id")
    String id;
    @AssignToField("anotherId")
    String anotherId;

    public static void main(String[] args) {
        EventProcessor eventProcessor = Fluxtion.interpret(c -> {
            c.addNode(new LombokedNode("testId", "anotherThing"));
        });
        eventProcessor.init();
        eventProcessor.onEvent("hello world");
    }

    @OnEventHandler
    public boolean MyMarketDataEvent(String input) {
        System.out.println("received:" + input + " id:" + id + " anotherId:" + anotherId);
        return true;
    }
}
{% endhighlight %}

### Running the example prints this to console:

{% highlight console %}
received:hello world id:testId anotherId:anotherThing
{% endhighlight %}
