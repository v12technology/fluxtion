---
title: Injecting runtime instance
parent: Cookbook
has_children: false
nav_order: 5
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/inject/suppliedinstance
---

## Introduction

Inject instances into nodes within a running event processor before calling EventProcessor.init(), using 
StaticEventProcessor.injectInstance(). 

Events and EventHandler can achieve the same behaviour but only after init has been called. it may be desirable to set the 
instances in the graph from an outside source before the init method is executed.


1. Add an [InstanceSupplier]({{site.fluxtion_src_runtime}}/node/InstanceSupplier.java) to a user class. 
2. Mark the InstanceSupplier with an  [@Inject]({{site.fluxtion_src_runtime}}/annotations/builder/Inject.java) annotation
3. Build the EventProcessor
4. Inject an instance to the EventProcessor calling [StaticEventProcessor.injectInstance]({{site.fluxtion_src_runtime}}/StaticEventProcessor.java#L106)
5. Call EventProcessor.init(), the user instance will be bound into the InstanceSupplier in the user node before any
any node initialisation methods are invoked. 

## Example

See the [example here]({{example_src}}), this example uses two injected instances differentiated by am instance name
qualifier in the Inject annotation.

{% highlight java %}
public class GlobalSalesTaxCalculator {

    @Inject(instanceName = "foreignTaxCalculator")
    public InstanceSupplier<TaxCalculator> taxCalculatorForeign;

    @Inject(instanceName = "domesticTaxCalculator")
    public InstanceSupplier<TaxCalculator> taxCalculatorDomestic;

    //cut code for clarity
}
{% endhighlight %}

To access the runtime inject instance the node invokes InstanceSupplier.get():
{% highlight java %}
private void dumpTax() {
    globalTax = foreignUnitsSoldTotal * taxCalculatorForeign.get().taxRate()
    + domesticUnitSoldTotal * taxCalculatorDomestic.get().taxRate();

    //cut code for clarity
}
{% endhighlight %}

The example [main method]({{example_src}}/GlobalSalesTaxCalculatorMain.java) sets the instances before init is called
and chanhes the instance the node refers to dynamically:
{% highlight java %}
public static void main(String[] args) {
    var globalTaxProcessor = Fluxtion.interpret(c -> c.addNode(new GlobalSalesTaxCalculator()));
    globalTaxProcessor.injectNamedInstance(() -> 0.1, TaxCalculator.class, "domesticTaxCalculator");
    globalTaxProcessor.injectNamedInstance(() -> 0.3, TaxCalculator.class, "foreignTaxCalculator");

    globalTaxProcessor.init();
    globalTaxProcessor.onEvent(new DomesticSale(100));
    globalTaxProcessor.onEvent(new ForeignSale(100));
    globalTaxProcessor.onEvent(new DomesticSale(100));

    //update tax rate
    System.out.print("""
                        update foreign tax rate to 20%
                        ---------------------------------------------
                        """);
    globalTaxProcessor.injectNamedInstance(() -> 0.2, TaxCalculator.class, "domesticTaxCalculator");
    globalTaxProcessor.onEvent(new ForeignSale(100));
}
{% endhighlight %}


Running the sample produces this output:

{% highlight console %}
[domestic sold: 100] [tax rate: 0.100000] [Tax due: 10.000000]
[foreign  sold: 0] [tax rate: 0.300000] [Tax due: 0.000000]
Total global tax due : 10.000000

[domestic sold: 100] [tax rate: 0.100000] [Tax due: 10.000000]
[foreign  sold: 100] [tax rate: 0.300000] [Tax due: 30.000000]
Total global tax due : 40.000000

[domestic sold: 200] [tax rate: 0.100000] [Tax due: 20.000000]
[foreign  sold: 100] [tax rate: 0.300000] [Tax due: 30.000000]
Total global tax due : 50.000000

update foreign tax rate to 20%
---------------------------------------------
[domestic sold: 200] [tax rate: 0.200000] [Tax due: 40.000000]
[foreign  sold: 200] [tax rate: 0.300000] [Tax due: 60.000000]
Total global tax due : 100.000000

{% endhighlight %}






