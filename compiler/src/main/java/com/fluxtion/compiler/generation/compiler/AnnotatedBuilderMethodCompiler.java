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
package com.fluxtion.compiler.generation.compiler;

import com.fluxtion.runtime.annotations.builder.ClassProcessor;
import com.fluxtion.runtime.annotations.builder.Disabled;
import com.fluxtion.runtime.annotations.builder.SepBuilder;
import com.fluxtion.compiler.SEPConfig;
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
public class AnnotatedBuilderMethodCompiler implements ClassProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedBuilderMethodCompiler.class.getName());
    private File generatedDir;
    private File resourceDir;
    private File rootDir;

    @Override
    public void outputDirectories(File rootDir, File output, File resourceDir) {
        this.rootDir = rootDir;
        //default params do not work, set default here
        this.generatedDir = new File(rootDir, "src/main/java");
        this.resourceDir = resourceDir;
    }

    @Override
    public void process(URL classPath) {
        if (classPath == null) {
            LOGGER.warn("scan classpath is null, exiting AnnotatedBuilderMethodCompiler");
            return;
        }
        try {
            File fin = new File(classPath.toURI());
            LOGGER.debug("AnnotationCompiler scanning url:'{}' for SepBuilder annotations", fin);
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClasspath(fin)
                    .scan()) {
                ClassInfoList csvList = scanResult
                        .getClassesWithMethodAnnotation(SepBuilder.class.getCanonicalName())
                        .exclude(scanResult.getClassesWithAnnotation(Disabled.class.getCanonicalName()));
                for (ClassInfo csvClassInfo : csvList) {

                    csvClassInfo.getMethodInfo().filter((methodInfo) -> methodInfo.hasAnnotation(SepBuilder.class.getCanonicalName())
                            && !methodInfo.hasAnnotation(Disabled.class.getCanonicalName())).forEach((method) -> {
                        try {
                            LOGGER.info("sep builder method:" + method);
                            final Object newInstance = csvClassInfo.loadClass().getDeclaredConstructor().newInstance();

                            Consumer<SEPConfig> consumer = cfg -> {
                                try {
                                    method.loadClassAndGetMethod().invoke(newInstance, cfg);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    LOGGER.error("problem executing SepConfig builder method", ex);
                                }
                            };
                            AnnotationParameterValueList params = method.getAnnotationInfo(SepBuilder.class.getCanonicalName()).getParameterValues();
                            ClassProcessorDispatcher.DirectoryNames dirNames = ClassProcessorDispatcher.standardParamsHelper(params, rootDir, generatedDir, resourceDir);
                            boolean init = (params.get("initialise") == null || (Boolean) params.get("initialise"));
                            InProcessSepCompiler.sepInstance(consumer, dirNames.pkgName, params.get("name").toString(), dirNames.outDir, dirNames.resDir, init);
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
        LOGGER.debug("AnnotatedBuilderMethodCompiler completed");
    }

}
