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
package com.fluxtion.ext.streaming.builder.util;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.generator.compiler.OutputRegistry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.openhft.compiler.CachedCompiler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * various utility functions to help generate code for the SEP.
 *
 * @author Greg Higgins
 */
public interface FunctionGeneratorHelper {

    static int intFromMap(Map<String, ?> configMap, String key, int defualtValue) {
        if (configMap.containsKey(key)) {
            try {
                String val = "" + configMap.get(key);
                defualtValue = Integer.parseInt(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defualtValue;
    }
    
    static File sourcesDir(boolean isTest){
        String dir = System.getProperty("fluxtion.cacheDirectory");
        File sourcesDir = new File(isTest?OutputRegistry.JAVA_TESTGEN_DIR:OutputRegistry.JAVA_GEN_DIR);
        if (dir != null) {
            sourcesDir = new File(dir + "/source/");
        }
        return sourcesDir;
    }
    
    static File resourcesDir(boolean isTest){
        String dir = System.getProperty("fluxtion.cacheDirectory");
        File resourcesDir = new File(isTest?OutputRegistry.RESOURCE_TEST_DIR:OutputRegistry.RESOURCE_DIR);
        if (dir != null) {
            resourcesDir = new File(dir + "/resources/");
        }
        return resourcesDir;
    }

    static <T> Class<T> compile(Reader srcFile, String fqn) throws IOException, ClassNotFoundException {
        return compile(srcFile, fqn, false);
    }

    static <T> Class<T> compileTest(Reader srcFile, String fqn) throws IOException, ClassNotFoundException {
        return compile(srcFile, fqn, true);
    }
    
    static <T> Class<T> compile(Reader srcFile, String fqn, boolean isTest) throws IOException, ClassNotFoundException {
        String packageName = ClassUtils.getPackageCanonicalName(fqn);
        String className = ClassUtils.getShortCanonicalName(fqn);
        String dir = System.getProperty("fluxtion.cacheDirectory");
        File sourcesDir = sourcesDir(isTest);
        File resourcesDir = resourcesDir(isTest);
        if (dir != null) {
            System.setProperty("fluxtion.build.outputdirectory", dir + "/classes/");
        }
        GenerationContext.updateContext(packageName, className, sourcesDir, resourcesDir);
        CachedCompiler javaCompiler = GenerationContext.SINGLETON.getJavaCompiler();
        Class newClass = javaCompiler.loadFromJava(GenerationContext.SINGLETON.getClassLoader(), fqn, IOUtils.toString(srcFile));
        return newClass;
    }

    static <T> Class<T> generateAndCompile(T node, String templateFile, GenerationContext generationConfig, Context ctx) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException, ClassNotFoundException {
        String className = writeSourceFile(node, templateFile, generationConfig, ctx);
        String fqn = generationConfig.getPackageName() + "." + className;
        File file = new File(generationConfig.getPackageDirectory(), className + ".java");
        CachedCompiler javaCompiler = GenerationContext.SINGLETON.getJavaCompiler();
        String javaCode = GenerationContext.readText(file.getCanonicalPath());
        new Thread(() -> Generator.formatSource(file)).start();
        Class newClass = javaCompiler.loadFromJava(GenerationContext.SINGLETON.getClassLoader(), fqn, javaCode);
        return newClass;
    }

    static String writeSourceFile(Object node, String templateFile, GenerationContext generationConfig, Context ctx) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(generationConfig.getClassLoader());
        initVelocity();
        Thread.currentThread().setContextClassLoader(originalClassLoader);
//generates the actual class
        ctx.put(FunctionKeys.functionPackage.name(), generationConfig.getPackageName());
        String generatedClassName = (String) ctx.get(FunctionKeys.functionClass.name());
        generationConfig.getProxyClassMap().put(node, generatedClassName);
        Template template = null;
        try {
            template = Velocity.getTemplate(templateFile);
        } catch (Exception e) {
            System.out.println("failed to load template, setting threadcontext class loader");
            try {
                Thread.currentThread().setContextClassLoader(generationConfig.getClassLoader());
                template = Velocity.getTemplate(templateFile);
            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
        File outFile = new File(generationConfig.getPackageDirectory(), generatedClassName + ".java");
        try (FileWriter templateWriter = new FileWriter(outFile)) {
            template.merge(ctx, templateWriter);
            templateWriter.flush();
        }

        try {
            while (true) {
                if (outFile.exists() && outFile.length() > 1) {
                    break;
                } else {
                    Thread.sleep(1);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(FunctionGeneratorHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedClassName;
    }

    static void deleteGeneratedClass(GenerationContext generationConfig, String generatedClassName) {
        File outFile = new File(generationConfig.getPackageDirectory(), generatedClassName + ".java");
        if (!outFile.delete()) {
            System.out.println("unable to delete file:" + outFile.getAbsolutePath());
        }

    }

    static void initVelocity() throws RuntimeException {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

}
