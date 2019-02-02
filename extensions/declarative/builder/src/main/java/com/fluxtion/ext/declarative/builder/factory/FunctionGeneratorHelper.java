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
package com.fluxtion.ext.declarative.builder.factory;

import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.generator.Generator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.openhft.compiler.CachedCompiler;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.Callback;
import net.vidageek.mirror.dsl.Mirror;
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

    public static int intFromMap(Map<String, ?> configMap, String key, int defualtValue) {
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

    public static <T> Method numericGetMethod(T instance, Function<T, ? super Number> f) {
        return methodFromLambda(instance, f);
    }

    public static <T> Method numericGetMethod(Class<T> instance, Function<T, ? super Number> f) {
        return methodFromLambda(instance, f);
    }

    public static <T> Method numericSetMethod(T instance, BiConsumer<T, ? super Byte> targetFunction) {
        Method[] result = new Method[1];
        targetFunction.accept(generateInterceptor(instance, result), (byte) 0);
        return result[0];
    }

    public static <T> Method numericSetMethod(Class<T> instance, BiConsumer<T, ? super Byte> targetFunction) {
        Method[] result = new Method[1];
        try {
            ((BiConsumer<T, ? super Integer>) targetFunction).accept(generateInterceptorByClass(instance, result), 0);
        } catch (Exception e) {
        }
        try {
            ((BiConsumer<T, ? super Double>) targetFunction).accept(generateInterceptorByClass(instance, result), 0.0);
        } catch (Exception e) {
        }
        try {
            ((BiConsumer<T, ? super Long>) targetFunction).accept(generateInterceptorByClass(instance, result), 0l);
        } catch (Exception e) {
        }
        try {
            ((BiConsumer<T, ? super Short>) targetFunction).accept(generateInterceptorByClass(instance, result), (short) 0);
        } catch (Exception e) {
        }
        try {
            ((BiConsumer<T, ? super Float>) targetFunction).accept(generateInterceptorByClass(instance, result), 0.0f);
        } catch (Exception e) {
        }
        try {
            targetFunction.accept(generateInterceptorByClass(instance, result), (byte) 0);
        } catch (Exception e) {
        }
        return result[0];
    }

    public static <T> Method setCharMethod(T instance, BiConsumer<T, ? super Character> targetFunction) {
        Method[] result = new Method[1];
        targetFunction.accept(generateInterceptor(instance, result), (char) 0);
        return result[0];
    }

    public static <T> Method methodFromLambda(T instance, Function<T, ?> f) {
        Method[] result = new Method[1];
        f.apply(generateInterceptor(instance, result));
        return result[0];
    }

    public static <T> Method methodFromLambda(T target, BiConsumer<T, ?> targetFunction) {
        return methodFromLambda((Class<T>) target.getClass(), targetFunction);
    }

    public static <T> Method methodFromLambda(Class<T> target, BiConsumer<T, ?> targetFunction) {
        Method[] result = new Method[1];
        targetFunction.accept(generateInterceptorByClass(target, result), null);
        return result[0];

    }

    public static <T> Method numericMethodFromLambda(Class<T> instance, BiConsumer<T, ? super Byte> targetFunction) {
        Method[] result = new Method[1];
        targetFunction.accept(generateInterceptorByClass(instance, result), (byte) 0);
        return result[0];
    }

    public static <T> Method methodFromLambda(Class<T> instance, Function<T, ?> f) {
        Method[] result = new Method[1];
        f.apply(generateInterceptorByClass(instance, result));
        return result[0];
    }

    static <T> T generateInterceptor(T instance, Method[] result) {
        return generateInterceptorByClass((Class<T>) instance.getClass(), result);
    }

    static <T> T generateInterceptorByClass(Class<T> instance, Method[] result) {
        final MethodInterceptor interceptor = (Object obj, Method method, Object[] args, MethodProxy proxy) -> {
            result[0] = method;
            return null;
        };
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance);
        enhancer.setCallbackType(interceptor.getClass());

        final Class<?> proxyClass = enhancer.createClass();
        Enhancer.registerCallbacks(proxyClass, new Callback[]{interceptor});

        T proxy = (T) new Mirror().on(proxyClass).invoke().constructor().bypasser();
        return proxy;
    }

//    public static <T> Class<T> generateAndCompileClass(Class class1, String templateFile, GenerationContext generationConfig, Context ctx) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException, ClassNotFoundException {
//        String className = generationConfig.getPackageName() + "." + (String) ctx.get(FunctionKeys.functionClass.name());
//        Class newClass = null;
//        initVelocity();
//        ctx.put(FunctionKeys.functionPackage.name(), generationConfig.getPackageName());
//        Template template = Velocity.getTemplate(templateFile);
//        Writer writer = new StringWriter();
//        template.merge(ctx, writer);
//        newClass = CACHED_COMPILER.loadFromJava(className, writer.toString());
//        return newClass;
//    }
    public static <T> Class<T> generateAndCompile(T node, String templateFile, GenerationContext generationConfig, Context ctx) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException, ClassNotFoundException {
        String className = writeSourceFile(node, templateFile, generationConfig, ctx);
        String fqn = generationConfig.getPackageName() + "." + className;
        File file = new File(generationConfig.getPackageDirectory(), className + ".java");
        CachedCompiler javaCompiler = GenerationContext.SINGLETON.getJavaCompiler();
        String javaCode = GenerationContext.readText(file.getCanonicalPath());
        Executors.newCachedThreadPool().submit(() ->{
            Generator.formatSource(file);
        });
        Class newClass = javaCompiler.loadFromJava(GenerationContext.SINGLETON.getClassLoader(), fqn, javaCode);
        return newClass;
    }

    public static String writeSourceFile(Object node, String templateFile, GenerationContext generationConfig, Context ctx) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException {
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

    public static void deleteGeneratedClass(GenerationContext generationConfig, String generatedClassName) {
        File outFile = new File(generationConfig.getPackageDirectory(), generatedClassName + ".java");
        if (!outFile.delete()) {
            System.out.println("unable to delete file:" + outFile.getAbsolutePath());
        }

    }

    public static void initVelocity() throws RuntimeException {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

}
