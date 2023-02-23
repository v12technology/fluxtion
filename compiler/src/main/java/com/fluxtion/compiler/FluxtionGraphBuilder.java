package com.fluxtion.compiler;

import com.fluxtion.runtime.annotations.builder.Disabled;

import java.io.File;

/**
 * A builder class for use with {@link Fluxtion#scanAndCompileFluxtionBuilders(File...)}
 * <p>
 * Allows programmatic control of:
 * <ul>
 *     <li>Graph building using {@link #buildGraph(EventProcessorConfig)} method</li>
 *     <li>Generation using {@link #configureGeneration(FluxtionCompilerConfig)} method</li>
 * </ul>
 * <p>
 * Any builder marked with the {@link Disabled} annotation will be ignored
 */
public interface FluxtionGraphBuilder {
    void buildGraph(EventProcessorConfig eventProcessorConfig);

    void configureGeneration(FluxtionCompilerConfig compilerConfig);
}
