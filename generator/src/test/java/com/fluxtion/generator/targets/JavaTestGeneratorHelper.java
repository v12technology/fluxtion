/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.generator.Generator;
import com.fluxtion.generator.compiler.SepCompiler;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import static com.fluxtion.generator.targets.JavaGeneratorNames.packageDefault;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;

/**
 *
 * @author Greg Higgins
 */
public interface JavaTestGeneratorHelper {

    public static void setupDefaultTestContext(String packageName, String className){
        GenerationContext.setupStaticContext(packageName, className, new File("target/generated-test-sources/java/"), new File("target/generated-test-sources/resources/"));
    }
    
    /**
     * 
     * @param packageName
     * @param className
     * @return 
     */
    public static SepCompilerConfig getTestSepCompileConfig(String packageName, String className){
        SepCompilerConfig cfg = new SepCompilerConfig();
        cfg.setOutputDirectory("target/generated-test-sources/java/");
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
    public static void generateAndCompile(SepCompilerConfig config) throws Exception{
        SepCompiler compiler = new SepCompiler();
        compiler.compile(config);
    }
    
    public static EventHandler generateAndInstantiate(SepCompilerConfig config) throws Exception{
        generateAndCompile(config);
        Class<EventHandler> resultProcessorClass = (Class<EventHandler>) Class.forName(config.getFqn());
        return resultProcessorClass.newInstance();
    }
    
    public static JavaClass generateClass(SEPConfig cfg, GenerationContext context) throws Exception {
        cfg.templateFile = "javaTemplate.vsl";
        Generator generator = new Generator();
        generator.templateSep(cfg);
        JavaDocBuilder builder = new JavaDocBuilder();
        File f = new File("target/generated-test-sources/java/");
        builder.addSourceTree(f);
        JavaClass genClass = builder.getClassByName(context.getPackageName() + "." + context.getSepClassName());
        //build inline version:
        return genClass;
    }
    
    public static JavaClass generateClass(SEPConfig cfg, String packageName, String className) throws Exception {
        cfg.templateFile = "javaTemplate.vsl";
        GenerationContext.setupStaticContext(packageName, className, new File("target/generated-test-sources/java/"), new File("target/generated-test-sources/resources/"));
        Generator generator = new Generator();
        generator.templateSep(cfg);
        JavaDocBuilder builder = new JavaDocBuilder();
        File f = new File("target/generated-test-sources/java/");
        builder.addSourceTree(f);
        JavaClass genClass = builder.getClassByName(packageName + "." + className);
        //build inline version:
        return genClass;
    }

    public static JavaClass generateClass(SEPConfig cfg, String className, boolean inLine, boolean dirtySupport) throws Exception {
        cfg.inlineEventHandling = inLine;
        cfg.supportDirtyFiltering = dirtySupport;
        return generateClass(cfg, packageDefault.name, className);
    }

    public static JavaClass generateClass(SEPConfig cfg, JavaGeneratorNames name, boolean dirtySupport) throws Exception {
        return generateClass(cfg, name.name, false, dirtySupport);
    }

    public static JavaClass generateClass(SEPConfig cfg, JavaGeneratorNames name) throws Exception {
        return generateClass(cfg, name.name, false, false);
    }

    public static JavaClass generateClassInline(SEPConfig cfg, JavaGeneratorNames name) throws Exception {
        return generateClass(cfg, name.name  + "_inline", true, false);
    }
    
    public static JavaClass generateClassInline(SEPConfig cfg, JavaGeneratorNames name, boolean dirtySupport) throws Exception {
        return generateClass(cfg, name.name  + "_inline", true, dirtySupport);
    }

    public static <T> T sepInstance(JavaGeneratorNames name) throws Exception {
        String fqn = packageDefault.name + "." + name.name;
        Class<T> clazzTest = (Class<T>) Class.forName(fqn);
        T newInstance = clazzTest.newInstance();
        return newInstance;
    }

    public static <T> T sepInstanceInline(JavaGeneratorNames name) throws Exception {
        String fqn = packageDefault.name + "." + name.name + "_inline";
        Class<T> clazzTest = (Class<T>) Class.forName(fqn);
        T newInstance = clazzTest.newInstance();
        return newInstance;
    }

    /**
     * Tests that the classes in the traceList are the same as thos in the
     * expected The trace list can be recorded using the traceEvent.
     *
     * @param traceList
     * @param expected
     */
    public static void testClassOrder(List<?> traceList, Class... expected) {
        List<Class> collect = traceList
                .stream()
                .map((obj) -> obj.getClass())
                .collect(Collectors.toList());
        Assert.assertThat(collect, IsIterableContainingInOrder.contains(expected));
    }
    
    public static void testClassOrder(List<?> traceList, List<?> traceListInline, Class... expected) {
        testClassOrder(traceList, expected);
        testClassOrder(traceListInline, expected);
    }
    
    public static void testTraceIdOrder(List<String> traceList, String... expectedTrace){
        Assert.assertThat(traceList, IsIterableContainingInOrder.contains(expectedTrace));
    }
    
    public static void testTraceIdContains(List<String> traceList, String... expectedTrace){
        Assert.assertThat(traceList, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedTrace));
    }
    
    public static void testTraceIdOrder(List<String> traceList, List<String> traceListInline, String... expectedTrace){
        testTraceIdOrder(traceList, expectedTrace);
        testTraceIdOrder(traceListInline, expectedTrace);
    }

    /**
     * test the presence of a public field in the sep.
     *
     * @param sep
     * @param fieldName
     */
    public static void testPublicField(Object sep, String fieldName) {
        Assert.assertNotNull(FieldUtils.getDeclaredField(sep.getClass(), fieldName));
    }
    public static void testPublicField(Object sep, Object sepInline, String fieldName) {
        testPublicField(sep, fieldName);
        testPublicField(sepInline, fieldName);
    }

}
