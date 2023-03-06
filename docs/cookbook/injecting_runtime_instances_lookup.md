---
title: Injecting at runtime - instance Lookup
parent: Cookbook
has_children: false
nav_order: 5
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/inject/lookupinstance
---

## Introduction

Inject instances into nodes within a running event processor before calling EventProcessor.init(), using
[EventProcessorContext]({{site.fluxtion_src_runtime}}/EventProcessorContext.java).getInjectedInstance() at runtime to 
perform a lookup. 

Events and EventHandler can inject instances into the running event processor only after init has been called. 
it may be desirable to set the instances in the graph from an outside source before the init method is executed. 


1. EventProcessor build time:
   2. Create a user class that will be included in an [EventProcessor]({{site.EventProcessor_link}})
   2. [Annotate an init method]({{site.fluxtion_src_runtime}}/annotations/Initialise.java) in the user class 
   2. Inject an [EventProcessorContext]({{site.fluxtion_src_runtime}}/EventProcessorContext.java) to give access to the runtime lookup
   3. In the init method lookup instances with [EventProcessorContext]({{site.fluxtion_src_runtime}}/EventProcessorContext.java).getInjectedInstance()
4. Injecting at runtime:
   5. Build the EventProcessor with the user class included in the graph 
   5. Inject an instance to the EventProcessor calling [StaticEventProcessor.injectInstance]({{site.fluxtion_src_runtime}}/StaticEventProcessor.java#L106)
   6. Call EventProcessor.init(), the instance will be bound into the EventProcessorContext in the user node before any
   any node initialisation methods are invoked. 

## Example

See the [example here]({{page.example_src}}), this example uses four injected instances 
- Three instances are of the same type and differentiated by a instance name
- One instance is resolved by type


[StringProcessor]({{page.example_src}}/StringProcessor.java) performs a lookup in the init method at 
the runtime and binds the instances to local variables that will be used during processing. The teardown method dumps
the result to console.

{% highlight java linenos %}

public class StringProcessor {
@Inject
public EventProcessorContext context;
private List<String> upperCaseStrings;
private List<String> lowerCaseStrings;
private List<String> mixedCaseStrings;
private Supplier<String> titleSupplier;


    @OnEventHandler
    public boolean onString(String in) {
        if (in.toUpperCase().equals(in)) {
            upperCaseStrings.add(in);
        } else if (in.toLowerCase().equals(in)) {
            lowerCaseStrings.add(in);
        } else {
            mixedCaseStrings.add(in);
        }
        return true;
    }

    @Initialise
    @SuppressWarnings("unchecked")
    public void init() {
        titleSupplier = context.getInjectedInstance(Supplier.class);
        upperCaseStrings = context.getInjectedInstance(List.class, "upper");
        lowerCaseStrings = context.getInjectedInstance(List.class, "lower");
        mixedCaseStrings = context.getInjectedInstance(List.class, "mixed");
    }

    @TearDown
    public void tearDown() {
        System.out.printf("""
                
                %s
                ---------------------------------------------------------------------
                upper count: %d
                lower count: %d
                mixed count: %d
                """.formatted(
                titleSupplier.get(),
                upperCaseStrings.size(),
                lowerCaseStrings.size(),
                mixedCaseStrings.size()
        ));
    }
}
{% endhighlight %}

The node lookups up the runtime injected instances with context.getInjectedInstance():
{% highlight java %}
@Initialise
public void init() {
    titleSupplier = context.getInjectedInstance(Supplier.class);
    upperCaseStrings = context.getInjectedInstance(List.class, "upper");
    lowerCaseStrings = context.getInjectedInstance(List.class, "lower");
    mixedCaseStrings = context.getInjectedInstance(List.class, "mixed");
}
{% endhighlight %}

The example [main method]({{page.example_src}}/Main.java) sets the instances before init is called. When init is called
all the instances bound from the main method are available.

{% highlight java %}
public class Main {

    public static void main(String[] args) {
        var stringProcessor = Fluxtion.interpret(c -> c.addNode(new StringProcessor()));
        //bind instances ready for lookup
        stringProcessor.injectInstance(Main::title, Supplier.class);
        stringProcessor.injectNamedInstance(new ArrayList<>(), List.class, "upper");
        stringProcessor.injectNamedInstance(new ArrayList<>(), List.class, "lower");
        stringProcessor.injectNamedInstance(new ArrayList<>(), List.class, "mixed");
        //init
        stringProcessor.init();
        //send data
        stringProcessor.onEvent("test");
        stringProcessor.onEvent("Test");
        stringProcessor.onEvent("START");
        stringProcessor.onEvent("BILL");
        stringProcessor.onEvent("mixedCase");
        //teardown
        stringProcessor.tearDown();
    }

    private static String title(){
        return "StringSplit execution time: " + new Date();
    }
}
{% endhighlight %}


Running the sample produces this output:

{% highlight console %}
StringSplit execution time: Mon Mar 06 07:28:39 GMT 2023
---------------------------------------------------------------------
upper count: 2
lower count: 1
mixed count: 2

{% endhighlight %}






