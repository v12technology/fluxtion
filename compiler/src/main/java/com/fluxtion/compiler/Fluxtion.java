package com.fluxtion.compiler;

import com.fluxtion.compiler.builder.factory.RootInjectedNode;
import com.fluxtion.compiler.generation.compiler.InProcessSepCompiler;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.partition.LambdaReflection.SerializableConsumer;
import lombok.SneakyThrows;

/**
 * Entry point for generating a {@link StaticEventProcessor}
 */
public interface Fluxtion {

    /**
     * Generates and compiles Java source code for a {@link StaticEventProcessor}. The compiled version only requires
     * the Fluxtion runtime dependencies to operate and process events.
     *
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * @see SEPConfig
     * @param sepConfig the configuration used to build this {@link StaticEventProcessor}
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    @SneakyThrows
    static EventProcessor compile(SerializableConsumer<SEPConfig> sepConfig){
       return (EventProcessor) InProcessSepCompiler.compile(sepConfig);
    }

    /**
     * Generates an in memory version of a {@link StaticEventProcessor}. The in memory version is transient and requires
     * the runtime and compiler Fluxtion libraries to operate.
     *
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * @see SEPConfig
     * @param sepConfig the configuration used to build this {@link StaticEventProcessor}
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    static EventProcessor interpret(SerializableConsumer<SEPConfig> sepConfig){
        return InProcessSepCompiler.interpreted(sepConfig);
    }

    /**
     * Generates and compiles Java source code for a {@link StaticEventProcessor}. The compiled version only requires
     * the Fluxtion runtime dependencies to operate and process events.
     *
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * The root node is injected into the graph. If the node has any injected dependencies these are added to the
     * graph. If a custom builder for the root node exists this will called and additional nodes can be added to the
     * graph in the factory method.
     *
     * @param rootNode the root node of this graph
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    @SneakyThrows
    static EventProcessor compile(RootInjectedNode rootNode){
        return (EventProcessor) InProcessSepCompiler.compile(rootNode);
    }

    /**
     * Generates an in memory version of a {@link StaticEventProcessor}. The in memory version is transient and requires
     * the runtime and compiler Fluxtion libraries to operate.
     *
     * {@link Lifecycle#init()} has not been called on the returned instance. The caller must invoke init before
     * sending events to the processor using {@link StaticEventProcessor#onEvent(Object)}
     *
     * The root node is injected into the graph. If the node has any injected dependencies these are added to the
     * graph. If a custom builder for the root node exists this will called and additional nodes can be added to the
     * graph in the factory method.
     *
     * @param rootNode the root node of this graph0
     * @return An uninitialized instance of a {@link StaticEventProcessor}
     */
    @SneakyThrows
    static EventProcessor interpret(RootInjectedNode rootNode){
        return InProcessSepCompiler.interpreted(rootNode);
    }
}
