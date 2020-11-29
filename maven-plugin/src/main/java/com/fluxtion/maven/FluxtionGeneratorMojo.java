/* 
 * Copyright (C) 2017-2020 V12 Technology Limited
 *
 * This file is part of Fluxtion.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fluxtion.maven;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * A mojo to wrap the invocation of the Fluxtion executable.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
@Mojo(name = "generate",
        requiresProject = true,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        defaultPhase = LifecyclePhase.COMPILE
)
public class FluxtionGeneratorMojo extends AbstractMojo {

    private String classPath;

    @Override
    public void execute() throws MojoExecutionException {
        if (System.getProperty("skipFluxtion") != null) {
            getLog().info("Fluxtion generation skipped.");
            return;
        }
        try {
            updateClasspath();
            try {
                setDefaultProperties();
                List<String> cmdList = new ArrayList<>();
//                cmdList.add("java com.fluxtion.generator.Main.main");
                if (logDebug) {
                    cmdList.add("-debug");
                }
                cmdList.add("-outDirectory");
                cmdList.add(outputDirectory);
                cmdList.add("-buildDirectory");
                cmdList.add(buildDirectory);
                cmdList.add("-outResDirectory");
                cmdList.add(resourcesOutputDirectory);
                cmdList.add("-outPackage");
                cmdList.add(packageName);
                cmdList.add("-configClass");
                cmdList.add(configClass);
                cmdList.add("-outClass");
                cmdList.add(className);
                cmdList.add("-buildClasses");
                cmdList.add(Boolean.toString(compileGenerated));
                cmdList.add("-formatSource");
                cmdList.add(Boolean.toString(formatSource));
                cmdList.add("-supportDirtyFiltering");
                cmdList.add(Boolean.toString(supportDirtyFiltering));
                cmdList.add("-generateDebugPrep");
                cmdList.add(Boolean.toString(generateDebugPrep));
                cmdList.add("-generateDescription");
                cmdList.add(Boolean.toString(generateDescription));
//                cmdList.add("-generateTestDecorator");
//                cmdList.add(Boolean.toString(generateTestDecorator));
                cmdList.add("-assignPrivate");
                cmdList.add(Boolean.toString(assignNonPublicMembers));
                //optionals
//                if (nodeNamingClass != null) {
//                    cmdList.add("-nodeNamingClass");
//                    cmdList.add(nodeNamingClass);
//                }
//                if (filterNamingClass != null) {
//                    cmdList.add("-filterNamingClass");
//                    cmdList.add(filterNamingClass);
//                }
                if (rootFactoryClass != null) {
                    cmdList.add("-rootFactoryClass");
                    cmdList.add(rootFactoryClass);
                }
                if (yamlFactoryConfig != null) {
                    cmdList.add("-yamlFactoryConfig");
                    cmdList.add(yamlFactoryConfig.getCanonicalPath());
                }
                if (templateSep != null) {
                    cmdList.add("-sepTemplate");
                    cmdList.add(templateSep);
                }
                if (templateDebugSep != null) {
                    cmdList.add("-sepDebugTemplate");
                    cmdList.add(templateDebugSep);
                }
                //proxy settings
                if (http_proxyHost != null) {
                    cmdList.add("-http.proxyHost");
                    cmdList.add(http_proxyHost);
                }
                if (http_proxyPort != null) {
                    cmdList.add("-http.proxyPort");
                    cmdList.add(http_proxyPort);
                }
                if (http_proxyUser != null) {
                    cmdList.add("-http.proxyUser");
                    cmdList.add(http_proxyUser);
                }
                if (http_proxyPassword != null) {
                    cmdList.add("-http_proxyPassword");
                    cmdList.add(http_proxyPassword);
                }
                //must be at end
                cmdList.add("-cp");
                cmdList.add(classPath);
                getLog().info("java -jar fluxtion.jar " + cmdList.stream().collect(Collectors.joining(" ")));
                throw new UnsupportedOperationException("unsupported maven goal");
//                com.fluxtion.generator.Main.main(cmdList.toArray(new String[0]));
            } catch (IOException e) {
                getLog().error("error while invoking Fluxtion generator", e);
                throw new RuntimeException(e);
            }
        } catch (MalformedURLException | DependencyResolutionRequiredException ex) {
            getLog().error("error while building classpath", ex);
            throw new RuntimeException(ex);
        }
    }

    private void setDefaultProperties() throws MojoExecutionException, IOException {
        try {
            if (outputDirectory == null || outputDirectory.length() < 1) {
                outputDirectory = project.getBasedir().getCanonicalPath() + "/target/generated-sources/java";
            } else if (!outputDirectory.startsWith("/")) {
                outputDirectory = project.getBasedir().getCanonicalPath() + "/" + outputDirectory;
            }
            if (resourcesOutputDirectory == null || resourcesOutputDirectory.length() < 1) {
                resourcesOutputDirectory = project.getBasedir().getCanonicalPath() + "/target/generated-sources/sep";
            } else if (!resourcesOutputDirectory.startsWith("/")) {
                resourcesOutputDirectory = project.getBasedir().getCanonicalPath() + "/" + resourcesOutputDirectory;
            }
            if (buildDirectory == null) {
                buildDirectory = project.getBasedir().getCanonicalPath() + "/target/classes";
            } else if (!buildDirectory.startsWith("/")) {
                buildDirectory = project.getBasedir().getCanonicalPath() + "/" + buildDirectory;
            }
        } catch (IOException iOException) {
            getLog().error(iOException);
            throw new MojoExecutionException("problem setting default properties", iOException);
        }
    }

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * The fully qualified name of SEPConfig class that fluxtion will use to
     * build the static event processor
     */
    @Parameter(property = "configClass", defaultValue = "com.fluxtion.builder.node.SEPConfig")
    private String configClass;

    /**
     * The output package of the generated static event processor.
     */
    @Parameter(property = "packageName", required = true)
    private String packageName;

    /**
     * The simple class name of the generated static event processor.
     */
    @Parameter(property = "className", required = true)
    private String className;

    /**
     * The fully qualified name of a root NoceFactory class that will be used in
     * conjunction of the yaml factory configuration to generate a static event
     * processor.
     */
    @Parameter(property = "rootFactoryClass", required = false)
    private String rootFactoryClass;

    /**
     * The yaml configuration that is used in conjunction with a root
     * NoceFactory to generate a static event processor.
     */
    @Parameter(property = "yamlFactoryConfig", required = false)
    private File yamlFactoryConfig;

    /**
     * The output directory for source artifacts generated by fluxtion. Absolute
     * paths are preceded with "/" otherwise the path relative to the project
     * root directory
     */
    @Parameter(property = "outputDirectory", defaultValue = "target/generated-sources/java")
    private String outputDirectory;

    /**
     * The output directory for build artifacts generated by fluxtion. Absolute
     * paths are preceded with "/" otherwise the path relative to the project
     * root directory
     */
    @Parameter(property = "buildDirectory", defaultValue = "target/classes")
    private String buildDirectory;

    /**
     * The output directory for resources generated by fluxtion, such as a
     * meta-data describing the static event processor. Absolute paths are
     * preceded with "/" otherwise the path relative to the project root
     * directory
     */
    @Parameter(property = "resourcesOutputDirectory", defaultValue = "src/main/resources")
//    @Parameter(property = "resourcesOutputDirectory", defaultValue = "target/generated-sources/fluxtion")
    private String resourcesOutputDirectory;

    /**
     * Override the velocity template file that is used by fluxtion to generate
     * the static event processor
     */
    @Parameter(property = "templateSep")
    private String templateSep;

    /**
     * Override the velocity template file that is used by fluxtion to generate
     * the debug static event processor
     */
    @Parameter(property = "templateDebugSep")
    private String templateDebugSep;

    /**
     * Override whether the generated static event processor supports dirty
     * filtering.
     */
    @Parameter(property = "supportDirtyFiltering", defaultValue = "true")
    private boolean supportDirtyFiltering;

    /**
     * Generate a debug version of the static event processor for use with the
     * fluxtion graphical debugger tool.
     */
    @Parameter(property = "generateDebugPrep", defaultValue = "false")
    public boolean generateDebugPrep;

    /**
     * Generate meta data for the generated static event processor. The meta
     * includes a png and graphml describing the SEP.
     */
    @Parameter(property = "generateDescription", defaultValue = "true")
    public boolean generateDescription;

    /**
     * Generate a test decorator for the static event processor
     */
    @Parameter(property = "generateTestDecorator", defaultValue = "false")
    public boolean generateTestDecorator;

    /**
     * Override whether the generated static event processor supports reflection
     * based assignment for initialisation.
     */
    @Parameter(property = "assignNonPublicMembers", defaultValue = "false")
    public boolean assignNonPublicMembers;

    /**
     * Compile the source artifacts, placing the results in the build directory
     */
    @Parameter(property = "compileGenerated", defaultValue = "true")
    public boolean compileGenerated;

    /**
     * Format the generated source files.
     */
    @Parameter(property = "formatSource", defaultValue = "true")
    public boolean formatSource;

    /**
     * Set log level to debug for fluxtion generation.
     */
    @Parameter(property = "logDebug", defaultValue = "false")
    public boolean logDebug;

    /**
     * Set the http.proxyHost fluxtion will traverse to authenticate with the
     * license server
     */
    @Parameter(property = "http.proxyHost")
    private String http_proxyHost;

    /**
     * Set the http.proxyPort fluxtion will traverse to authenticate with the
     * license server
     */
    @Parameter(property = "http.proxyPort")
    private String http_proxyPort;

    /**
     * Set the http.proxyUser fluxtion will traverse to authenticate with the
     * license server
     */
    @Parameter(property = "http.proxyUser")
    private String http_proxyUser;

    /**
     * Set the http.proxyUser fluxtion will traverse to authenticate with the
     * license server
     */
    @Parameter(property = "http.proxyPassword")
    private String http_proxyPassword;

    /**
     * continue build even if fluxtion tool returns an error
     */
    @Parameter(property = "ignoreErrors", defaultValue = "false")
    public boolean ignoreErrors;

    private void updateClasspath() throws MojoExecutionException, MalformedURLException, DependencyResolutionRequiredException {
        StringBuilder sb = new StringBuilder();
        List<String> elements = project.getCompileClasspathElements();
        for (String element : elements) {
            File elementFile = new File(element);
            getLog().debug("Adding element from runtime to classpath:" + elementFile.getPath());
            sb.append(elementFile.getPath()).append(";");
        }
        classPath = sb.substring(0, sb.length() - 1);
        getLog().debug("classpath:" + classPath);
    }
}
