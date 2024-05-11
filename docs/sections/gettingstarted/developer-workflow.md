---
title: Developer workflow
parent: Developer tutorials
has_children: false
nav_order: 1
published: true
example_project: https://github.com/v12technology/fluxtion-examples/tree/main/getting-started/developer-workflow
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/getting-started/developer-workflow/src/main/java/com/fluxtion/example/devworkflow
example_test: https://github.com/v12technology/fluxtion-examples/tree/main/getting-started/developer-workflow/src/test/java/com/fluxtion/example/devworkflow
---


# Developer workflow with Fluxtion
{: .no_toc }
---

Fluxtion has been designed to have a simple on ramp that decreases developer inertia for building event driven logic.
A good approach to using Fluxtion for the first time or integrating into an existing project is:

1. Add the fluxtion-compiler library as a runtime dependency
2. Use Fluxtion.interpret to experiment and create application logic, events and service interfaces
3. Refactor the research event handling code ready for production, and call from unit tests
4. For AOT move fluxtion-compiler to compile only dependency, use maven plugin to generate event processor as part of the build
5. Use the event processor in your application

Each step builds incrementally allowing progress at the developers chosen pace, there is no big bang requirement to get 
started with Fluxtion

{: .no_toc }
<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

## Example project
{: .no_toc }
In our example we are building an authorized command executor only authorized users can execute a command. The example
source [project is here]({{page.example_project}}).

### Processing logic
{: .no_toc }
Our design sketches show what we intend to integrate into our system

```mermaid
flowchart TB
    {{site.mermaid_eventHandler}}
    {{site.mermaid_graphNode}}
    {{site.mermaid_exportedService}}
    {{site.mermaid_eventProcessor}}

    AuthorizedCall><b>ServiceCall</b> authorize:CommandPermission]:::eventHandler
    RemoveAuthorizedCall><b>ServiceCall</b> removeAuthorized:CommandPermission]:::eventHandler
    
    AdminCommand><b>InputEvent</b>::AdminCommand]:::eventHandler
    CommandAuthorizer([<b>ServiceLookup</b>::CommandAuthorizer]):::exportedService

    CommandExecutor[CommandExecutor\n<b>EventHandler</b>::AdminCommand]:::graphNode
    CommandAuthorizerNode[CommandAuthorizerNode\n <b>ExportService</b>::CommandAuthorizer]:::graphNode

    AdminCommand --> CommandExecutor
    AuthorizedCall & RemoveAuthorizedCall --> CommandAuthorizer --> CommandAuthorizerNode
 
    subgraph EventProcessor
        CommandAuthorizerNode --> CommandExecutor
    end

```

## 1 - Add fluxtion-compiler build dependency

Maven build file for getting started with Fluxtion-compiler as a runtime dependency. `Fluxtion.interpret` requires the 
compiler library at runtime.

<div class="tab">
  <button class="tablinks" onclick="openTab(event, 'pom_xml')" id="defaultOpen">Maven pom</button>
  <button class="tablinks" onclick="openTab(event, 'Maven')">Maven dependencies</button>
  <button class="tablinks" onclick="openTab(event, 'Gradle')">Gradle dependencies</button>
</div>
<div id="Maven" class="tabcontent">
<div markdown="1">
{% highlight xml %}
    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>runtime</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
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
implementation 'com.fluxtion:runtime:{{site.fluxtion_version}}'
implementation 'com.fluxtion:compiler:{{site.fluxtion_version}}'
{% endhighlight %}
</div>
</div>
<div id="pom_xml" class="tabcontent">
<div markdown="1">
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <artifactId>example.master</artifactId>
        <groupId>com.fluxtion.example</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <name>getting-started :: developer-workflow</name>
    <artifactId>developer-workflow</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.fluxtion</groupId>
            <artifactId>compiler</artifactId>
            <version>{{site.fluxtion_version}}</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>33.1.0-jre</version>
        </dependency>
    </dependencies>
</project>
{% endhighlight %}
</div>
</div>


<script>
document.getElementById("defaultOpen").click();
document.getElementById("defaultExample").click();
</script>

## 2 - Experiment with app solutions

One approach is to add all the classes as nested classes into a experiment class with a main method. Use `Fluxtion.interpret`
to construct the processor and fire events into it or make service calls. 

For our research we are experimenting with:

* Records as events
* Guava multimap as a permission map
* Adding/removing permissions via an api
* Using a main method to construct the processor with Fluxtion.interpret
* Adding debug methods, printing to System.out for logging

### Code sample
The quick development cycle allows the developer to experiment and develop with very little friction, see [MainExperiment]({{page.example_src}}/experiment/MainExperiment.java)

{% highlight java %}
public class MainExperiment {
    public static void main(String[] args) {
        var processor = Fluxtion.interpret(new CommandExecutor(new CommandAuthorizerNode()));
        processor.init();

        CommandAuthorizer commandAuthorizer = processor.getExportedService();
        commandAuthorizer.authorize(new CommandPermission("admin", "shutdown"));
        commandAuthorizer.authorize(new CommandPermission("admin", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Aslam", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Puck", "createMischief"));

        commandAuthorizer.dumpMap();

        processor.onEvent(new AdminCommand("admin", "shutdown", () -> System.out.println("executing shutdown command")));
        processor.onEvent(new AdminCommand("Aslam", "listUser", () -> System.out.println("executing listUser command")));
        processor.onEvent(new AdminCommand("Puck", "createMischief", () -> System.out.println("move the stool")));
        processor.onEvent(new AdminCommand("Aslam", "shutdown", () -> System.out.println("executing shutdown command")));

        commandAuthorizer.removeAuthorized(new CommandPermission("Puck", "createMischief"));
        commandAuthorizer.dumpMap();
        processor.onEvent(new AdminCommand("Puck", "createMischief", () -> System.out.println("move the stool")));
    }
    
    public interface CommandAuthorizer {
        boolean authorize(CommandPermission commandPermission);
        boolean removeAuthorized(CommandPermission commandPermission);
        //used for testing
        void dumpMap();
    }

    public static class CommandAuthorizerNode implements @ExportService CommandAuthorizer {
        private transient final Multimap<String, String> permissionMap = HashMultimap.create();

        @Override
        public boolean authorize(CommandPermission commandPermission) {
            permissionMap.put(commandPermission.user, commandPermission.command);
            return false;
        }

        @Override
        public boolean removeAuthorized(CommandPermission commandPermission) {
            permissionMap.remove(commandPermission.user, commandPermission.command);
            return false;
        }

        @Override
        public void dumpMap() {
            System.out.println("""
                    
                    Permission map
                    --------------------
                    %s
                    --------------------
                    """.formatted(permissionMap.toString()));
        }

        boolean isAuthorized(AdminCommand adminCommand) {
            return permissionMap.containsEntry(adminCommand.user, adminCommand.command);
        }
    }

    @Data
    public static class CommandExecutor {
        @NoTriggerReference
        private final CommandAuthorizerNode commandAuthorizer;

        @OnEventHandler
        public boolean executeCommand(AdminCommand command) {
            boolean authorized = commandAuthorizer.isAuthorized(command);
            if (authorized) {
                System.out.println("Executing command " + command);
                command.commandToExecute().run();
            } else {
                System.out.println("FAILED authorization for command " + command);
            }
            return authorized;
        }
    }

    public record CommandPermission(String user, String command) { }

    public record AdminCommand(String user, String command, Runnable commandToExecute) {
        @Override
        public String toString() {
            return "AdminCommand{user='" + user + '\'' + ", command='" + command + '\'' +'}';
        }
    }
}
{% endhighlight %}


### Experiment execution output

{% highlight console %}
Permission map
--------------------
{admin=[listUser, shutdown], Puck=[createMischief], Aslam=[listUser]}
--------------------

Executing command AdminCommand{user='admin', command='shutdown'}
executing shutdown command
Executing command AdminCommand{user='Aslam', command='listUser'}
executing listUser command
Executing command AdminCommand{user='Puck', command='createMischief'}
move the stool
FAILED authorization for command AdminCommand{user='Aslam', command='shutdown'}

Permission map
--------------------
{admin=[listUser, shutdown], Aslam=[listUser]}
--------------------

FAILED authorization for command AdminCommand{user='Puck', command='createMischief'}
{% endhighlight %}

## 3 - Integrate and unit test
We are happy with the direction of the development so we move to integrating the event processor into our application.

* Move all our nested classes to top level classes. Delete the Experiment class with the test main method
* Remove debug methods and any debug print statements
* Create a unit test that validates our desired behaviour

The experiment is refactored into [the integrating package]({{page.example_src}}/integrating/)

### Unit test
The [sample unit test]({{page.example_test}}/integrating/CommandExecutorTest.java) demonstrates how our solution now meets the application requirements

{% highlight java %}

class CommandExecutorTest {
    @Test
    public void testPermission(){
        var processor = Fluxtion.interpret(new CommandExecutor(new CommandAuthorizerNode()));
        processor.init();

        CommandAuthorizer commandAuthorizer = processor.getExportedService();
        commandAuthorizer.authorize(new CommandPermission("admin", "shutdown"));
        commandAuthorizer.authorize(new CommandPermission("admin", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Aslam", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Puck", "createMischief"));

        LongAdder longAdder = new LongAdder();
        processor.onEvent(new AdminCommand("admin", "shutdown", longAdder::increment));
        Assertions.assertEquals(1, longAdder.intValue());

        processor.onEvent(new AdminCommand("Aslam", "listUser", longAdder::increment));
        Assertions.assertEquals(2, longAdder.intValue());

        processor.onEvent(new AdminCommand("Puck", "createMischief", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());

        processor.onEvent(new AdminCommand("Aslam", "shutdown", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());

        commandAuthorizer.removeAuthorized(new CommandPermission("Puck", "createMischief"));
        processor.onEvent(new AdminCommand("Puck", "createMischief", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());
    }
}

{% endhighlight %}

## 4 - Build AOT processor
We are happy with the behaviour of our event processor and want this behaviour to be fixed at build time so our application
is less dynamic and starts up more quickly. 

* Add the fluxtion maven plugin to our pom, configured to execute the scan task
* Extend FluxtionGraphBuilder to build the processor and configure the generated source files
* Run the build to generate the event processor AOT
* Update the unit tests to reference the AOT processor, removing the `Fluxtion.interpret` calls

The interpreted version is refactored into [the aot package]({{page.example_src}}/aot/)

### Aot builder
Refactor the processor building code into [PermissionAotBuilder]({{page.example_src}}/aot/PermissionAotBuilder.java) that
will be called as part of the build process by the maven plugin. We may have to resolve source code generation issues at this point:

{% highlight java %}
public class PermissionAotBuilder implements FluxtionGraphBuilder {
    @Override
    public void buildGraph(EventProcessorConfig eventProcessorConfig) {
        eventProcessorConfig.addNode(new CommandExecutor(new CommandAuthorizerNode()));
    }

    @Override
    public void configureGeneration(FluxtionCompilerConfig compilerConfig) {
        compilerConfig.setClassName("PermittedCommandProcessor");
        compilerConfig.setPackageName("com.fluxtion.example.devworkflow.aot.generated");
    }
}

{% endhighlight %}

### AOT Unit test
The [sample unit test]({{page.example_test}}/aot/CommandExecutorTest.java) is updated to use the AOT processor with this line changed

`var processor = new PermittedCommandProcessor();`

{% highlight java %}

class CommandExecutorTest {
    @Test
    public void testPermission(){
        var processor = new PermittedCommandProcessor();
        processor.init();

        CommandAuthorizer commandAuthorizer = processor.getExportedService();
        commandAuthorizer.authorize(new CommandPermission("admin", "shutdown"));
        commandAuthorizer.authorize(new CommandPermission("admin", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Aslam", "listUser"));
        commandAuthorizer.authorize(new CommandPermission("Puck", "createMischief"));

        LongAdder longAdder = new LongAdder();
        processor.onEvent(new AdminCommand("admin", "shutdown", longAdder::increment));
        Assertions.assertEquals(1, longAdder.intValue());

        processor.onEvent(new AdminCommand("Aslam", "listUser", longAdder::increment));
        Assertions.assertEquals(2, longAdder.intValue());

        processor.onEvent(new AdminCommand("Puck", "createMischief", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());

        processor.onEvent(new AdminCommand("Aslam", "shutdown", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());

        commandAuthorizer.removeAuthorized(new CommandPermission("Puck", "createMischief"));
        processor.onEvent(new AdminCommand("Puck", "createMischief", longAdder::increment));
        Assertions.assertEquals(3, longAdder.intValue());
    }
}

{% endhighlight %}

### Update pom for AOT generation

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

    <name>getting-started :: developer-workflow</name>
    <artifactId>developer-workflow</artifactId>

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
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>33.1.0-jre</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.10.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>


{% endhighlight %}


## 5 - Application integrating 
Application integration is a simple matter of 
* creating an instance of PermittedCommandProcessor and call init
* lookup exported service CommandAuthorizer
* Either call service methods or onEvent

{% highlight java %}

var processor = new PermittedCommandProcessor();
processor.init();
CommandAuthorizer commandAuthorizer = processor.getExportedService();

{% endhighlight %}