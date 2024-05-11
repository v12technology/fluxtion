---
title: Maven plugin
parent: Build event processor
grand_parent: Reference documentation
has_children: false
nav_order: 3
published: true
---

# Maven fluxtion plugin
---

Fluxtion provides a maven plugin that automates the AOT generation of event processors. This confines the generation 
process to a build time activity which means only the runtime libraries are required at runtime, and the compiler library
can be provided scope.

{: .info }
If maven plugin generates all processors AOT then the compiler library can be moved to provided scope and the runtime 
library explicitly added to the runtime scope of the application. This vastly reduces runtime dependencies.
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

## Setting maven build properties
The maven plugin formats source files using the Google formatter which requires maven to export certain packages. Add a
jvm.config file in a .mvn directory of your root project, for reference see the [.mvn/jvm.config]({{site.examples_project}}/.mvn/jvm.config)
used in these examples.

{% highlight properties %}

--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED

{% endhighlight %}


# FluxtionGraphBuilder scan and compile 
The maven plugin uses the discovery method described in [processor generation scan for fluction graph builders](processor_generation#compile-aot---scan-for-fluxtiongraphbuilder). 
As the plugin performs the discovery operation as part of the build there is no need to write a main method to invoke the 
scan and compile function

**Requirements to create a discoverable builder**
* Implement the interface [FluxtionGraphBuilder]({{site.fluxtion_src_compiler}}/FluxtionGraphBuilder.java). The two call back methods separate graph building and compiler configuration
* Add fluxtion-maven-plugin to the build, configure the plugin to execute the **scan** goal
* Any Builder annotated with [`@Disabled`]({{site.fluxtion_src_runtime}}/annotations/builder/Disabled.java) is ignored by the scan function

The generated event processor is [SampleAotBuilderProcessor]({{site.reference_examples}}/generation/src/main/java/com/fluxtion/example/reference/generation/genoutput/SampleAotBuilderProcessor.java)

{% highlight xml %}

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.fluxtion.example</groupId>
        <artifactId>reference-examples</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>generation</artifactId>
    <name>reference-example :: generation</name>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
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
        <!--   UPDATE RUNTIME SCOPE, com.fluxtion:runtime NO LONGER SUPPLIED BY FLUXTION COMPILER-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>{{site.fluxtion_version}}</version>
            <scope>compile</scope>
        </dependency>
        <!--  UPDATE PROVIDED SCOPE com.fluxtion:compiler NO LONGER REQUIREDAT RUNTIME-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>


{% endhighlight %}

# Spring xml compile 
Fluxtion provides a maven plugin that automates the AOT generation of event processors that are driven by spring config files. 
The maven plugin should configure the file location of the spring config file to use in the AOT generation process. 

**Requirements to generate event processor**
* Create a spring config file in the classpath
* Add fluxtion-maven-plugin to the build configure the plugin to execute the **springToFluxtion** goal with configuration:
  * springFile config file location
  * className of the generated event processor 
  * packageName of the generated event processor 

{% highlight xml %}

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.fluxtion.example</groupId>
        <artifactId>reference-examples</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>generation</artifactId>
    <name>reference-example :: generation</name>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>com.fluxtion</groupId>
                <artifactId>fluxtion-maven-plugin</artifactId>
                <version>3.0.14</version>
                <executions>
                    <execution>
                        <id>spring to fluxtion builder</id>
                        <goals>
                            <goal>springToFluxtion</goal>
                        </goals>
                        <configuration>
                            <springFile>src/main/resources/spring-lottery.xml</springFile>
                            <className>LotteryProcessor</className>
                            <packageName>com.fluxtion.example.cookbook.lottery.aot</packageName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <dependencies>
        <!--   UPDATE RUNTIME SCOPE, com.fluxtion:runtime NO LONGER SUPPLIED BY FLUXTION COMPILER-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>{{site.fluxtion_version}}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <scope>compile</scope>
            <version>2.0.7</version>
        </dependency>
        <!--  UPDATE PROVIDED SCOPE com.fluxtion:compiler NO LONGER REQUIREDAT RUNTIME-->
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.29</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>


{% endhighlight %}

# To be documented

- Yaml support