/*
 * Copyright (c) 2019, 2024 gregory higgins.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.compiler;

import com.fluxtion.compiler.generation.OutputRegistry;
import lombok.Getter;
import lombok.Setter;

import java.io.StringWriter;
import java.io.Writer;

import static com.fluxtion.compiler.generation.compiler.Templates.JAVA_TEMPLATE;

/**
 * Configuration for the EventProcessor compilation process.
 *
 * @author Greg Higgins
 */
@Getter
public class FluxtionCompilerConfig {

    /**
     * output package for generated SEP
     * <p>
     * required.
     */
    @Setter
    private String packageName;
    /**
     * class name for generated SEP
     * <p>
     * required.
     */
    @Setter
    private String className;
    /**
     * Output directory for generated SEP.
     * <p>
     * not required.
     */
    @Setter
    private String outputDirectory;
    /**
     * Output directory where compiled artifacts should be written. If null
     * no artifacts are written.
     */
    @Setter
    private String buildOutputDirectory;
    /**
     * Attempt to compile the generated source files
     */
    @Setter
    private boolean compileSource;
    /**
     * Generate an interpreted version
     */
    @Setter
    private boolean interpreted = false;
    /**
     * Generate a compiled version that uses the objects supplied as nodes in the processor. The dispatch table is
     * compiled
     */
    @Setter
    private boolean dispatchOnlyVersion = false;
    /**
     * Attempt to format the generated source files
     */
    @Setter
    private boolean formatSource;
    /**
     * Output for any resources generated with the SEP, such as debug information.
     * <p>
     * not required.
     */
    @Setter
    private String resourcesOutputDirectory;
    /**
     * The velocity template file to use in the SEP generation process. Default
     * value will be used if not supplied.
     * <p>
     * required.
     */
    @Setter
    private String templateSep;

    /**
     * Flag controlling generation of meta data description resources.
     * <p>
     * not required, default = true.
     */
    @Setter
    private boolean generateDescription;

    /**
     * Flag controlling where the templated source file is written or the source is transient
     * <p>
     * not requires, default = true;
     */
    @Setter
    private boolean writeSourceToFile;

    /**
     * The if {@link #writeSourceToFile} is false this writer will capture the content of the generation process
     */
    private Writer sourceWriter;
    /**
     * Flag controlling adding build time to generated source files
     */
    @Setter
    private boolean addBuildTime;

    @Setter
    private transient ClassLoader classLoader;

    public FluxtionCompilerConfig() {
        generateDescription = false;
        writeSourceToFile = false;
        compileSource = true;
        addBuildTime = false;
        formatSource = true;
        templateSep = JAVA_TEMPLATE;
        classLoader = FluxtionCompilerConfig.class.getClassLoader();
        outputDirectory = OutputRegistry.JAVA_SRC_DIR;
        resourcesOutputDirectory = OutputRegistry.RESOURCE_DIR;
        sourceWriter = new StringWriter();
    }

    public String getFqn() {
        return getPackageName() + "." + getClassName();
    }

    public void setSourceWriter(Writer sourceWriter) {
        setFormatSource(true);
        setWriteSourceToFile(false);
        this.sourceWriter = sourceWriter;
    }

    @Override
    public String toString() {
        return "SepCompilerConfig{"
                + "packageName=" + packageName
                + ", className=" + className
                + ", resourcesOutputDirectory=" + resourcesOutputDirectory
                + ", outputDirectory=" + outputDirectory
                + ", buildOutputdirectory=" + buildOutputDirectory
                + ", writeSourceToFile=" + writeSourceToFile
                + ", compileSource=" + compileSource
                + ", interpreted=" + interpreted
                + ", formatSource=" + formatSource
                + ", templateSep=" + templateSep
                + ", generateDescription=" + generateDescription
                + '}';
    }

}
