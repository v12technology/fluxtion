<p align="center">
  <img width="270" height="200" src="images/Fluxtion_logo.png">
</p>

[![Build Status](https://travis-ci.org/v12technology/fluxtion.svg?branch=master)](https://travis-ci.org/v12technology/fluxtion)

## Overview
Thanks for dropping by, hope we can persuade you to donate your time to investigate Fluxtion further. 

Fluxtion is a code generator that automates the production of event stream processing logic. The generated code is self-contained and designed to sit within an application. The application delivers events to the Fluxtion generated Static Event Processor for stream processing.

Low latency, easy maintenance, zero gc, complex graph processing, simplified development and the "wow you can do that!!" reaction are the principles that guide our project. 

As a stretch goal we would like to be the [fastest single threaded java stream processor](https://github.com/v12technology/fluxtion-quickstart/blob/master/README.md#run) on the planet. 

## What are we solving
Fluxtion is focused on optimising the implementation of stream processing logic. Other stream processors support marshalling, distributed processing, event distribution, gui's and a multitude of other features. Fluxtion presumes there is an event queue that will feed it, and concentrates solely on delivering correct and optimal execution of application logic. 

Want to upgrade your application logic without rewriting your infrastructure? Fluxtion is the perfect solution for you.

### Capabilities
<details>
  <summary>Event processing support</summary>
  
*  
   * Batching or Streaming
   * Lifecycle – init, terminate, after event
   * Push and pull model
   * Configurable conditional branching
   * Handles complex graphs of thousands of nodes.
   * Event filtering
     * Event type
     * Event type and static annotation value
     * Event type and instance variable value
   * Parent change identification
   * Simple Integration of user functions
   * Stateful or stateless
</details>

<details>
  <summary>High performance</summary>
  
*   
   * Process hundreds of millions of events per second per core
   * Optimal pre-calculated execution path generation.
   * Zero gc
   * Cache optimised
   * JIT friendly code
   * Type inference, no auto-boxing primitive access.
</details>

<details>
  <summary>Developer Friendly</summary>
  
*  
   * Processing inference, no error prone separate graph description required.
   * Easy to use annotation based api for build-time.
   * Multi-language targets from one model, eg C++ processor from Java model.
   * Seamlessly integrate declarative and imperative processing in one processor.
   * Supports dependency injection.
  </details>

<details>
  <summary>Auditing</summary>
  
* 
   *  Auditors record event and node execution paths for post processing analysis.
   *  graphml and png are generated as well as code. 
   *  Audit records are in a structured machine friendly form. 
   *  Graphml and audit records loaded into the visualiser for analysis.
   *  Dynamic property tracing using reflection.
   *  Auditors can record performance and profile systems or individual nodes.
  </details>

<details>
  <summary>Plugins</summary>
  
*  
   * Text processing
   * Csv processing
   * Complex event processing joins, group by, aggregates, windows
   * Statistical functions
   * State machine
   * Functional support
  </details>

<details>
  <summary>Deployment</summary>
  
*   
   * Designed to be embedded
   * Use within any java process from j2me to servers.
    </details>

<details>
  <summary>Multiple model definitions</summary>
  
*  
   * Imperative
   * Declarative
   * Dependency injection via annotation
   * Data driven configuration via yml, xml or spring.xml
   * Bespoke strategies
  </details>

<details>
  <summary>Source code as an asset</summary>
  
*  
   * Variable naming strategy for human readable code
   * Audit friendly, prevents runtime dynamism.
   * Simplifies problem resolution, no hidden libraries.
   * Explicit generated code combats concryption – encryption by configuration.
  </details>

<details>
  <summary>Dynamic programming</summary>
  
*  
   * Generated parsers
   * Optimised functions generated conditioned upon variants.
  </details>

<details>
  <summary>Generative programming</summary>
  
*  
   * Function generation
   * Type inference, no autoboxing for primitives.
   * Handler generation from processing inference.
   * Core template customisation.
   * Zero gc logger statically generated.
  </details>

<details>
  <summary>Tool support</summary>
  
*  
   * Maven plugin
   * GraphML xml output
   * Visualiser/analyser
  </details>

## Example
The steps to integrate fluxtion static event processor(SEP) into a system using the imperative form:

![build process](images/Fluxtion_build.png)

### Step 1 
User writes classes representing incoming events and processing node containing bujsiness logics, annotations mark callback methods in the nodes. These classes will be used by your end application.

<details>
  <summary>Show me</summary>

This example demonstrates implementing a simple unix wc like utility with Fluxtion. The user creates a set of application classes that perform the actual processing, the application classes will be orchestrated by the generated SEP.


**[CharEvent:](https://github.com/v12technology/fluxtion-quickstart/blob/master/src/main/java/com/fluxtion/sample/wordcount/CharEvent.java)** Extends [Event](api/src/main/java/com/fluxtion/runtime/event/Event.java), the content of the CharEvent is the char value. An event is the entry point to a processing cycle in the SEP.

```java
public class CharEvent extends Event{
    
    public static final int ID = 1;
    
    public CharEvent(char id) {
        super(ID, id);
        filterId = id;
    }

    public char getCharacter() {
        return (char) filterId;
    }

    /**
     * Setting the character will also make the filterId update as well
     * @param character 
     */
    public void setCharacter(char character) {
        filterId = character;
    }

    @Override
    public String toString() {
        return "CharEvent{" + getCharacter() + '}';
    }
           
}
```

The optional filter value of the event is set to the value of the char. This is the event the application will create and feed into the generated SEP.


**[WordCounter:](https://github.com/v12technology/fluxtion-quickstart/blob/master/src/main/java/com/fluxtion/sample/wordcount/WordCounter.java)** receives CharEvents and maintains a set of stateful calculations for chars, words and lines. An instance of this class is created and referenced within the generated SEP, the SEP will handle all initialisation, lifecycle and event dispatch for managed nodes. 

```java
public class WordCounter {

    public transient int wordCount;
    public transient int charCount;
    public transient int lineCount;
    private int increment = 1;

    @EventHandler
    public void onAnyChar(CharEvent event) {
        charCount++;
    }

    @EventHandler(filterId = '\t')
    public void onTabDelimiter(CharEvent event) {
        increment = 1;
    }

    @EventHandler(filterId = ' ')
    public void onSpaceDelimiter(CharEvent event) {
        increment = 1;
    }

    @EventHandler(filterId = '\n')
    public void onEol(CharEvent event) {
        lineCount++;
        increment = 1;
    }

    @EventHandler(filterId = '\r')
    public void onCarriageReturn(CharEvent event) {
        //do nothing handle \r\n
    }

    @EventHandler(FilterType.unmatched)
    public void onUnmatchedChar(CharEvent event) {
        wordCount += increment;
        increment = 0;
    }
    ....
}
```

The ```@EventHandler``` annotation attached to a single argument method, marks the method as an entry point for processing. 

Some of the methods are marked with a filter value ```@EventHandler(filterId = '\t')``` signifying the  methods are only invoked when the Event and the filter value of the event match.
  
</details>

### Step 2 
Write a [SEPConfig](builder/src/main/java/com/fluxtion/api/node/SEPConfig.java) that binds instances of nodes together into an object graph, this class will be used by Fluxtion generator at compile time.

<details>
  <summary>Show me</summary>
  
The Builder class extends the base class SEPConfig and provides meta-data to the Fluxtion generator. 

```java
public static class Builder extends SEPConfig {

    @Override
    public void buildConfig() {
        addPublicNode(new WordCounter(), "result");
        maxFiltersInline = 15;
    }
}
```

In this case we are adding a single node with public scoped variable "result" with ```addPublicNode(new WordCounter(), "result");```. This file is used by Fluxtion at build time to generate the SEP.
  
</details>

### Step 3 
In your pom use the fluxtion maven plugin, specifying SEPConfig class, output package and class name. Inovke the fluxtion generator to generate a SEP as part of the build lifecycle.
<details>
  <summary>Show me</summary>

A maven plugin configuration in the [pom.xml](https://github.com/v12technology/fluxtion-quickstart/blob/master/pom.xml) invokes Fluxtion compiler with the correct parameters in the configuration section to drive the Fluxtion compiler. 

```
<build>
    <plugins>
        <plugin>
            <groupId>com.fluxtion</groupId>
            <artifactId>fluxtion-maven-plugin</artifactId>
            <version>${fluxtion.maven-plugin.ver}</version>
            <executions>
                <execution>
                    <id>wc-processor-gen</id>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <configClass>com.fluxtion.sample.wordcount.WordCounter$Builder</configClass>
                        <packageName>com.fluxtion.sample.wordcount.generated</packageName>
                        <className>WcProcessor</className>
                        <supportDirtyFiltering>false</supportDirtyFiltering>
                        <outputDirectory>src/main/java</outputDirectory>
                        <generateDescription>false</generateDescription>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Explanation of the configuration parameters:
*  configClass: The SEPConfig class Fluxtion compiler uses as source of meta-data at build time.
*  packageName: The output package for the generated SEP.
*  className: The simple class name for the generated SEP.
*  supportDirtyFiltering: controls guard support for conditional processing of sub nodes.
*  outputDirectory: Output directory for generated source used as compilation inputs.
*  generateDescription: controls generation of SEP descriptors, eg png. Single node SEP's have none, turn off.

When run as part of the build using:

```console
mvn install -P fluxtion
```

The SEP source file,[ WcProcessor.java](https://github.com/v12technology/fluxtion-quickstart/blob/master/src/main/java/com/fluxtion/sample/wordcount/generated/WcProcessor.java) will be generated by Fluxtion compiler:

```java
public class WcProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  public final WordCounter result = new WordCounter();
  //Dirty flags

  //Filter constants

  public WcProcessor() {}

  @Override
  public void onEvent(com.fluxtion.runtime.event.Event event) {
    switch (event.eventId()) {
      case (CharEvent.ID):
        {
          CharEvent typedEvent = (CharEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(CharEvent typedEvent) {
    switch (typedEvent.filterId()) {
        //Event Class:[com.fluxtion.sample.wordcount.CharEvent] filterId:[9]
      case (9):
        result.onTabDelimiter(typedEvent);
        result.onAnyChar(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.sample.wordcount.CharEvent] filterId:[10]
      case (10):
        result.onEol(typedEvent);
        result.onAnyChar(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.sample.wordcount.CharEvent] filterId:[13]
      case (13):
        result.onCarriageReturn(typedEvent);
        result.onAnyChar(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.sample.wordcount.CharEvent] filterId:[32]
      case (32):
        result.onSpaceDelimiter(typedEvent);
        result.onAnyChar(typedEvent);
        afterEvent();
        return;
    }
    //Default, no filter methods
    result.onAnyChar(typedEvent);
    result.onUnmatchedChar(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {}

  @Override
  public void init() {}

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
```

</details>

### Step 4
Use the generated SEP in your code/tests.
<details>
  <summary>Show me</summary>

The SEP is the same as using any java source file in your, just code as normal. The generated SEP implements the interface [EventHandler](https://github.com/v12technology/fluxtion/blob/master/api/src/main/java/com/fluxtion/runtime/lifecycle/EventHandler.java). The application instantiates the SEP (WcProcessor) and sends events for processing by invoking ```EventHandler.onEvent(Event e)``` with a new event. 

```java
public class Main {

    public static final int SIZE = 4 * 1024;

    public static void main(String[] args) {
        File f = new File(args[0]);
        try {
            streamFromFile(f);
        } catch (IOException ex) {
            System.out.println("error processing file:" + ex.getMessage());
        }
    }

    public static WcProcessor streamFromFile(File file) throws FileNotFoundException, IOException {
        long now = System.nanoTime();
        WcProcessor processor = new WcProcessor();
        processor.init();
        if (file.exists() && file.isFile()) {
            FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
            long size = file.length();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, size);
            CharEvent charEvent = new CharEvent(' ');

            final byte[] barray = new byte[SIZE];
            int nGet;
            while (buffer.hasRemaining()) {
                nGet = Math.min(buffer.remaining(), SIZE);
                buffer.get(barray, 0, nGet);
                for (int i = 0; i < nGet; i++) {
                    charEvent.setCharacter((char) barray[i]);
                    processor.handleEvent(charEvent);
                }
            }
            processor.tearDown();
            double delta = ((int)(System.nanoTime() - now)/1_000_000)/1_000.0;
            System.out.println(processor.result.toString());
            System.out.printf("time: %.3f sec %n", delta);
        } else {
            System.out.println("cannot process file file:" + file.getAbsolutePath());
        }
        return processor;
    }
}
```
  
Most of the code handles streaming data from a file and wrapping each byte as a CharEvent. The key integration points between app and generated SEP are shown below. 


The creation and intialisation of the SEP (WcProcessor)
```java
        WcProcessor processor = new WcProcessor();
        processor.init();
```        
  
Pushing data to the SEP for each byte in the file

```java
        charEvent.setCharacter((char) barray[i]);
        processor.handleEvent(charEvent);
```

Pulling results from the SEP. Pull functionality is available as we declared the WcProcessors as a public node in the builder.
```java
        processor.tearDown();
        ...
        System.out.println(processor.result.toString());
```
Execute the jar that holds the application classes, both user and Fluxtion generated.
```bat
c:\tmp\fluxtion-quickstart>java -jar dist\wc.jar dist\sample\norvig.txt
 48,698,162 chars
  7,439,040 words
  1,549,801 lines

time: 0.098 sec
```
</details>


## Graph processing primer

In a stream processor events are received and processed with predictable results. A set of dependent behaviours can be modelled as a directed acyclic graph. Each behaviour is a node on the graph and in our case these behaviours are functions. For predictable processing to hold true we can say the following:

*  The first node on an execution path is an event handler.
*  An execution path is formed of nodes that have the first event handler as a parent.
*  Functions are nodes on the execution path.
*  An event handler is a node that accepts an incoming event for processing.
*  Functions will always be invoked in execution path order.
*  Execution path order is topologically sorted such that all parent nodes are invoked before child nodes.
*  A child node will be given a valid reference to each parent dependency before any event processing occurs.


![example graph](images/Execution_graph_paths.png)

For the example above:
*  **Event graph:** Node 1, Node 2, Node 3, Node 4, Node 10, Node 11
*  **Event handlers:** Node 1, Node 10
*  **Execution Paths:**
   * Event A: Node 1, Node 2, Node 3 Node 4, Node 11
   * Event B: Node 10, Node 11


## Philosophy
Every decision we make about Fluxtion is driven by increasing efficiency, we want to reduce cost. Where we differ from other philosophies is the inputs that make up our analysis. Just reducing cpu cycles is not our end goal, we want to be broad in our ambition.

On a personal level solving a coding problem may bring a feeling of self satisifaction. But the hundreds of billions of dollars of investment in IT are made because they make a real reduction on the bottom line. Information technology is an efficiency play. We dont have the space for a full discussion here, but rather list some of the non-obvious sources of cost we would like to address:

### Component re-use
Component re-use is proferred as a goal because it reduces the lines of custom code to write and therefore saves money. There are hidden costs in using someone else's framework; integration costs, learning costs, understanding unexpected behaviour and supporting someone else's product in your system. Generating code means more lines, but only some are manually typed. The generated solution can now fit our problem more exactly, is easier to understand, debug and support. 

### If
Reasoning about code that has multiple if statements is not only hard but it is also expensive to compute due to pipelining failures on the cpu. State-machine code is simple to understand and has minimal conditional branching, but is expensive to construct. Having a system that can cheaply produce statemachine-like transitions for application code would be beneficial. Minimising user written condtional branching, reduces bugs and saving computational resource.  

### Concryption
Some systems are completely assembled by configuration that can be spread over several documents. The configuration can become a language in itself, but with almost no type checking. Worse it may become both order and state dependent, evolving into a dialect that only one or two developers can support for your system. We call this effect concryption - encryption by configuration. Components are sometimes so decoupled we have no hope of understanding the linkages. This creates inertia in development slowing delivery, deployment and fault finding. Sometimes it would be just cheaper and easier to specify the component configuration in code.

### Fear of starting again
Once we build a set of complex of application logic we can become highly resistant to pulling it down and building it again. The construction cost may be high, logic may be spread over multiple components. We then start making compromised decisions that eventaully lead to fragile systems. We should be happy to destroy a solution and build it again if the assembly cost is cheap enough.

### Staying small
As developers we have a wonderful eye for detail, usually our unit tests are great. But when it comes to the large the complexity overwhelms us, integration tests are usually much less effective. When we create larger logical processing units they are less reliable. It would be better to leave the correct combination of smaller logical units to an automated process at the macro-level and allow developers to create the intricate logic at the micro-level.

## Maintenance tools 

The visualiser tool can load any graphml file created by Fluxtion for inspection. 
![Visualiser image](images/visualiser_1.png)


## Contributing
We welcome contributions to the project. Detailed information on our ways of working will be written in time. In brief our goals are:

* Sign the [Fluxtion Contributor Licence Agreement](https://github.com/v12technology/fluxtion/blob/master/contributorLicenseAgreement);
* Push your changes to a fork;
* Submit a pull request.


## License
Fluxtion is licensed under the [Server Side Public License](https://www.mongodb.com/licensing/server-side-public-license). This license is created by MongoDb, for further info see [FAQ](https://www.mongodb.com/licensing/server-side-public-license/faq) and comparison with [AGPL v3.0](https://www.mongodb.com/licensing/server-side-public-license/faq).


**This README is a work in progress and will be updating regularly**
