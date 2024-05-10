---
title: Processor generation
parent: Build event processor
grand_parent: Reference documentation
has_children: false
nav_order: 2
published: true

aot_advantage:
  "Fast execution<br/>
  zero gc<br/>
  Supports 100's nodes<br/>
  Single runtime library<br/>
  easy to debug"

compile_advantage:
  "Fast execution<br/>
  zero gc<br/>
  Supports 100's nodes<br/>
  Compile new graphs at runtime<br/>
  easy to debug
  "

interpreted_disadvantage:
  "Slower execution<br/>
  produces garbage<br/>
  More runtime libraries<br/>
  Harder to debug with map based dispatch"

compile_disadvantage:
  "More runtime libraries<br/>
  Limit on generated code size<br/>
  inline lambda not supported<br/>"

aot_disadvantage:
  "Limit on generated code size<br/>
  inline lambda not supported<br/>
  "

compiler_libs:
  "Fluxtion - compiler<br/>
  Fluxtion - runtime<br/>
  Third party libs"
---

# Generating an event processor
{: .no_toc }
---

The Fluxtion compiler library provides several tools for generating an event processor from a model supplied by the
client.
This section documents the utilities and tools available for event processor generation and how to use them.
The binding of nodes to the event processor is covered in [binding user classes](binding_user_classes)

The [Fluxtion]({{site.Fluxtion_link}}) class gives static access to the generation methods.

The source project for the examples can be
found [here]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation)

![TEST](../../images/integration_overview-generating.png)

## Generating modes
{: .no_toc }

To complete building an event processor the code model and a generation mode is passed to the compiler. The final event
processor binds in all user classes combined with pre-calculated event dispatch to meet the dispatch rules.

Supported generation modes

- **Interpreted** In memory execution of an event processor
- **In memory compilation** In memory execution of compiled event processor
- **Ahead of time compilation** Ahead of time generation of compiled event processor, execute in another process

Regardless of which generation mode is used the generated event processor will behave the same with respect to the
dispatch rules.

## Generation modes comparison
{: .no_toc }

| Mode                      | Runtime libraries<br/>Required | Advantage                                  | Disadvantages                     | Use case |
|---------------------------|--------------------------------|--------------------------------------------|-----------------------------------|----------|
| Interpreted               | {{page.compiler_libs}}         | Supports 1000's nodes<br/>Quick to develop | {{page.interpreted_disadvantage}} |          |
| In memory compilation     | {{page.compiler_libs}}         | {{page.compile_advantage}}                 | {{page.compile_disadvantage}}     |          |
| Ahead of time compilation | Fluxtion - runtime             | {{page.aot_advantage}}                     | {{page.aot_disadvantage}}         |          |

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

# Interpreted overview

The interpreted mode implements the event processor with a map based dispatch. Generation and execution all happen in
the
same process so both the runtime and compiler Fluxtion libraries are required at runtime.

[Fluxtion.interpret]({{site.Fluxtion_link}}) methods are the entry point to generating an interpreted version of the
event processor.

A user can either supply a varargs list of objects to include in the final event processor, or configure a
supplied [EventProcessorConfig]({{site.EventProcessorConfig_link}})
and imperatively add nodes. See the interpreted section of [binding user classes](binding_user_classes) document for
a description of binding functions into an event processor.

# Interpreted code sample

Generating an in-memory event processor is achieved with a call to one of the
overloaded [Fluxtion.interpret]({{site.Fluxtion_link}}) methods.
Imperative and DSL models are both generated through the same call to interpret.

## Imperative generation example

Interpret can be called with list of nodes that are implicitly added to the event processor or the use the version that
accepts an EventProcessorConfig consumer for user code to imperatively add nodes.
{% highlight java %}
EventProcessor<?> processor;

//these are equivalent processors
//add a list of nodes using short cut method
processor = Fluxtion.interpret(new MyNode("node A"));
//add nodes using supplied EventProcessorConfig
processor = Fluxtion.interpret(cfg -> {
    cfg.addNode(new MyNode("node A"));
});

//these are equivalent processors
processor = Fluxtion.interpret(new MyNode("node A"), new MyNode("node B"));
processor = Fluxtion.interpret(cfg -> {
    cfg.addNode(new MyNode("node A"));
    cfg.addNode(new MyNode("node B"));
});

//executing event processor
processor.init();
processor.onEvent("hello world");
{% endhighlight %}

## Functional DSL generation example

To generate an interpreted version using Fluxtion DSL, the DataFlow calls must happen within the context of interpret
version that accepts an EventProcessorConfig
consumer `Fluxtion.interpret(Consumer<EventProcessorConfig> configProcessor)`

{% highlight java %}
//DSL calls must use the Supplier<>
Fluxtion.interpret(cfg -> {
    DataFlow.subscribe(String.class)
            .push(new MyNode("node A")::handleStringEvent);        
});
{% endhighlight %}

# Compiling overview

The compiling mode generates a source file that represents the event processor, ready for compilation and use within an
application. The source generation and compilation process can happen either in process or as part of the build stage.
If
the source generation happens as part of the build process the event processor is classed as ahead of time (AOT).

The generate source file is a serialised state of the event processor and all the instances it manages. This places
stricter
requirements on the nodes bound to the event processor than running in interpreted mode. Bound user classes are declared
as fields
in the generated event processor. The fields of a bound class will be serialized as well.

## FluxtionCompilerConfig

There are several compile and compileAot methods in the [Fluxtion]({{site.Fluxtion_link}}), most of these are short cuts
that delegate to a single compile method:

`EventProcessor compile(Consumer<EventProcessorConfig> sepConfig, Consumer<FluxtionCompilerConfig> cfgBuilder)`

We have previosuly seen how EventProcessorConfig controls what elements are bound into the event processor. To configure
the generated source and compiler user code calls methods on the
supplied [FluxtionCompilerConfig]({{site.FluxtionCompilerConfig_link}}).

### Configurable properties for compilation

The FluxtionCompilerConfig instance allows user code to configure the compilation process via these properties

* **Compilation control**
    * interpreted - generate an interpreted version of the event processor
    * compileSource - flag to enable compilation of any generated source files
    * ClassLoader - override classLoader at generation time

* **Source control**
    * templateSep - The velocity template file to use in the source generation process
    * className - class name for generated event processor
    * packageName - package name for generated event processor
    * addBuildTime - flag to add build time to the generated source files
    * formatSource - flag enable formatting of the generated source files

* **Output control**
    * outputDirectory - Output directory for generated event processor source files
    * buildOutputDirectory - Output directory where compiled artifacts should be written
    * resourcesOutputDirectory - Output directory for any resources generated with the event processor
    * writeSourceToFile - Flag controlling if the generated source is written to output or is transient memory only
      version
    * sourceWriter - if writeSourceToFile is false this writer will capture the content of the generation process
    * generateDescription - Flag controlling generation of meta data description resources

# Compiling in process

Compiling in process is a very similar process to generating an interpreted event processor, just replace the calls to
`Fluxtion.interpret` with `Fluxtion.compile`. The source code will be generated and compiled in memory, no configuration
of
FluxtionCompilerConfig is required

## Imperative generation example

Compile can be called with list of nodes that are implicitly added to the event processor or the use the version that
accepts an EventProcessorConfig consumer for user code to imperatively add nodes.
{% highlight java %}
EventProcessor<?> processor;

//these are equivalent processors
//add a list of nodes using short cut method
processor = Fluxtion.compile(new MyNode("node A"));
//add nodes using supplied EventProcessorConfig
processor = Fluxtion.compile(cfg -> {
    cfg.addNode(new MyNode("node A"));
});

//these are equivalent processors
processor = Fluxtion.compile(new MyNode("node A"), new MyNode("node B"));
processor = Fluxtion.compile(cfg -> {
    cfg.addNode(new MyNode("node A"));
    cfg.addNode(new MyNode("node B"));
});

//executing event processor
processor.init();
processor.onEvent("hello world");
{% endhighlight %}

## Functional DSL generation example

To generate an in process compiled version using Fluxtion DSL, the DataFlow calls must happen within the context of
interpret
version that accepts an EventProcessorConfig consumer `Fluxtion.compile(Consumer<EventProcessorConfig> configProcessor)`

{% highlight java %}

//DSL calls must use the Supplier<>
Fluxtion.compile(cfg -> {
    DataFlow.subscribe(String.class)
            .push(new MyNode("node A")::handleStringEvent);        
});

{% endhighlight %}

# Compiling AOT - programmatically

Compiling an event processor AOT is a very similar process to compiling in process, we use the method

`EventProcessor.compile(Consumer<EventProcessorConfig> sepConfig, Consumer<FluxtionCompilerConfig> cfgBuilder)`

How and where the source code will be generated is configured through the supplied FluxtionCompilerConfig instance.
`EventProcessor.compile`can be called in a separate process to generate the event processor AOT. The generated source
file can be used in the project as a normal class.

A maven plugin is provided as part of the Fluxtion toolset that integrates AOT building into a standard part of your
build.

## Imperative generation

Compile can be called with list of nodes that are implicitly added to the event processor or the use the version that
accepts an EventProcessorConfig consumer for user code to imperatively add nodes.
{% highlight java %}

public static void main(String[] args) {
    Fluxtion.compile(
            //binds classes to event processor
            eventProcessorConfig -> eventProcessorConfig.addNode(new MyNode("node A")),
            //controls the generation
            fluxtionCompilerConfig -> {
                fluxtionCompilerConfig.setClassName("MyEventProcessor");
                fluxtionCompilerConfig.setPackageName("com.fluxtion.example.aot.generated");
            });
}
{% endhighlight %}

The output from the generation process is a source file MyEventProcessor.java in package
com.fluxtion.example.aot.generated
written to directory com/fluxtion/example/aot/generated below the current working directory of main method. A message
to console is printed to confirm the generation process output

{% highlight console %}
21-Apr-24 07:39:25 [main] INFO EventProcessorCompilation - generated EventProcessor file: /fluxtion-examples/src/main/java/com/fluxtion/example/aot/generated/MyEventProcessor.java
{% endhighlight %}

## Using an AOT event processor

Once generated AOT use the event processor like any normal java class in application code.

{% highlight java %}

public static void main(String[] args) {
    var processor = new com.fluxtion.example.aot.generated.MyEventProcessor();
    processor.init();
    processor.onEvent("hello world");
}
{% endhighlight %}

# Rules for serializing bound classes

- Bound class must have a public constructor or constructors, either:
    - A zero argument constructor
    - For complex construction add a custom serializer
      with [`EventProcessorConfig.addClassSerialize`]({{site.EventProcessorConfig_link}})
- Final non-transient fields must be assigned in the constructor
- A constructor must exist that matches the final non-transient fields as arguments
- Transient fields are not serialised
- Rules for serializing fields of bound classes
    - Only non-transient fields are serialized
    - All standard types are supported
    - Java bean properties are serialized using setter
    - Public fields are serialized

## Serialize fields example

This example demonstrates field serialisation for standard types and passing a named constructor argument. Because there
are 2 String fields the generated constructor needs a hint to which field is assigned in the constructor:

```java
@AssignToField("cId")
private final String cId;
```

To ensure the consructor argument is taken from the cId field when generating the event processor source file.

The transient field `transientName` is ignored during the source generation process.

{% highlight java %}

public class FieldsExample {

    public enum SampleEnum {VAL1, VAL2, VALX}

    public static void main(String[] args) {
        Fluxtion.compileAot("com.fluxtion.example.reference.generation.genoutput", "FieldsExampleProcessor",
                BasicTypeHolder.builder()
                        .cId("cid")
                        .name("holder")
                        .myChar('$')
                        .longVal(2334L)
                        .intVal(12)
                        .shortVal((short) 45)
                        .byteVal((byte) 12)
                        .doubleVal(35.8)
                        .doubleVal2(Double.NaN)
                        .floatVal(898.24f)
                        .boolean1Val(true)
                        .boolean2Val(false)
                        .classVal(String.class)
                        .enumVal(SampleEnum.VAL2)
                        .build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class BasicTypeHolder implements NamedNode {
        private String name;
        @AssignToField("cId")
        private final String cId;
        private char myChar;
        private long longVal;
        private int intVal;
        private short shortVal;
        private byte byteVal;
        private double doubleVal;
        private double doubleVal2;
        private float floatVal;
        private boolean boolean1Val;
        private boolean boolean2Val;
        private Class<?> classVal;
        private SampleEnum enumVal;
    }
}

{% endhighlight %}

## Serialised event processor
{: .no_toc }

The full event processor
is [FieldsExampleProcessor]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation/genoutput/FieldsExampleProcessor.java)
this excerpt shows the relevant statements:

{% highlight java %}

public class FieldsExampleProcessor
    implements EventProcessor<FieldsExampleProcessor>,
    StaticEventProcessor,
    InternalEventProcessor,
    BatchHandler,
    Lifecycle {

    //REMOVED FOR CLARITY
    private final BasicTypeHolder holder = new BasicTypeHolder("cid");
    //REMOVED FOR CLARITY
    
    public FieldsExampleProcessor(Map<Object, Object> contextMap) {
        context.replaceMappings(contextMap);
        holder.setBoolean1Val(true);
        holder.setBoolean2Val(false);
        holder.setByteVal((byte) 12);
        holder.setClassVal(java.lang.String.class);
        holder.setDoubleVal(35.8);
        holder.setDoubleVal2(Double.NaN);
        holder.setEnumVal(SampleEnum.VAL2);
        holder.setFloatVal(898.24f);
        holder.setIntVal(12);
        holder.setLongVal(2334L);
        holder.setMyChar('$');
        holder.setName("holder");
        holder.setShortVal((short) 45);
        //node auditors
        initialiseAuditor(clock);
        initialiseAuditor(nodeNameLookup);
        subscriptionManager.setSubscribingEventProcessor(this);
        context.setEventProcessorCallback(this);
    }
    
    //REMOVED FOR CLARITY
}


{% endhighlight %}

## Custom field serialization

A custom serializer can be injected for fields that cannot be serialized with the standard algorithm. Add a custom
serializer to the EventProcesserConfig instance with

`EventProcesserConfig.addClassSerializer(MyThing.class, CustomSerializerExample::customSerialiser);`

The custom serializer function generates a String that can be compiled in the event processor generated source

{% highlight java %}

public class CustomSerializerExample {

    public static void main(String[] args) {
        Fluxtion.compile(
                cfg -> {
                    //static method is not serializable
                    var myThing = MyThing.newThing("my instance param");
                    cfg.addNode(new StringHandler(myThing));

                    //register custom serializer for MyThing
                    cfg.addClassSerializer(MyThing.class, CustomSerializerExample::customSerialiser);
                },
                compilerConfig -> {
                    compilerConfig.setClassName("CustomSerializerExampleProcessor");
                    compilerConfig.setPackageName("com.fluxtion.example.reference.generation.genoutput");
                });
    }

    //Implements the custom serialiser function
    public static String customSerialiser(FieldContext<MyThing> fieldContext) {
        fieldContext.getImportList().add(MyThing.class);
        MyThing myThing = fieldContext.getInstanceToMap();
        return "MyThing.newThing(\"" + myThing.getID() + "\")";
    }

    public static class MyThing {
        private final String iD;

        private MyThing(String iD) {
            this.iD = iD;
        }

        public String getID() {
            return iD;
        }

        public static MyThing newThing(String in) {
            return new MyThing(in);
        }
    }

    public static class StringHandler {
        private final Object delegate;

        public StringHandler(Object delegate) {
            this.delegate = delegate;
        }

        @OnEventHandler
        public boolean onEvent(MyThing event) {
            return false;
        }
    }
}

{% endhighlight %}

## Serialised event processor
{: .no_toc }

The generated event processor
is [CustomSerializerExampleProcessor]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation/genoutput/CustomSerializerExampleProcessor.java)
this excerpt shows the relevant statements that invokes the static build method `MyThing.newThing("my instance param")`

{% highlight java %}

public class CustomSerializerExampleProcessor
    implements EventProcessor<CustomSerializerExampleProcessor>,
    StaticEventProcessor,
    InternalEventProcessor,
    BatchHandler,
    Lifecycle {
    
    //Node declarations
    //REMOVED FOR CLARITY
    private final StringHandler stringHandler_0 = new StringHandler(MyThing.newThing("my instance param"));
    //REMOVED FOR CLARITY

}

{% endhighlight %}

# Compile AOT - spring configuration

Spring configuration is natively supported by Fluxtion. Any beans in the spring ApplicationContext will be bound into
the model and eventually the generated event processor. Pass the spring ApplicationContext into fluxtion and compile AOT
with

`FluxtionSpring.compileAot(ApplicationContext appContext, Consumer<FluxtionCompilerConfig> fluxtionCompilerConfig)`

The generated event processor
is [CustomSerializerExampleProcessor]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation/genoutput/SpringExampleProcessor.java)

See [FluxtionSpring]({{site.fluxtion_src_compiler}}/extern/spring/FluxtionSpring.java) for the various compilation
methods
supported for Spring integration.

{% highlight java %}

public class SpringConfigAdd {

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
        FluxtionSpring.compileAot(
                new ClassPathXmlApplicationContext("com/fluxtion/example/reference/spring-example.xml"),
                fluxtionCompilerConfig -> {
                    fluxtionCompilerConfig.setClassName("SpringExampleProcessor");
                    fluxtionCompilerConfig.setPackageName("com.fluxtion.example.reference.generation.genoutput");
                });
    }
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

# Compile AOT - scan for FluxtionGraphBuilder

Fluxtion provides a mechanism for discovering event processor builder classes and invoking each one to generate
an event processor AOT.

**Requirements to create a discoverable builder**

* Implement the interface [FluxtionGraphBuilder]({{site.fluxtion_src_compiler}}/FluxtionGraphBuilder.java). The two call
  back methods separate graph building and compiler configuration
* Create a main method that
  calls [`Fluxtion.scanAndCompileFluxtionBuilders(File... builderLocations)`]({{site.Fluxtion_link}}#L265) where file is
  the location
  of the builder classes.
* Any Builder annotated with [`@Disabled`]({{site.fluxtion_src_runtime}}/annotations/builder/Disabled.java) is ignored
  by the scan function

The generated event processor
is [SampleAotBuilderProcessor]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation/genoutput/SampleAotBuilderProcessor.java)

{% highlight java %}

//This is a discoverable FluxtionGraphBuilder
public class SampleAotBuilder implements FluxtionGraphBuilder {
    @Override
    public void buildGraph(EventProcessorConfig eventProcessorConfig) {
        DataFlow.subscribe(String.class)
          .mapToInt(String::length);
    }

    @Override
    public void configureGeneration(FluxtionCompilerConfig compilerConfig) {
        compilerConfig.setClassName("SampleAotBuilderProcessor");
        compilerConfig.setPackageName("com.fluxtion.example.reference.generation.genoutput");
    }

    //invokes the scanAndCompileFluxtionBuilders utility
    public static void main(String[] args) {
        Fluxtion.scanAndCompileFluxtionBuilders(Path.of("target/classes/").toFile());
    }
}

{% endhighlight %}

## Build tool scan integration

The mechanism for scan and compile is encapsulated in the maven plugin removing the need to provide the main method.
Presently there is no gradle plugin, simple wrapping the discovery function in a main method in the gradle build file
works.

# To be documented

- Yaml support
