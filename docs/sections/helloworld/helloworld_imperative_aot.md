---
title: AOT hello world
has_children: false
parent: Hello fluxtion world
nav_order: 3
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/imperative-helloworld-aot/src/main/java/com/fluxtion/example/imperative/helloworld
---

# 5 minute hello world - AOT
{: .no_toc }

Use Fluxtion to add two numbers from different event streams and log when the sum > 100.
The sum is the addition of the current value from each event stream. The stream of events can be infinitely long,
calculations are run whenever a new event is received. 

Code is available as a [maven project]({{page.example_src}})

This example **creates an event processor ahead of time using the Fluxtion maven plugin**. Once generated the event processor
is used as a normal user class. The main method instantiates and initialises the event processor, then fires data events at it.
If a breach occurs a warning will be logged to console. All dispatch and change notification is handled by Fluxtion when an event is
received. Business logic resides in the user functions/classes.

For an event based interpreted example see [Hello fluxtion world](helloworld_imperative)

## Processing graph
{: .no_toc }

```mermaid
flowchart TB

    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}
    
    EventA><b>InputEvent</b>::Event_A]:::eventHandler 
    EventB><b>InputEvent</b>::Event_B]:::eventHandler 
    HandlerA[Event_A_Handler\n<b>EventHandler</b>::Event_A]:::graphNode 
    HandlerB[Event_B_Handler\n<b>EventHandler</b>::Event_A]:::graphNode 
    DataSumCalculator:::graphNode
    BreachNotifier:::graphNode

    EventA --> HandlerA
    EventB --> HandlerB
    
    subgraph EventProcessor
      HandlerA --> DataSumCalculator
      HandlerB --> DataSumCalculator
      DataSumCalculator --> BreachNotifier
    end
    
```
## Processing logic
The Fluxtion event processor manages all the event call backs, the user code handles the business logic.

* An event handlers is notified when an event of the matching type is received.
* This in turn invokes the DataSumCalculator annotated trigger method which calculates the current sum extracting values from handler_A and handler_B.
* If the sum > 100 the DataSumCalculator returns true which propagates a notification to the BreachNotifier annotated trigger method.  
* The BreachNotifier trigger method prints a message to the console.


## Dependencies
{: .no_toc }

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'Maven')" >Maven dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')" id="defaultOpen">Gradle dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'pom_xml')">Maven pom</button>
</div>

<div id="pom_xml" class="tabcontent">
<div markdown="1">
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <version>1.0.0-SNAPSHOT</version>
  <parent>
    <artifactId>example.master</artifactId>
    <groupId>com.fluxtion.example</groupId>
  </parent>

  <modelVersion>4.0.0</modelVersion>
  <artifactId>imperative-helloworld</artifactId>
  <name>imperative :: hello world</name>

  <build>
      <plugins>
          <plugin>
              <groupId>com.fluxtion</groupId>
              <artifactId>fluxtion-maven-plugin</artifactId>
              <version>3.0.14</version>
              <executions>
                  <execution>
                      <goals>
                          <goal>scan</goal>
                      </goals>
                  </execution>
              </executions>
          </plugin>
      </plugins>
  </build>

  <dependencies>
      <dependency>
          <groupId>com.fluxtion</groupId>
          <artifactId>compiler</artifactId>
          <version>{{site.fluxtion_version}}</version>
      </dependency>
  </dependencies>
</project>
{% endhighlight %}
</div>
</div>

<div id="Maven" class="tabcontent">
<div markdown="1">
{% highlight xml %}
    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
    </dependencies>
{% endhighlight %}
</div>
</div>

<div id="Gradle" class="tabcontent">
<div markdown="1">
{% highlight groovy %}
implementation 'com.fluxtion:compiler:{{site.fluxtion_version}}'
{% endhighlight %}
</div>
</div>

## Three steps to using Fluxtion
{: .no_toc }

{: .info }
1 - Mark event handling methods with annotations or via functional programming<br>
2 - Build the event processor using fluxtion compiler utility<br>
3 - Integrate the event processor in the app and feed it events
{: .fs-4 }

# Step 1 - annotate event handling methods

There are two types of user classes employed at runtime. First, pojo's with event processing methods that are bound into
the
generated event processor. Secondly, record classes that defines the event types that are fed into the
BreachNotifierProcessor.
The event processor routes events to event handler methods on bound instances.

Annotated callback methods

- **@OnEventHandler** annotation declares the [entry point]({{page.example_src}}/Event_A_Handler.java) of an execution
  path, triggered by an external event.
- **@OnTrigger** annotated [methods]({{page.example_src}}/DataSumCalculator.java) indicate call back methods to be
  invoked if a parent propagates a change.

The return boolean flag from a trigger or event handler method indicates if event notification should be propagated.

## Event handlers

| Name              | Event handler | Trigger handler | Description                                                      |
|-------------------|---------------|-----------------|------------------------------------------------------------------|
| Event_A_Handler   | yes           | no              | Handles incoming events of type Event_A                          |
| Event_B_Handler   | yes           | no              | Handles incoming events of type Event_B                          |
| DataSumCalculator | no            | yes             | References DataHandler nodes and calculates the current sum      |
| BreachNotifier    | no            | yes             | References the DataSumCalculator and logs a warning if sum > 100 |

The event handler method is called when a matching event type is published to the container, the trigger handler is
called when a parent dependency haa been trigger or a parent event handler method has been called.

<div class="tab">
  <button class="tablinks2" onclick="openTab2(event, 'Handler A')" id="defaultExample">Handler A</button>
  <button class="tablinks2" onclick="openTab2(event, 'Handler B')">Handler B</button>
  <button class="tablinks2" onclick="openTab2(event, 'DataSumCalculator')">DataSumCalculator</button>
  <button class="tablinks2" onclick="openTab2(event, 'BreachNotifier')">BreachNotifier</button>
</div>

<div id="Handler A" class="tabcontent2">
<div markdown="1">
An entry point for processing events of type Event_A and stores the latest value as a member variable.
Annotate the event handler method with `@OnEventHandler` as follows:
{% highlight java %}
public class Event_A_Handler {
    private double value;

    @OnEventHandler
    public boolean data1Update(Event_A data1) {
        value = data1.value();
        return true;
    }

    public double getValue() {
        return value;
    }
}
{% endhighlight %}
</div>
</div>

<div id="Handler B" class="tabcontent2">
<div markdown="1">
An entry point for processing events of type Event_B and stores the latest value as a member variable.
Annotate the event handler method with `@OnEventHandler` as follows:
{% highlight java %}
public class Event_B_Handler {
    private double value;

    @OnEventHandler
    public boolean data1Update(Event_B data2) {
        value = data2.value();
        return true;
    }

    public double getValue() {
        return value;
    }
}
{% endhighlight %}
</div>
</div>

<div id="DataSumCalculator" class="tabcontent2">
<div markdown="1">
Calculates the current sum adding the values of Event_A_Handler and Event_B_Handler. Will be triggered when either handler
has its updated method invoked. Annotate the trigger method with **@OnTrigger** as follows:

{% highlight java %}

public class DataSumCalculator {
    private final Event_A_Handler event_A_Handler;
    private final Event_B_Handler event_B_Handler;
    private double sum;
    
    public DataSumCalculator(Event_A_Handler event_A_Handler, Event_B_Handler event_B_Handler) {
        this.event_A_Handler = event_A_Handler;
        this.event_B_Handler = event_B_Handler;
    }
    
    public DataSumCalculator() {
        this(new Event_A_Handler(), new Event_B_Handler());
    }

    @OnTrigger
    public boolean calculate() {
        sum = event_A_Handler.getValue() + event_B_Handler.getValue();
        System.out.println("sum:" + sum);
        return sum > 100;
    }

    public double getSum() {
        return sum;
    }
}

{% endhighlight %}

The return flag indicates that the event notification should be propagated and any child nodes trigger methods
should be invoked.
</div>
</div>


<div id="BreachNotifier" class="tabcontent2">
<div markdown="1">
Logs to console when the sum breaches a value, BreachNotifier holds a reference to the DataSumCalculator instance.
The trigger method is only invoked if the DataSumCalculator propagates the notification, by returning true from its
trigger method. Annotate the trigger method with **@OnTrigger** as follows:

{% highlight java %}

public class BreachNotifier {
    private final DataSumCalculator dataAddition;
    
    public BreachNotifier(DataSumCalculator dataAddition) {
        this.dataAddition = dataAddition;
    }
    
    public BreachNotifier() {
        this(new DataSumCalculator());
    }
    
    @OnTrigger
    public boolean printWarning() {
        System.out.println("WARNING DataSumCalculator value is greater than 100 sum = " + dataAddition.getSum());
        return true;
    }
}

{% endhighlight %}
</div>
</div>

## Events

Java records are used as events.

{% highlight java %}
public record Event_A(double value) {}
public record Event_B(double value) {}
{% endhighlight %}

# Step 2 - build the event processor

All the pojo classes required for processing are linked together using an imperative style in
our [AotBuilder]({{page.example_src}}/AotBuilder.java).
The maven plugin interrogates the builder to generate an event processor that binds in all the user pojos.

Fluxtion generator binds all objects supplied in the `buildGraph(EventProcessorConfig eventProcessorConfig)`
method. Any connected instance will be automatically discovered and added to the final event processor. Due to discovery
only BreachNotifier needs to be added with `eventProcessorConfig.addNode(new BreachNotifier())` to bind the whole user
object graph into the event processor.

The configuration for the generated source file is set in the builder
method `configureGeneration(FluxtionCompilerConfig fluxtionCompilerConfig)`

<div class="tab">
  <button class="tablinks3" onclick="openTab3(event, 'AotBuilder')" id="aotBuilder">AotBuilder</button>
  <button class="tablinks3" onclick="openTab3(event, 'BreachNotifierProcessor')">Generated code</button>
</div>


<div id="AotBuilder" class="tabcontent3">
<div markdown="1">
The [AotBuilder]({{page.example_src}}/AotBuilder.java)  adds user classes imperatively to the EventProcessorConfig in the buildGraph method.
Source generation configuration is handled in the configureGeneration method.

{% highlight java %}
public class AotBuilder implements FluxtionGraphBuilder {

    @Override
    public void buildGraph(EventProcessorConfig eventProcessorConfig) {
        eventProcessorConfig.addNode(new BreachNotifier());
    }

    @Override
    public void configureGeneration(FluxtionCompilerConfig fluxtionCompilerConfig) {
        fluxtionCompilerConfig.setClassName("BreachNotifierProcessor");
        fluxtionCompilerConfig.setPackageName("com.fluxtion.example.imperative.helloworld.generated");
    }
}
{% endhighlight %}
</div>
</div>


<div id="BreachNotifierProcessor" class="tabcontent3">
<div markdown="1">
The AOT generated event processor source file is here [BreachNotifierProcessor.java]({{page.example_src}}/generated/BreachNotifierProcessor.java)

{% highlight java %}

/**
 *
 *
 * <pre>
 * generation time                 : Not available
 * eventProcessorGenerator version : 9.2.23
 * api version                     : 9.2.23
 * </pre>
 *
 * Event classes supported:
 *
 * <ul>
 *   <li>com.fluxtion.example.imperative.helloworld.Event_A
 *   <li>com.fluxtion.example.imperative.helloworld.Event_B
 *   <li>com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent
 * </ul>
 *
 * @author Greg Higgins
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BreachNotifierProcessor
    implements EventProcessor<BreachNotifierProcessor>,
        StaticEventProcessor,
        InternalEventProcessor,
        BatchHandler,
        Lifecycle {

  //Node declarations
  private final CallbackDispatcherImpl callbackDispatcher = new CallbackDispatcherImpl();
  private final Event_A_Handler event_A_Handler_2 = new Event_A_Handler();
  private final Event_B_Handler event_B_Handler_3 = new Event_B_Handler();
  private final DataSumCalculator dataSumCalculator_1 =
      new DataSumCalculator(event_A_Handler_2, event_B_Handler_3);
  private final BreachNotifier breachNotifier_0 = new BreachNotifier(dataSumCalculator_1);
  public final NodeNameAuditor nodeNameLookup = new NodeNameAuditor();
  private final SubscriptionManagerNode subscriptionManager = new SubscriptionManagerNode();
  private final MutableEventProcessorContext context =
      new MutableEventProcessorContext(
          nodeNameLookup, callbackDispatcher, subscriptionManager, callbackDispatcher);
  public final Clock clock = new Clock();
  private final ExportFunctionAuditEvent functionAudit = new ExportFunctionAuditEvent();
  //Dirty flags
  private boolean initCalled = false;
  private boolean processing = false;
  private boolean buffering = false;
  private final IdentityHashMap<Object, BooleanSupplier> dirtyFlagSupplierMap =
      new IdentityHashMap<>(3);
  private final IdentityHashMap<Object, Consumer<Boolean>> dirtyFlagUpdateMap =
      new IdentityHashMap<>(3);

  private boolean isDirty_dataSumCalculator_1 = false;
  private boolean isDirty_event_A_Handler_2 = false;
  private boolean isDirty_event_B_Handler_3 = false;
  //Forked declarations

  //Filter constants

  public BreachNotifierProcessor(Map<Object, Object> contextMap) {
    context.replaceMappings(contextMap);
    //node auditors
    initialiseAuditor(clock);
    initialiseAuditor(nodeNameLookup);
    subscriptionManager.setSubscribingEventProcessor(this);
    context.setEventProcessorCallback(this);
  }

  public BreachNotifierProcessor() {
    this(null);
  }

  @Override
  public void init() {
    initCalled = true;
    auditEvent(Lifecycle.LifecycleEvent.Init);
    //initialise dirty lookup map
    isDirty("test");
    clock.init();
    afterEvent();
  }

  @Override
  public void start() {
    if (!initCalled) {
      throw new RuntimeException("init() must be called before start()");
    }
    processing = true;
    auditEvent(Lifecycle.LifecycleEvent.Start);

    afterEvent();
    callbackDispatcher.dispatchQueuedCallbacks();
    processing = false;
  }

  @Override
  public void stop() {
    if (!initCalled) {
      throw new RuntimeException("init() must be called before stop()");
    }
    processing = true;
    auditEvent(Lifecycle.LifecycleEvent.Stop);

    afterEvent();
    callbackDispatcher.dispatchQueuedCallbacks();
    processing = false;
  }

  @Override
  public void tearDown() {
    initCalled = false;
    auditEvent(Lifecycle.LifecycleEvent.TearDown);
    nodeNameLookup.tearDown();
    clock.tearDown();
    subscriptionManager.tearDown();
    afterEvent();
  }

  @Override
  public void setContextParameterMap(Map<Object, Object> newContextMapping) {
    context.replaceMappings(newContextMapping);
  }

  @Override
  public void addContextParameter(Object key, Object value) {
    context.addMapping(key, value);
  }

  //EVENT DISPATCH - START
  @Override
  public void onEvent(Object event) {
    if (buffering) {
      triggerCalculation();
    }
    if (processing) {
      callbackDispatcher.processReentrantEvent(event);
    } else {
      processing = true;
      onEventInternal(event);
      callbackDispatcher.dispatchQueuedCallbacks();
      processing = false;
    }
  }

  @Override
  public void onEventInternal(Object event) {
    if (event instanceof com.fluxtion.example.imperative.helloworld.Event_A) {
      Event_A typedEvent = (Event_A) event;
      handleEvent(typedEvent);
    } else if (event instanceof com.fluxtion.example.imperative.helloworld.Event_B) {
      Event_B typedEvent = (Event_B) event;
      handleEvent(typedEvent);
    } else if (event instanceof com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent) {
      ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
      handleEvent(typedEvent);
    }
  }

  public void handleEvent(Event_A typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    isDirty_event_A_Handler_2 = event_A_Handler_2.data1Update(typedEvent);
    if (guardCheck_dataSumCalculator_1()) {
      isDirty_dataSumCalculator_1 = dataSumCalculator_1.calculate();
    }
    if (guardCheck_breachNotifier_0()) {
      breachNotifier_0.printWarning();
    }
    afterEvent();
  }

  public void handleEvent(Event_B typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    isDirty_event_B_Handler_3 = event_B_Handler_3.data1Update(typedEvent);
    if (guardCheck_dataSumCalculator_1()) {
      isDirty_dataSumCalculator_1 = dataSumCalculator_1.calculate();
    }
    if (guardCheck_breachNotifier_0()) {
      breachNotifier_0.printWarning();
    }
    afterEvent();
  }

  public void handleEvent(ClockStrategyEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    clock.setClockStrategy(typedEvent);
    afterEvent();
  }
  //EVENT DISPATCH - END

  public void bufferEvent(Object event) {
    buffering = true;
    if (event instanceof com.fluxtion.example.imperative.helloworld.Event_A) {
      Event_A typedEvent = (Event_A) event;
      auditEvent(typedEvent);
      isDirty_event_A_Handler_2 = event_A_Handler_2.data1Update(typedEvent);
    } else if (event instanceof com.fluxtion.example.imperative.helloworld.Event_B) {
      Event_B typedEvent = (Event_B) event;
      auditEvent(typedEvent);
      isDirty_event_B_Handler_3 = event_B_Handler_3.data1Update(typedEvent);
    } else if (event instanceof com.fluxtion.runtime.time.ClockStrategy.ClockStrategyEvent) {
      ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
      auditEvent(typedEvent);
      clock.setClockStrategy(typedEvent);
    }
  }

  public void triggerCalculation() {
    buffering = false;
    String typedEvent = "No event information - buffered dispatch";
    if (guardCheck_dataSumCalculator_1()) {
      isDirty_dataSumCalculator_1 = dataSumCalculator_1.calculate();
    }
    if (guardCheck_breachNotifier_0()) {
      breachNotifier_0.printWarning();
    }
    afterEvent();
  }

  private void auditEvent(Object typedEvent) {
    clock.eventReceived(typedEvent);
    nodeNameLookup.eventReceived(typedEvent);
  }

  private void auditEvent(Event typedEvent) {
    clock.eventReceived(typedEvent);
    nodeNameLookup.eventReceived(typedEvent);
  }

  private void initialiseAuditor(Auditor auditor) {
    auditor.init();
    auditor.nodeRegistered(breachNotifier_0, "breachNotifier_0");
    auditor.nodeRegistered(dataSumCalculator_1, "dataSumCalculator_1");
    auditor.nodeRegistered(event_A_Handler_2, "event_A_Handler_2");
    auditor.nodeRegistered(event_B_Handler_3, "event_B_Handler_3");
    auditor.nodeRegistered(callbackDispatcher, "callbackDispatcher");
    auditor.nodeRegistered(subscriptionManager, "subscriptionManager");
    auditor.nodeRegistered(context, "context");
  }

  private void beforeServiceCall(String functionDescription) {
    functionAudit.setFunctionDescription(functionDescription);
    auditEvent(functionAudit);
    if (buffering) {
      triggerCalculation();
    }
    processing = true;
  }

  private void afterServiceCall() {
    afterEvent();
    callbackDispatcher.dispatchQueuedCallbacks();
    processing = false;
  }

  private void afterEvent() {

    clock.processingComplete();
    nodeNameLookup.processingComplete();
    isDirty_dataSumCalculator_1 = false;
    isDirty_event_A_Handler_2 = false;
    isDirty_event_B_Handler_3 = false;
  }

  @Override
  public void batchPause() {
    auditEvent(Lifecycle.LifecycleEvent.BatchPause);
    processing = true;

    afterEvent();
    callbackDispatcher.dispatchQueuedCallbacks();
    processing = false;
  }

  @Override
  public void batchEnd() {
    auditEvent(Lifecycle.LifecycleEvent.BatchEnd);
    processing = true;

    afterEvent();
    callbackDispatcher.dispatchQueuedCallbacks();
    processing = false;
  }

  @Override
  public boolean isDirty(Object node) {
    return dirtySupplier(node).getAsBoolean();
  }

  @Override
  public BooleanSupplier dirtySupplier(Object node) {
    if (dirtyFlagSupplierMap.isEmpty()) {
      dirtyFlagSupplierMap.put(dataSumCalculator_1, () -> isDirty_dataSumCalculator_1);
      dirtyFlagSupplierMap.put(event_A_Handler_2, () -> isDirty_event_A_Handler_2);
      dirtyFlagSupplierMap.put(event_B_Handler_3, () -> isDirty_event_B_Handler_3);
    }
    return dirtyFlagSupplierMap.getOrDefault(node, StaticEventProcessor.ALWAYS_FALSE);
  }

  @Override
  public void setDirty(Object node, boolean dirtyFlag) {
    if (dirtyFlagUpdateMap.isEmpty()) {
      dirtyFlagUpdateMap.put(dataSumCalculator_1, (b) -> isDirty_dataSumCalculator_1 = b);
      dirtyFlagUpdateMap.put(event_A_Handler_2, (b) -> isDirty_event_A_Handler_2 = b);
      dirtyFlagUpdateMap.put(event_B_Handler_3, (b) -> isDirty_event_B_Handler_3 = b);
    }
    dirtyFlagUpdateMap.get(node).accept(dirtyFlag);
  }

  private boolean guardCheck_breachNotifier_0() {
    return isDirty_dataSumCalculator_1;
  }

  private boolean guardCheck_dataSumCalculator_1() {
    return isDirty_event_A_Handler_2 | isDirty_event_B_Handler_3;
  }

  @Override
  public <T> T getNodeById(String id) throws NoSuchFieldException {
    return nodeNameLookup.getInstanceById(id);
  }

  @Override
  public <A extends Auditor> A getAuditorById(String id)
      throws NoSuchFieldException, IllegalAccessException {
    return (A) this.getClass().getField(id).get(this);
  }

  @Override
  public void addEventFeed(EventFeed eventProcessorFeed) {
    subscriptionManager.addEventProcessorFeed(eventProcessorFeed);
  }

  @Override
  public void removeEventFeed(EventFeed eventProcessorFeed) {
    subscriptionManager.removeEventProcessorFeed(eventProcessorFeed);
  }

  @Override
  public BreachNotifierProcessor newInstance() {
    return new BreachNotifierProcessor();
  }

  @Override
  public BreachNotifierProcessor newInstance(Map<Object, Object> contextMap) {
    return new BreachNotifierProcessor();
  }

  @Override
  public String getLastAuditLogRecord() {
    try {
      EventLogManager eventLogManager =
          (EventLogManager) this.getClass().getField(EventLogManager.NODE_NAME).get(this);
      return eventLogManager.lastRecordAsString();
    } catch (Throwable e) {
      return "";
    }
  }
}

{% endhighlight %}

</div>
</div>

# Step 3 - Integrate event processor and connect event stream

The example [Main method]({{page.example_src}}/Main.java) instantiates the [BreachNotifierProcessor]({{page.example_src}}/generated/BreachNotifierProcessor.java), initialises it and submits events for
processing using the onEvent method. The init method must be called before submitting events. 

Events are submitted for processing by calling `eventProcessor.onEvent()` with instances of Event_A or Event_B.

The code for instantiating, initializing and sending events:

{% highlight java %}
public class Main {
    public static void main(String[] args) {
        var eventProcessor = new BreachNotifierProcessor();
        eventProcessor.init();
        eventProcessor.onEvent(new Event_A(34.4));
        eventProcessor.onEvent(new Event_B(52.1));
        eventProcessor.onEvent(new Event_A(105));//should create a breach warning
        eventProcessor.onEvent(new Event_A(12.4));
    }
}
{% endhighlight %}

## Example execution output

{% highlight console %}
sum:34.4
sum:86.5
sum:157.1
WARNING DataSumCalculator value is greater than 100 sum = 157.1
sum:64.5
{% endhighlight %}



<script>
document.getElementById("defaultOpen").click();
document.getElementById("defaultExample").click();
document.getElementById("aotBuilder").click();
</script>