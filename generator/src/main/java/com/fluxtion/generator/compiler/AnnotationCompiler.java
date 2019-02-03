/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.compiler;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.google.auto.service.AutoService;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregp
 */
@AutoService(ClassProcessor.class)
public class AnnotationCompiler implements ClassProcessor {

    private Logger LOGGER = LoggerFactory.getLogger(AnnotationCompiler.class.getName());
    private File generatedDir;
    private File resourceDir;
    private File rootDir;

    @Override
    public void outputDirectories(File rootDir, File output, File resourceDir) {
        this.rootDir = rootDir;
        this.generatedDir = output;
        this.resourceDir = resourceDir;
    }

    @Override
    public void process(URL classPath) {
        if(classPath == null ){
            LOGGER.warn("scan classpath is null, exiting AnnotationCompiler");
            return;
        }
        try {
            File fin = new File(classPath.toURI());
            LOGGER.info("AnnotationCompiler scanning url:'{}' for SepBuilder annotations", fin);
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClasspath(fin)
                    .scan()) {
                ClassInfoList csvList = scanResult
                        .getClassesWithMethodAnnotation(SepBuilder.class.getCanonicalName())
                        .exclude(scanResult.getClassesWithAnnotation(Disabled.class.getCanonicalName()))
                        .exclude(scanResult.getClassesWithMethodAnnotation(Disabled.class.getCanonicalName()));
                for (ClassInfo csvClassInfo : csvList) {

                    csvClassInfo.getMethodInfo().filter((methodInfo) -> {
                        return methodInfo.hasAnnotation(SepBuilder.class.getCanonicalName());
                    }).forEach((method) -> {
                        try {
                            LOGGER.info("sep builder method:" + method);
                            final Object newInstance = csvClassInfo.loadClass().newInstance();

                            Consumer<SEPConfig> consumer = new Consumer<SEPConfig>() {
                                @Override
                                public void accept(SEPConfig cfg) {
                                    try {
                                        method.loadClassAndGetMethod().invoke(newInstance, cfg);
                                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                        LOGGER.error("problem executing SepConfig builder method", ex);
                                    }
                                }
                            };
                            AnnotationParameterValueList params = method.getAnnotationInfo(SepBuilder.class.getCanonicalName()).getParameterValues();
                            String outDir = generatedDir.getCanonicalPath();
                            String resDir = resourceDir.getCanonicalPath();
                            if( params.get("outputDir")!=null){
                                outDir = rootDir.getCanonicalPath() + "/" + (params.get("outputDir").toString());
                            }
                            if( params.get("resourceDir")!=null){
                                outDir = rootDir.getCanonicalPath() + "/" + (params.get("resourceDir").toString());
                            }
                            InprocessSepCompiler.sepInstance(consumer, params.get("packageName").toString(), params.get("name").toString(), outDir, resDir, false);
                        } catch (Exception ex) {
                            LOGGER.error("problem creating class containing SepConfig builder method, should have default constructor", ex);
                        }
                    });
                }
            } catch (Exception ex) {
                LOGGER.error("problem generating static event processor", ex);
            }

        } catch (URISyntaxException ex) {
            LOGGER.error("problem generating static event processor", ex);
        }
        LOGGER.info("AnnotationCompiler completed");
    }

}
