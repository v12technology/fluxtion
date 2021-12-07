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
package com.fluxtion.generator.compiler;

import static com.fluxtion.generator.Templates.JAVA_TEMPLATE;
import net.openhft.compiler.CachedCompiler;

/**
 * Configuration for the SEP compiler process. Initial calues can be read from 
 * System properties using the static method 
 * {@link SepCompilerConfig#initFromSystemProperties() } .
 * System properties read for initialisation:
 * <pre>
 * fluxtion.configClass
 * fluxtion.className
 * fluxtion.packageName
 * fluxtion.rootFactoryClass
 * fluxtion.yamlFactoryConfig
 * fluxtion.outputDirectory
 * fluxtion.resourcesOutputDirectory
 * fluxtion.templateSep
 * fluxtion.supportDirtyFiltering
 * fluxtion.assignNonPublicMembers
 * fluxtion.nodeNamingClass
 * fluxtion.filterNamingClass 
 * </pre>
 * @author Greg Higgins
 */
public class SepCompilerConfig {
    
    private static final String CONFIGCLASSDEFAULT = com.fluxtion.builder.node.SEPConfig.class.getCanonicalName();
    
    /**
     * SepConfig class, to instantiate and use to generate the SEP, if config
     * generation is used.
     * 
     * One of configClass, rootFactoryClass, yamlFactoryConfig is required.
     */
    private String configClass;
    /**
     * root factory class to instantiate and use to generate the SEP, if factory
     * generation is used.
     * 
     * One of configClass, rootFactoryClass, yamlFactoryConfig is required.
     */
    private String rootFactoryClass;
    /**
     * location of yaml file to use when generating SEP by factories specified 
     * in config.
     * 
     * One of configClass, rootFactoryClass, yamlFactoryConfig is required.
     */
    private String yamlFactoryConfig;
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
    private String buildOutputdirectory;
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
     * Flag controlling generation of conditional branching in the generated SEP.
     * 
     * not required, default = true.
     */
    private boolean supportDirtyFiltering;

    /**
     * Flag controlling generation of meta data description resources.
     * 
     * not required, default = true.
     */
    private boolean generateDescription;

    /**
     * Flag controlling use of non public fields in the generated SEP for setting
     * members. Not all platforms support "reflection" style mutators.
     * 
     * not required, default = false.
     */
    private boolean assignNonPublicMembers;

    private ClassLoader classLoader;
    
    //re-usable compiler
    private CachedCompiler cachedCompiler;
    
    public SepCompilerConfig() {
        configClass = CONFIGCLASSDEFAULT;
        supportDirtyFiltering = true;
        generateDescription = true;
        assignNonPublicMembers = false;
        compileSource = true;
        formatSource = true;
        templateSep = JAVA_TEMPLATE;
        classLoader = SepCompilerConfig.class.getClassLoader();
    }
    
    /**
     * Creates and initialises a SepCompilerConfig with system properties:
     * 
     * <pre>
     * fluxtion.configClass
     * fluxtion.className
     * fluxtion.packageName
     * fluxtion.rootFactoryClass
     * fluxtion.yamlFactoryConfig
     * fluxtion.outputDirectory
     * fluxtion.resourcesOutputDirectory
     * fluxtion.templateSep
     * fluxtion.supportDirtyFiltering
     * fluxtion.assignNonPublicMembers
     * </pre>
     *        
     * @return SepCompilerConfig configured by system properties
     */
    public static SepCompilerConfig initFromSystemProperties(){
        SepCompilerConfig config = new SepCompilerConfig();
        config.configClass = System.getProperty("fluxtion.configClass", CONFIGCLASSDEFAULT);
        config.className = System.getProperty("fluxtion.className");
        config.packageName = System.getProperty("fluxtion.packageName");
        config.rootFactoryClass = System.getProperty("fluxtion.rootFactoryClass");
        config.yamlFactoryConfig = System.getProperty("fluxtion.yamlFactoryConfig");
        config.outputDirectory = System.getProperty("fluxtion.outputDirectory");
        config.resourcesOutputDirectory = System.getProperty("fluxtion.resourcesOutputDirectory");
        config.templateSep = System.getProperty("fluxtion.templateSep", config.templateSep);
        config.supportDirtyFiltering = Boolean.valueOf(System.getProperty("fluxtion.supportDirtyFiltering", "true"));
        config.generateDescription = Boolean.getBoolean("fluxtion.generateDescription");
        config.assignNonPublicMembers = Boolean.getBoolean("fluxtion.assignNonPublicMembers");
        config.buildOutputdirectory = System.getProperty("fluxtion.build.outputdirectory", "");
        config.buildOutputdirectory = config.buildOutputdirectory.isEmpty()?null:config.buildOutputdirectory;
        return config;
    }

    public String getConfigClass() {
        return configClass;
    }

    public String getRootFactoryClass() {
        return rootFactoryClass;
    }

    public String getYamlFactoryConfig() {
        return yamlFactoryConfig;
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

    public String getBuildOutputdirectory() {
        return buildOutputdirectory;
    }

    public String getResourcesOutputDirectory() {
        return resourcesOutputDirectory;
    }

    public String getTemplateSep() {
        return templateSep;
    }

    public boolean isSupportDirtyFiltering() {
        return supportDirtyFiltering;
    }

    public boolean isGenerateDescription() {
        return generateDescription;
    }

    public boolean isAssignNonPublicMembers() {
        return assignNonPublicMembers;
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

    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    public void setRootFactoryClass(String rootFactoryClass) {
        this.rootFactoryClass = rootFactoryClass;
    }

    public void setYamlFactoryConfig(String yamlFactoryConfig) {
        this.yamlFactoryConfig = yamlFactoryConfig;
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

    public void setBuildOutputdirectory(String buildOutputdirectory) {
        this.buildOutputdirectory = buildOutputdirectory;
    }

    public void setResourcesOutputDirectory(String resourcesOutputDirectory) {
        this.resourcesOutputDirectory = resourcesOutputDirectory;
    }

    public void setTemplateSep(String templateSep) {
        this.templateSep = templateSep;
    }

    public void setSupportDirtyFiltering(boolean supportDirtyFiltering) {
        this.supportDirtyFiltering = supportDirtyFiltering;
    }

    public void setGenerateDescription(boolean generateDescription) {
        this.generateDescription = generateDescription;
    }

    public void setAssignNonPublicMembers(boolean assignNonPublicMembers) {
        this.assignNonPublicMembers = assignNonPublicMembers;
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
                + "configClass=" + configClass 
                + ", rootFactoryClass=" + rootFactoryClass 
                + ", yamlFactoryConfig=" + yamlFactoryConfig 
                + ", packageName=" + packageName 
                + ", className=" + className 
                + ", resourcesOutputDirectory=" + resourcesOutputDirectory 
                + ", outputDirectory=" + outputDirectory 
                + ", buildOutputdirectory=" + buildOutputdirectory
                + ", compileSource=" + compileSource
                + ", formatSource=" + formatSource
                + ", templateSep=" + templateSep
                + ", supportDirtyFiltering=" + supportDirtyFiltering
                + ", generateDescription=" + generateDescription 
                + ", assignNonPublicMembers=" + assignNonPublicMembers
                + '}';
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public CachedCompiler getCachedCompiler() {
        return cachedCompiler;
    }

    public void setCachedCompiler(CachedCompiler cachedCompiler) {
        this.cachedCompiler = cachedCompiler;
    }
    
}
