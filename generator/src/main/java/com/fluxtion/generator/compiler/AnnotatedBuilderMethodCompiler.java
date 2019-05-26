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

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.compiler.ClassProcessorDispatcher.standardParamsHelper;
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

    private Logger LOGGER = LoggerFactory.getLogger(AnnotatedBuilderMethodCompiler.class.getName());
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

                    csvClassInfo.getMethodInfo().filter((methodInfo) -> {
                        return methodInfo.hasAnnotation(SepBuilder.class.getCanonicalName())
                                && !methodInfo.hasAnnotation(Disabled.class.getCanonicalName());
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
                            ClassProcessorDispatcher.DirectoryNames dirNames = standardParamsHelper(params, rootDir, generatedDir, resourceDir);
                            InprocessSepCompiler.sepInstance(consumer, dirNames.pkgName, params.get("name").toString(), dirNames.outDir, dirNames.resDir, false);
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
        LOGGER.info("AnnotatedBuilderMethodCompiler completed");
    }

}
