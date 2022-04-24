/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.generation.compiler.SepCompiler;
import com.fluxtion.compiler.generation.compiler.SepCompilerConfig;
import com.fluxtion.runtime.StaticEventProcessor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Greg Higgins
 */
public interface JavaTestGeneratorHelper {
    
    static final String TEST_SOURCE_GEN_DIR = "target/generated-test-sources/fluxtion/";

    static void setupDefaultTestContext(String packageName, String className){
        GenerationContext.setupStaticContext(packageName, className, new File(TEST_SOURCE_GEN_DIR), new File("target/generated-test-sources/resources/"));
    }
    
    /**
     * 
     * @param packageName
     * @param className
     * @return 
     */
    static SepCompilerConfig getTestSepCompileConfig(String packageName, String className){
        SepCompilerConfig cfg = new SepCompilerConfig();
        cfg.setOutputDirectory(TEST_SOURCE_GEN_DIR);
        cfg.setResourcesOutputDirectory("target/generated-test-sources/resources/");
        cfg.setPackageName(packageName);
        cfg.setClassName(className);
        cfg.setGenerateDescription(false);
        return cfg;
    }
    
    /**
     *
     * @param config
     * @throws Exception
     */
    static Class generateAndCompile(SepCompilerConfig config) throws Exception{
        SepCompiler compiler = new SepCompiler();
        return compiler.compile(config);
    }
    
    static StaticEventProcessor generateAndInstantiate(SepCompilerConfig config) throws Exception{
        Class<StaticEventProcessor> resultProcessorClass = generateAndCompile(config);
        return resultProcessorClass.newInstance();
    }



    /**
     * Tests that the classes in the traceList are the same as thos in the
     * expected The trace list can be recorded using the traceEvent.
     *
     * @param traceList
     * @param expected
     */
    static void testClassOrder(List<?> traceList, Class... expected) {
        List<Class> collect = traceList
                .stream()
                .map((obj) -> obj.getClass())
                .collect(Collectors.toList());
        Assert.assertThat(collect, IsIterableContainingInOrder.contains(expected));
    }
    
    static void testClassOrder(List<?> traceList, List<?> traceListInline, Class... expected) {
        testClassOrder(traceList, expected);
        testClassOrder(traceListInline, expected);
    }
    
    static void testTraceIdOrder(List<String> traceList, String... expectedTrace){
        Assert.assertThat(traceList, IsIterableContainingInOrder.contains(expectedTrace));
    }
    
    static void testTraceIdContains(List<String> traceList, String... expectedTrace){
        Assert.assertThat(traceList, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedTrace));
    }
    
    static void testTraceIdOrder(List<String> traceList, List<String> traceListInline, String... expectedTrace){
        testTraceIdOrder(traceList, expectedTrace);
        testTraceIdOrder(traceListInline, expectedTrace);
    }

    /**
     * test the presence of a public field in the sep.
     *
     * @param sep
     * @param fieldName
     */
    static void testPublicField(Object sep, String fieldName) {
        Assert.assertNotNull(FieldUtils.getDeclaredField(sep.getClass(), fieldName));
    }
    static void testPublicField(Object sep, Object sepInline, String fieldName) {
        testPublicField(sep, fieldName);
        testPublicField(sepInline, fieldName);
    }

}
