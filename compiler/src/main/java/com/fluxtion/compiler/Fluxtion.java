package com.fluxtion.compiler;

import com.fluxtion.compiler.generation.EventProcessorFactory;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.builder.Disabled;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

/**
 * Entry point for generating a {@link StaticEventProcessor}
 */
public interface Fluxtion {

    /**
     * Generates and compiles Java source code for a {@link StaticEventProcessor}. The compiled version only requires
     * the Fluxtion runtime dependencies to operate and process events. The source code is only maintained in memory
     * as a string and is not persisted,
     *
     * <p>
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * @param sepConfig the configuration used to build this {@link StaticEventProcessor}
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     * @see EventProcessorConfig
     */
    @SneakyThrows
    static EventProcessor compile(SerializableConsumer<EventProcessorConfig> sepConfig) {
        return EventProcessorFactory.compile(sepConfig);
    }

    /**
     * Compiles the SEP in memory and captures the output to a user supplied {@link Writer}
     *
     * @param sepConfig    graph building config
     * @param sourceWriter target source writer
     * @return
     */
    @SneakyThrows
    static EventProcessor compile(SerializableConsumer<EventProcessorConfig> sepConfig, Writer sourceWriter) {
        return EventProcessorFactory.compile(sepConfig, c -> {
            c.setSourceWriter(sourceWriter);
            c.setWriteSourceToFile(false);
            c.setGenerateDescription(false);
            c.setFormatSource(true);
        });
    }

    @SneakyThrows
    static EventProcessor compile(SerializableConsumer<EventProcessorConfig> sepConfig,
                                  SerializableConsumer<FluxtionCompilerConfig> cfgBuilder) {
        return EventProcessorFactory.compile(sepConfig, cfgBuilder);
    }

    @SneakyThrows
    static EventProcessor compileAot(SerializableConsumer<EventProcessorConfig> cfgBuilder) {
        String packageName = (cfgBuilder.getContainingClass().getCanonicalName() + "." + cfgBuilder.method().getName()).toLowerCase();
        return compile(cfgBuilder, compilerCfg -> compilerCfg.setPackageName(packageName));
    }


    @SneakyThrows
    static EventProcessor compileAot(SerializableConsumer<EventProcessorConfig> cfgBuilder,
                                     String packageName,
                                     String className) {
        return compile(cfgBuilder, compilerCfg -> {
            compilerCfg.setPackageName(packageName.trim());
            compilerCfg.setClassName(className.trim());
        });
    }

    /**
     * Generates an in memory version of a {@link StaticEventProcessor}. The in memory version is transient and requires
     * the runtime and compiler Fluxtion libraries to operate.
     * <p>
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * @param sepConfig the configuration used to build this {@link StaticEventProcessor}
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     * @see EventProcessorConfig
     */
    static EventProcessor interpret(SerializableConsumer<EventProcessorConfig> sepConfig) {
        return EventProcessorFactory.interpreted(sepConfig);
    }

    /**
     * Generates and compiles Java source code for a {@link StaticEventProcessor}. The compiled version only requires
     * the Fluxtion runtime dependencies to operate and process events.
     * <p>
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     * <p>
     * The root node is injected into the graph. If the node has any injected dependencies these are added to the
     * graph. If a custom builder for the root node exists this will called and additional nodes can be added to the
     * graph in the factory method.
     *
     * @param rootNode the root node of this graph
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    @SneakyThrows
    static EventProcessor compile(RootNodeConfig rootNode) {
        return EventProcessorFactory.compile(rootNode);
    }

    @SneakyThrows
    static EventProcessor compile(RootNodeConfig rootNode, SerializableConsumer<FluxtionCompilerConfig> cfgBuilder) {
        return EventProcessorFactory.compile(rootNode, cfgBuilder);
    }

    @SneakyThrows
    static EventProcessor compileAot(RootNodeConfig rootNode) {
        String pkg = (rootNode.getRootClass().getCanonicalName() + "." + rootNode.getName()).toLowerCase();
        return EventProcessorFactory.compile(rootNode, compilerCfg -> compilerCfg.setPackageName(pkg));
    }

    @SneakyThrows
    static EventProcessor compileAot(RootNodeConfig rootNode, String packagePrefix) {
        String pkg = (packagePrefix + "." + rootNode.getName()).toLowerCase();
        return EventProcessorFactory.compile(rootNode, compilerCfg -> compilerCfg.setPackageName(pkg));
    }

    /**
     * Generates an EventProcessor from a yaml document read from a supplied reader.
     * <p>
     * Format:
     * <p>
     * Example yaml output for a definition
     * <pre>
     * Yaml yaml = new Yaml();
     *         Map<String, Object> configMap = new HashMap<>();
     *         configMap.put("firstKey", 12);
     *         configMap.put("anotherKey", "my value");
     *         FluxtionCompilerConfig compilerConfig = new FluxtionCompilerConfig();
     *         compilerConfig.setPackageName("mypackage.whatever");
     *         DataDrivenGenerationConfig myRootConfig = new DataDrivenGenerationConfig("myRoot", MyRootClass.class.getCanonicalName(), configMap, compilerConfig);
     *         System.out.println("dumpAsMap:\n" + yaml.dumpAsMap(myRootConfig));
     * </pre>
     *
     * <pre>
     * rootClass: com.company.MyRootClass
     * name: myRoot
     * configMap:
     *   anotherKey: my value
     *   firstKey: 12
     * compilerConfig:
     *   buildOutputDirectory: null
     *   className: null
     *   compileSource: true
     *   formatSource: false
     *   generateDescription: false
     *   outputDirectory: src/main/java/
     *   packageName: mypackage.whatever
     *   resourcesOutputDirectory: src/main/resources/
     *   templateSep: template/base/javaTemplate.vsl
     *   writeSourceToFile: false
     * </pre>
     *
     * @param reader the source of the yaml document
     * @return A compile EventProcessor
     */
    @SneakyThrows
    static EventProcessor compileFromReader(Reader reader) {
        Yaml yaml = new Yaml();
        DataDrivenGenerationConfig rootInjectedConfig = yaml.loadAs(reader, DataDrivenGenerationConfig.class);
        if (rootInjectedConfig.getCompilerConfig().isCompileSource()) {
            return EventProcessorFactory.compile(rootInjectedConfig.getEventProcessorConfig(), rootInjectedConfig.getCompilerConfig());
        } else {
            return interpret(rootInjectedConfig.getRootNodeConfig());
        }
    }

    /**
     * Generates an in memory version of a {@link StaticEventProcessor}. The in memory version is transient and requires
     * the runtime and compiler Fluxtion libraries to operate.
     * <p>
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     * <p>
     * The root node is injected into the graph. If the node has any injected dependencies these are added to the
     * graph. If a custom builder for the root node exists this will called and additional nodes can be added to the
     * graph in the factory method.
     *
     * @param rootNode the root node of this graph0
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    @SneakyThrows
    static EventProcessor interpret(RootNodeConfig rootNode) {
        return EventProcessorFactory.interpreted(rootNode);
    }

    /**
     * Scans the supplied File resources for any classes that implement the {@link FluxtionGraphBuilder} interface
     * and will generate an {@link EventProcessor} for any located builders.
     * <p>
     * Any builder marked with the {@link Disabled} annotation will be ignored
     *
     * @param files The locations to search for {@link FluxtionGraphBuilder} classes
     * @return The number of processors generated
     */
    static int scanAndCompileFluxtionBuilders(File... files) {
        Objects.requireNonNull(files, "provide valid locations to search for fluxtion builders");
        LongAdder generationCount = new LongAdder();
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .overrideClasspath(files)
                .scan()) {

            ClassInfoList builderList = scanResult
                    .getClassesImplementing(FluxtionGraphBuilder.class)
                    .exclude(scanResult.getClassesWithAnnotation(Disabled.class.getCanonicalName()));

            builderList.forEach(c -> {
                generationCount.increment();
                System.out.println(generationCount.intValue() + ": invoking builder " + c.getName());
                try {
                    final FluxtionGraphBuilder newInstance = (FluxtionGraphBuilder) c.loadClass().getDeclaredConstructor().newInstance();
                    compile(newInstance::buildGraph, newInstance::configureGeneration);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException("cannot instantiate FluxtionGraphBuilder", e);
                }
            });
        }
        return generationCount.intValue();
    }
}
