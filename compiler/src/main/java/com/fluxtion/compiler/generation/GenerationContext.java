/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.compiler.generation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Context for the generated output of the SEP. Provides functions to control
 * generation outputs from Fluxtion, but is not used to describe the graph
 * processing structure of a SEP.
 *
 * @author Greg Higgins
 */
@Data
@Slf4j
public class GenerationContext {

    public static GenerationContext SINGLETON;
    public static ClassLoader DEFAULT_CLASSLOADER;
    private static final AtomicInteger COUNT = new AtomicInteger();
    private final Map<? super Object, Map> cacheMap;

    /**
     * A global counter, can be used for generating unique class names.
     *
     * @return next id.
     */
    public static int nextId() {
        return COUNT.getAndIncrement();
    }

    private static class X {
    }

    public int nextId(String className) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> classCount = cacheMap.computeIfAbsent(X.class, k -> new HashMap<String, Integer>());
        String key = packageName + "." + className;
        return classCount.compute(key, (String k, Integer v) -> {
            int ret = 0;
            if (v != null) {
                ret = v + 1;
            }
            return ret;
        });
    }

    public static void setupStaticContext(String packageName, String className, File outputDirectory, File resourcesRootDirectory) {
        setupStaticContext(packageName, className, outputDirectory, resourcesRootDirectory, false);
    }

    public static void setupStaticContext(String packageName, String className, File outputDirectory, File resourcesRootDirectory, boolean createResourceDirectory) {
        File buildDir = null;
        if (!System.getProperty("fluxtion.build.outputdirectory", "").isEmpty()) {
            buildDir = new File(System.getProperty("fluxtion.build.outputdirectory", ""));
        }
        SINGLETON = new GenerationContext(packageName, className, outputDirectory, resourcesRootDirectory, buildDir);
        SINGLETON.createDirectories();
        if (createResourceDirectory) {
            SINGLETON.createResourceDirectory();
        }
    }

    public static void setupStaticContext(ClassLoader classLoader,
            String packageName,
            String className,
            File outputDirectory,
            File resourcesRootDirectory,
            boolean createResourceDirectory,
            File buildOutputDirectory,
            boolean createBuildOutputDirectory) {
        SINGLETON = new GenerationContext(
                classLoader,
                packageName,
                className,
                outputDirectory,
                resourcesRootDirectory,
                buildOutputDirectory);
        SINGLETON.createDirectories();
        if (createResourceDirectory) {
            SINGLETON.createResourceDirectory();
        }
        if (createBuildOutputDirectory && buildOutputDirectory != null) {
            buildOutputDirectory.mkdirs();
        }
    }

    /**
     * Map representing the name of the actual node class generated at SEP
     * processing stage. Allowing a generic proxy class to be used in the SEP
     * model processing phase and then replaced with the actual class reference
     * during the code generation phase. The real class name is only known after
     * the proxy has been generated.
     *
     */
    private final Map<Object, String> proxyClassMap = new HashMap<>();

    /**
     * Nodes that are to be added to the SEP
     */
    private final List<Object> nodeList = new ArrayList<>();

    /**
     * public named nodes to be added to the generated SEP
     */
    private final Map<Object, String> publicNodes = new HashMap<>();

    private final ClassLoader classLoader;

    /**
     * Output package for the generated file, used where relevant
     */
    private final String packageName;

    /**
     * Class name for the generated output file
     */
    private final String sepClassName;

    /**
     * the root output directory for the code generation
     */
    private final File sourceRootDirectory;

    /**
     * The package directory = outputDirectory + packageName
     */
    private File packageDirectory;

    /**
     * the output directory for the code generation
     */
    public File resourcesRootDirectory;
    public File resourcesOutputDirectory;
    
    public GenerationContext(String packageName, String sepClassName, File outputDirectory, File resourcesRootDirectory) {
        this(packageName, sepClassName, outputDirectory, resourcesRootDirectory, null);
    }

    private GenerationContext(String packageName, String sepClassName, File outputDirectory, File resourcesRootDirectory, File buildOutputDirectory) {
        this.packageName = packageName;
        this.sepClassName = sepClassName;
        this.sourceRootDirectory = outputDirectory;
        this.resourcesRootDirectory = resourcesRootDirectory;
        if(DEFAULT_CLASSLOADER ==null){
            log.debug("DEFAULT_CLASSLOADER is null using this classloader");
            this.classLoader = this.getClass().getClassLoader();
        }else{
            log.debug("DEFAULT_CLASSLOADER is classloader");
            this.classLoader = DEFAULT_CLASSLOADER;
        }
        log.info("classloader:{}", this.classLoader);
        log.debug("built GenerationContext: {}", this);
        cacheMap = new HashMap<>();
    }

    private GenerationContext(ClassLoader classLoasder, String packageName, String sepClassName, File outputDirectory, File resourcesRootDirectory, File buildOutputDirectory) {
        this.packageName = packageName;
        this.sepClassName = sepClassName;
        this.sourceRootDirectory = outputDirectory;
        this.resourcesRootDirectory = resourcesRootDirectory;
        this.classLoader = classLoasder;
        cacheMap = new HashMap<>();
        log.debug("built GenerationContext: {}", this);
    }

    private void createDirectories() {
        packageDirectory = new File(GenerationContext.SINGLETON.sourceRootDirectory, packageName.replace(".", "/"));
//        packageDirectory.mkdirs();
        resourcesOutputDirectory = new File(resourcesRootDirectory, packageName.replace(".", "/"));
    }

    public void createResourceDirectory() {
        resourcesOutputDirectory = new File(resourcesRootDirectory, packageName.replace(".", "/"));
//        resourcesOutputDirectory.mkdirs();
    }

    public List<Object> getNodeList() {
        return nodeList;
    }

    @SuppressWarnings("unchecked")
    public <T> T addOrUseExistingNode(T node) {
        if (getNodeList().contains(node)) {
            return (T) getNodeList().get(getNodeList().indexOf(node));
        }
        getNodeList().add(node);
        return node;
    }

    /**
     * a cache that is tied to this generation context instance. A new Map will
     * be created for each unique cache key.
     *
     * @param <K> The key type of the cache map
     * @param <V> The value type of the cache map
     * @param key the cache key
     * @return the newly created map
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getCache(Object key) {
        return cacheMap.computeIfAbsent(key, (k) -> new HashMap<>());
    }

    public <T> T nameNode(T node, String name) {
        publicNodes.put(node, name);
        return node;
    }

    /**
     * removes a cache map from this instance by key.
     *
     * @param <K> The key type of the cache map
     * @param <V> The value type of the cache map
     * @param key the cache key
     * @return The mapping of the map removed or null if no mapping
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> removeCache(Object key) {
        return cacheMap.remove(key);
    }

    public static String readText(@NotNull String resourceName) throws IOException {
        StringWriter sw = new StringWriter();
        Reader isr = new InputStreamReader(getInputStream(resourceName), UTF_8);
        try {
            char[] chars = new char[8 * 1024];
            int len;
            while ((len = isr.read(chars)) > 0) {
                sw.write(chars, 0, len);
            }
        } finally {
            close(isr);
        }
        return sw.toString();
    }

    private static void close(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                System.err.println("Failed to close " + closeable + e.getMessage());
            }
        }
    }

    private static InputStream getInputStream(@NotNull String filename) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream is;
        try {
            is = contextClassLoader.getResourceAsStream(filename);
            if (is != null) {
                return is;
            }
            InputStream is2 = contextClassLoader.getResourceAsStream('/' + filename);
            if (is2 != null) {
                return is2;
            }
        }catch(Exception e){
            //problem reading - continue
        }
        return Files.newInputStream(Paths.get(filename));
    }

}
