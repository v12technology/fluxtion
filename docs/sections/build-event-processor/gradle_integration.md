---
title: Gradle integration
parent: Build event processor
has_children: false
nav_order: 4
published: true
example_project: https://github.com/v12technology/fluxtion-examples/tree/main/gradle-scan-example
example_src: https://github.com/v12technology/fluxtion-examples/blob/main/gradle-scan-example/src/main/java/com/fluxtion/example/generation/gradle/GenerateFluxtion.java
example_gradle: https://github.com/v12technology/fluxtion-examples/blob/main/gradle-scan-example/build.gradle
---

# Gradle integration

Although no Fluxtion gradle plugin is available to scan and generate event processors AOT, gradle's open architecture
makes it easy to integrate a task to wrap a utility. To integrate `Fluxtion.scanAndCompileFluxtionBuilders` as a build 
task into a gradle project the following steps are needed:

* Add Fluxtion compile as a dependency to build.gradle
* Create a main method that invokes `Fluxtion.scanAndCompileFluxtionBuilders(classesDirectory)`
* Register the main method as a task in build.gradle
* Run the task when you want to regenerate event processors AOT

The [sample project]({{page.example_project}}) demonstrates integrating gradle with the Fluxtion scan and compile utility for AOT generation.

## Add dependencies
The [build.gradle]({{page.example_gradle}}) file declares the dependencies for the project.
{% highlight groovy %}
dependencies {
    implementation          'com.fluxtion:compiler:{{site.fluxtion_version}}'
    annotationProcessor     'com.fluxtion:compiler:{{site.fluxtion_version}}'
}
{% endhighlight %}


## Create main to wrap Fluxtion scan and compile 
The [GenerateFluxtion]({{page.example_src}}) main method calls the scanAndCompileFluxtionBuilders utility.

{% highlight java %}
public class GenerateFluxtion {
    public static void main(String[] args) throws Exception {
        File classesDirectory = new File("build/classes/java/main");
        System.out.println("Scanning " + classesDirectory.getAbsolutePath() + " for builders");
        Fluxtion.scanAndCompileFluxtionBuilders(classesDirectory);
    }
}
{% endhighlight %}


## Register build task
The [build.gradle]({{page.example_gradle}}) file register the GenerateFluxtion main as a task for the project.

{% highlight groovy %}
tasks.register('generateEventProcessors', JavaExec) {
    group = "generation"
    description = "Generate fluxtion event processors"
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'com.fluxtion.example.generation.gradle.GenerateFluxtion'
}
{% endhighlight %}


## Generate event processor with task

Generate the processors AOT with 

`./gradlew generateEventProcessors`

{% highlight console %}
./gradlew generateEventProcessors

> Task :generateEventProcessors
> Scanning /fluxtion-examples/gradle-scan-example/build/classes/java/main for builders
> 1: invoking builder com.fluxtion.example.generation.gradle.SampleAotBuilder
> 01-May-24 21:00:50 [main] INFO EventProcessorCompilation - generated EventProcessor file: /fluxtion-examples/gradle-scan-example/src/main/java/com/fluxtion/example/generation/gradle/generated/SampleAot.java

BUILD SUCCESSFUL in 6s
3 actionable tasks: 2 executed, 1 up-to-date
{% endhighlight %}



