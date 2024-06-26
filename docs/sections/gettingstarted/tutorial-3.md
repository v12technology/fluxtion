---
title: 3rd tutorial - AOT no spring
parent: Developer tutorials
has_children: false
nav_order: 4
published: true
---

# 3rd tutorial - AOT no spring
{: .no_toc }
---

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

# Introduction
---

This tutorial is an introduction to building Fluxtion dependency injection container ahead of time without Spring. The 
reader should be proficient in Java, maven, git and have completed the [second lottery tutorial](tutorial-2.md) before 
starting this tutorial.

Our goal is to create the lottery processing logic ahead of time as in tutorial 4, but with no Spring dependencies.

At the end of this tutorial you should understand:

- How to add user classes to an event processor programmatically using a FluxtionGraphBuilder
- How to generate a container ahead of time using the Fluxtion maven plugin
- Understand that spring is a build time configuration provider only

# Example project
The [example project]({{site.getting_started}}//tutorial6-lottery-nospring) is referenced in this tutorial.

# Building AOT with FluxtionGraphBuilder
To build an event processor container we need to specify the managed instances to the Fluxtion compiler. In previous 
examples we used the Spring config to supply that information, now we will specify the instances programmatically. Once
the components are specified the maven plugin will generate the event processor file as part of the build. Steps for a 
non-spring aot generation:
- Implement FluxtionGraphBuilder adding user classes programmatically
- Remove all the spring dependencies from the project, both libraries and config
- Add the fluxtion maven plugin to the build

## Add user classes with FluxtionGraphBuilder
[LotteryEventProcessorBuilder]({{site.getting_started}}/tutorial6-lottery-nospring/src/main/java/com/fluxtion/example/cookbook/lottery/builder/LotteryEventProcessorBuilder.java)
adds user classes to the programmatically in the buildGraph method using the supplied [EventProcessorConfig]({{site.fluxtion_src_compiler}}/EventProcessorConfig.java).

EventProcessorConfig gives configuration access to the event processor before the compilation/generation phase. User 
classes are added with `eventProcessorConfig.addNode(lotteryMachine, "lotteryMachine")`, this adds the lotteryMachine 
instance to the processor and sets the variable name in the generated file to lotteryMachine.

We are customising the variable names in the config so the generated file exactly matches the spring aot example.
A single add with `eventProcessorConfig.addNode(lotteryMachine)` would be sufficient, as the ticketStore instance would
be included through discovery and variable names are generated by the compiler. Functionally the two are the same but the 
variable names in the generated event processor would not match the previous tutorial.

Source file class and package name are configured in the configureGeneration method, via the supplied [FluxtionCompilerConfig]({{site.fluxtion_src_compiler}}/FluxtionCompilerConfig.java).


{% highlight java %}
public class LotteryEventProcessorBuilder implements FluxtionGraphBuilder {

    @Override
    public void buildGraph(EventProcessorConfig eventProcessorConfig) {
        TicketStoreNode ticketStore = new TicketStoreNode();
        LotteryMachineNode lotteryMachine = new LotteryMachineNode(ticketStore);
        eventProcessorConfig.addNode(lotteryMachine, "lotteryMachine");
        //no need to do this as fluxtion will automatically add the ticketStore instance by inspection of lotteryMachine
        //but nice to make the names the same as the spring file, can prove the generated files are identical
        eventProcessorConfig.addNode(ticketStore, "ticketStore");
        eventProcessorConfig.addEventAudit(EventLogControlEvent.LogLevel.DEBUG);
    }

    @Override
    public void configureGeneration(FluxtionCompilerConfig fluxtionCompilerConfig) {
        fluxtionCompilerConfig.setClassName("LotteryProcessor");
        fluxtionCompilerConfig.setPackageName("com.fluxtion.example.cookbook.lottery.aot");
    }
}
{% endhighlight %}

## Updating the build
The [pom.xml]({{site.getting_started}}/tutorial6-lottery-nospring/pom.xml) is updated to remove all 
references to spring and the fluxtion maven plugin scan goal is used to discover and process FluxtionGraphBuilder's 
in the project. Additionally, the spring-lottery.xml config file is deleted as it is no longer required.

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.fluxtion.example</groupId>
<artifactId>getting-started-tutorial6</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>jar</packaging>
<name>getting-started :: tutorial 6 :: no spring</name>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <fluxtion.version>{{site.fluxtion_version}}</fluxtion.version>
    </properties>

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
        <!--        RUNTIME SCOPE NO LONGER SUPPLIED BY FLUXTION COMPILER-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>${fluxtion.version}</version>
        </dependency>
        <!--        PROVIDED SCOPE-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>${fluxtion.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
{% endhighlight %}

## Build output
Running the build generates the
[LotteryProcessor.java]({{site.getting_started}}/tutorial6-lottery-nospring/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java)
source file which is exactly the same as the 
[generated version]({{site.getting_started}}/tutorial4-lottery-auditlog/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java)
in tutorial 4.

The maven plugin will print to console the FluxtionGraphBuilder instances it has discovered and is processing.

{% highlight console %}
[INFO] 
[INFO] --- fluxtion:3.0.14:scan (default) @ getting-started-tutorial6 ---
1: invoking builder com.fluxtion.example.cookbook.lottery.builder.LotteryEventProcessorBuilder
[main] INFO com.fluxtion.compiler.generation.compiler.EventProcessorCompilation - generated EventProcessor file: /development/fluxtion-examples/getting-started/tutorial6-lottery-nospring/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.137 s
[INFO] Finished at: 2024-03-01T09:19:23Z
[INFO] ------------------------------------------------------------------------
{% endhighlight %}

# Conclusion
In this tutorial we have seen how user classes can be added to the event processor programmatically for ahead of time 
compilation. The generated event processor is exactly the same as the spring configured version

- Programmatic access gives the programmer full control of what classes are in the event processor
- Spring config is a configuration layer that is not used at runtime
- Programmatic and spring configurations are completely interchangeable

I hope you have enjoyed reading this tutorial, and it has given you a desire to adding event auditing to your applications
. Please send me any comments or suggestions to improve this tutorial

[next tutorial 4](tutorial-4)
{: .text-right }