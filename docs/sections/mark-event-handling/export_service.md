---
title: Exporting services
parent: Mark event handling
grand_parent: Reference documentation
has_children: false
nav_order: 3
published: true
---
# Exporting services
{: .no_toc }
---

This section documents the runtime invocation of services that nodes bound within an event processor implement using
Fluxtion.

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - **Create user classes implement an interface, mark the interface with @ExportService annotation**<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }



{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Export service api
Exporting a service interface from a bound class is supported. The generated event processor implements the interface and 
routes calls to bound instances exporting the interface. The normal dispatch rules apply child instances receive trigger
callbacks on a change notification. Steps to export a service

- Create the interface
- Implement the interface in a bound class
- Mark the interface to export with an `@ExportService` annotation
- Lookup the interface on the container using `<T> serviceT = processor.getExportedService()`

The methods on an exported service must either be a boolean or void return type. The return value is used to notify 
a signal change, void is equivalent to returning true. Depending upon the return value the event processor the event 
processor then notifies annotated callback methods according to the [dispatch rules](../fluxtion-explored#event-dispatch-rules).

# Examples

The source project for the examples can be found [here]({{site.reference_examples}}/runtime-execution/src/main/java/com/fluxtion/example/reference/serviceexport)

## Simple service export
Export a single implemented service, only annotated interfaces are exported at the event processor level. The serivce
reference is discovered with a call to the event processor instance, `MyService myService = processor.getExportedService();`

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
