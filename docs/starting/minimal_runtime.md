---
title: Zero dependency processor
parent: First Fluxtion application
has_children: false
nav_order: 3
published: true
---

# Zero dependency processor
Fluxtion splits its libraries into runtime and builder, when combined with aot compilation
it is possible to embed a processor that has no 3rd party library dependencies. A slimmed down
event processor can be integrated into any applications without concern over conflicting libraries.
The example is located [here](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/examples/quickstart/lesson-4).

The Fluxtion builder libraries have multiple 3rd party dependencies, where as the Fluxtion runtime 
libraries have none. If the processor is generated at build time then all generation libraries
can be removed from the application. only the runtime libraries are required to execute the
event processor.

## Development process
To generate an event processor with no builder dependencies at runtime four steps are required
1. Add the Fluxtion maven plugin to the build.
1. Annotate any Fluxtion builder methods with `@SepBuilder` providing the fully qualified name of the generated processor as a parameter.
1. Remove any calls to dynamically build a processor at runtime and use the fqn above to instantiate a statically generated processor, including test cases.
1. Update the pom file to separate the scope of generation and runtime libraries.

The first three steps are covered in the previous [buildtime generation example](aot_compilation.md).

### Build zero dependency artifact
The pom is located [here](https://github.com/v12technology/fluxtion/tree/{{site.fluxtion_version}}/examples/quickstart/lesson-4/pom.xml), the relevant section are shown below.
The builder libraries are compile time only and marked with provided scope, runtime apis's are declared with default scope. The provided 
scope ensures builder libraries are not part of the projects transitive dependencies.

The maven jar plugin is configured to exclude the TradeProcessorBuilder from the final artifact.


```xml

    <profiles>
        <profile>
            <id>fluxtion-generate</id>
            <properties>
                <skipTests>true</skipTests>
            </properties>
            <build>
                <plugins>
                    <!--removes builder from the final build artifact, builder NOT required at runtime-->
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-jar-plugin</artifactId>
                        <version>3.2.0</version>
                        <configuration>
                            <excludes>
                                <exclude>**/TradeProcessorBuilder.*</exclude>
                            </excludes>
                        </configuration>
                    </plugin>  
                </plugins>
            </build>
        </profile>
    </profiles>

    <dependencies>
        <!--RUNTIME dependencies START-->
        <dependency>
            <groupId>com.fluxtion.extension</groupId>
            <artifactId>fluxtion-streaming-api</artifactId>
            <version>${project.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>it.unimi.dsi</groupId>
                    <artifactId>fastutil</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--RUNTIME dependencies END-->
        <!--COMPILE dependencies START-->
        <dependency>
            <groupId>com.fluxtion.extension</groupId>
            <artifactId>fluxtion-streaming-builder</artifactId>
            <scope>provided</scope>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
            <scope>provided</scope>
        </dependency>
        <!--COMPILE dependencies END-->
    </dependencies>

``` 


## Artifact analysis
As part of the build the maven shade plugin generates an uber jar that contains all the dependencies to run the application,
this contains Fluxtion runtime libraries, application classes and slf4j interfaces. The table below shows a comparinson
with the prevous example where the final artifact contains all the Fluxtion builders and their transitive dependencies.

| Fluxtion libraries   | Size              | External library count |
|:---------------------|------------------:|-----------------------:|
| builder + runtime    | 32,000 Kb         | 36  |
| runtime(minimal jar) | 182Kb             | 1  <br/>slf4j api|


