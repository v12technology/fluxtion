---
title: 2nd tutorial - AOT
parent: Getting started
has_children: false
nav_order: 2
published: true
---

<details markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

# Introduction

This tutorial is an introduction to building Fluxtion dependency injection container ahead of time. The reader should
be proficient in Java, maven, git, Spring and have completed the [first lottery tutorial](tutorial-1.md) before starting
this tutorial. The project source can be found [here.]({{site.getting_started}}/tutorial2-lottery-aot)

Our goal is to create the lottery processing logic ahead of time so the application has no change in functional
behaviour compared to the first tutorial. An aot application has the advantage of starting quicker, using less resources
and requiring fewer dependencies at runtime.

At the end of this tutorial you should understand:

- The relationship between Fluxtion runtime and compiler components
- How to generate a container ahead of time
- How the serialised container source file is used in an application

# Building AOT

Fluxtion has two components, the runtime and the compiler, that in combination create a dependency injection container.
In the first tutorial both components were used at runtime building a container that executes in **interpreted mode**. In
this
tutorial we invoke the compiler in the build phase and generate the container ahead of time. This is known as running
in **aot mode**.

The compiler serialises the configured dependency container to a standard java source file when in aot mode. The steps
for creating a container ahead of time:

- Create a function that uses the Fluxtion aot generation api's and call it during the build phase
- Add the generated java file to the application source tree
- Use the generated class as a concrete type in tests and within the application
- The application's **compile** dependencies must include the Fluxtion runtime and the Fluxtion compiler
- The application's **runtime** dependencies must include the Fluxtion runtime and can exclude the Fluxtion compiler

The function to build the container uses spring configuration file and the Fluxtion api to compile aot. The first
argument is the location of the spring config file and the second argument gives access to  [FluxtionCompilerConfig]({{site.fluxtion_src_compiler}}/FluxtionCompilerConfig.java) which
allows for configuration of the compiler output. We configure the class name and package of the generated source file.

{% highlight java %}
public class BuildAot {
    public static void main(String[] args) {
        FluxtionSpring.compileAot(
            new ClassPathXmlApplicationContext("/spring-lottery.xml"),
            c -> {
                c.setPackageName("com.fluxtion.example.cookbook.lottery.aot");
                c.setClassName("LotteryProcessor");
                //required because maven does not pass the classpath properly
                c.setCompileSource(false);
            }
        );
    }
}
{% endhighlight %}

By default, the Fluxtion compiler generates the source file in the standard maven location src/main/java but can be 
configured by the developer using the supplied [FluxtionCompilerConfig]({{site.fluxtion_src_compiler}}/FluxtionCompilerConfig.java) instance. 
The serialised source file [LotteryProcessor is here.]({{site.getting_started}}/tutorial2-lottery-aot/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java)

**FluxtionCompilerConfig is the configuration object for aot generated outputs passed to the compiler.**

BuildAot main can be run from maven via a build plugin:

{% highlight console %}
greg@Gregs-iMac tutorial2-lottery-aot % mvn compile exec:java
[INFO] Scanning for projects...
[INFO]
[INFO] -----------< com.fluxtion.example:getting-started-tutorial2 >-----------
[INFO] Building getting-started :: tutorial 2 :: lottery aot 1.0.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
...
...
[INFO] --- exec-maven-plugin:3.1.0:java (default-cli) @ getting-started-tutorial2 ---
23-Sept-23 18:49:19 [com.fluxtion.example.cookbook.lottery.BuildAot.main()] INFO EventProcessorCompilation - 
generated EventProcessor file: /x/tutorial2-lottery-aot/src/main/java/com/fluxtion/example/cookbook/lottery/aot/LotteryProcessor.java
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
{% endhighlight %}

## Build system
The example use maven to build the application but as we are generating the container aot we only need the compiler 
dependency at build time. We could leave the pom file dependencies unchanged from tutorial 1 but having less runtime 
dependencies will make for easier integration in the future. The following changes are made to the pom file dependencies

| Nmae              | Purpose                    | scope    | available at runtime |
|-------------------|----------------------------|----------|----------------------|
| fluxtion-runtime  | libraries for di container | compile  | YES                  |
| Slf4j             | runtime logging            | compile  | YES                  |
| fluxtion-compiler | generating di container    | provided | NO                   |
| Spring            | spring config parsing      | provided | NO                   |
| lombok            | source annotations         | provided | NO                   |


Fluxtion compiler is build time only so we must now explicitly add Fluxtion runtime and slf4j dependencies to the runtime 
classpath. Spring-context enables reading the spring config file for the Fluxtion compiler, as this is now an aot 
operation we can move spring to the provided scope as well.

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.fluxtion.example</groupId>
<artifactId>getting-started-tutorial2</artifactId>
<version>1.0.0-SNAPSHOT</version>
<packaging>jar</packaging>
<name>getting-started :: tutorial 2 :: lottery aot</name>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <fluxtion.version>9.1.9</fluxtion.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>com.fluxtion.example.cookbook.lottery.BuildAot</mainClass>
                    <classpathScope>compile</classpathScope>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencies>
<!--        PROVIDED SCOPE - BUILD TIME ONLY-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>${fluxtion.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.29</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.26</version>
            <scope>provided</scope>
        </dependency>
<!--        RUNTIME SCOPE NO LONGER SUPPLIED BY FLUXTION COMPILER-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>${fluxtion.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <scope>compile</scope>
            <version>2.0.7</version>
        </dependency>
    </dependencies>
</project>


{% endhighlight %}

# Running the application
Running the application is almost unchanged from the first tutorial, the only line that is changed how the lotteryEventProcessor
instance is instantiated in the start method from the aot generated class, LotteryProcessor.

{% highlight java %}
public class LotteryApp {
    //code removed for clarity ....

    public static void start(Consumer<String> ticketReceiptHandler, Consumer<String> resultsPublisher){
        //AOT USAGE LINE BELOW
        var lotteryEventProcessor = new LotteryProcessor();
        lotteryEventProcessor.init();
        //...
    }
}
{% endhighlight %}

Executing our application produces the same output as the first tutorial, but executes much faster.

{% highlight console %}
[main] INFO LotteryMachineNode - started
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO TicketStoreNode - store opened
[main] INFO LotteryApp - good luck with Ticket[number=126556, id=bdd084c8-de6f-4e6a-aa6c-15976724c8d3]
[main] INFO LotteryMachineNode - tickets sold:1
[main] INFO LotteryApp - good luck with Ticket[number=365858, id=5b211b4c-0364-4406-a412-5bb0f214b8b7]
[main] INFO LotteryMachineNode - tickets sold:2
[main] INFO LotteryApp - good luck with Ticket[number=730012, id=a8de75a9-a647-4612-b6c9-ef14116d4847]
[main] INFO LotteryMachineNode - tickets sold:3
[main] INFO LotteryApp - invalid numbers Ticket[number=25, id=e50a72f2-e305-4053-a344-97d75b9ab7f6]
[main] INFO TicketStoreNode - store closed
[main] INFO LotteryApp - store shut - no tickets can be bought
[main] INFO LotteryMachineNode - WINNING ticket Ticket[number=126556, id=bdd084c8-de6f-4e6a-aa6c-15976724c8d3]

Process finished with exit code 0
{% endhighlight %}

# Conclusion
In this tutorial we have seen that moving from interpreted to aot generated event processor is simple with Fluxtion. The
use of the aot container within the client code is totally unchanged. With very little effort the following benefits are 
realised:

- Moving to aot container is quick and easy
- Aot reduces cost and startup times
- Zero gc dependency container 
- Generated source makes debugging easier for the developer
- The aot container has a single Fluxtion dependency and no 3rd party dependencies
- Less dynamic behaviour at runtime 

I hope you have enjoyed reading this tutorial, and it has given you a desire to try running the container in aot mode
within your applications. Please send me in any comments or suggestions to improve this tutorial

[next tutorial 3](tutorial-3.md)
{: .text-right }