/* 
 * Copyright (c) 2019, V12 Technology Ltd.
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

import static com.fluxtion.compiler.generation.compiler.Templates.JAVA_TEMPLATE;

/**
 * Configuration for the EventProcessor compilation process.
 *
 * @author Greg Higgins
 */
public class FluxtionCompilerConfig {

    /**
     * output package for generated SEP
     * 
     * required.
     */
    private String packageName;
    /**
     * class name for generated SEP
     * 
     * required.
     */
    private String className;
    /**
     * Output directory for generated SEP.
     * 
     * not required.
     */
    private String outputDirectory;
    /**
     * Output directory where compiled artifacts should be written. If null 
     * no artifacts are written.
     */
    private String buildOutputDirectory;
    /**
     * Attempt to compile the generated source files
     */
    private boolean compileSource;
    /**
     * Attempt to format the generated source files
     */
    private boolean formatSource;
    /**
     * Output for any resources generated with the SEP, such as debug information.
     * 
     * not required.
     */
    private String resourcesOutputDirectory;
    /**
     * The velocity template file to use in the SEP generation process. Default 
     * value will be used if not supplied.
     * 
     * required.
     */
    private String templateSep;

    /**
     * Flag controlling generation of meta data description resources.
     * 
     * not required, default = true.
     */
    private boolean generateDescription;

    /**
     * Flag controlling where the templated source file is written or the source is transient
     *
     * not requires, default = true;
     */
    private boolean writeSourceToFile;

    private ClassLoader classLoader;

    public FluxtionCompilerConfig() {
        generateDescription = false;
        writeSourceToFile = true;
        compileSource = true;
        formatSource = true;
        templateSep = JAVA_TEMPLATE;
        classLoader = FluxtionCompilerConfig.class.getClassLoader();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getBuildOutputDirectory() {
        return buildOutputDirectory;
    }

    public String getResourcesOutputDirectory() {
        return resourcesOutputDirectory;
    }

    public String getTemplateSep() {
        return templateSep;
    }

    public boolean isGenerateDescription() {
        return generateDescription;
    }

    public boolean isCompileSource() {
        return compileSource;
    }

    public boolean isFormatSource() {
        return formatSource;
    }

    public String getFqn(){
        return getPackageName() + "." + getClassName();
    }

    public boolean isWriteSourceToFile() {
        return writeSourceToFile;
    }

    public void setWriteSourceToFile(boolean writeSourceToFile) {
        this.writeSourceToFile = writeSourceToFile;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setBuildOutputDirectory(String buildOutputDirectory) {
        this.buildOutputDirectory = buildOutputDirectory;
    }

    public void setResourcesOutputDirectory(String resourcesOutputDirectory) {
        this.resourcesOutputDirectory = resourcesOutputDirectory;
    }

    public void setTemplateSep(String templateSep) {
        this.templateSep = templateSep;
    }

    public void setGenerateDescription(boolean generateDescription) {
        this.generateDescription = generateDescription;
    }

    public void setCompileSource(boolean compileSource) {
        this.compileSource = compileSource;
    }

    public void setFormatSource(boolean formatSource) {
        this.formatSource = formatSource;
    }
    
    @Override
    public String toString() {
        return "SepCompilerConfig{"
                + ", packageName=" + packageName 
                + ", className=" + className 
                + ", resourcesOutputDirectory=" + resourcesOutputDirectory 
                + ", outputDirectory=" + outputDirectory 
                + ", buildOutputdirectory=" + buildOutputDirectory
                + ", compileSource=" + compileSource
                + ", formatSource=" + formatSource
                + ", templateSep=" + templateSep
                + ", generateDescription=" + generateDescription
                + '}';
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
