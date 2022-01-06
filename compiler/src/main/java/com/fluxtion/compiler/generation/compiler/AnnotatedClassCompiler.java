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
import com.fluxtion.runtime.annotations.builder.SepInstance;
import com.google.auto.service.AutoService;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregp
 */
@AutoService(ClassProcessor.class)
public class AnnotatedClassCompiler implements ClassProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedClassCompiler.class.getName());
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
            LOGGER.warn("scan classpath is null, exiting AnnotatedClassCompiler");
            return;
        }
        try {
            File fin = new File(classPath.toURI());
            LOGGER.debug("AnnotationCompiler scanning url:'{}' for SepInstance annotations", fin);
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClasspath(fin)
                    .scan()) {
                ClassInfoList csvList = scanResult
                        .getClassesWithAnnotation(SepInstance.class.getCanonicalName())
                        .exclude(scanResult.getClassesWithAnnotation(Disabled.class.getCanonicalName()));
                for (ClassInfo csvClassInfo : csvList) {
                    final Class<?> csvClass = csvClassInfo.loadClass();
                    Object newInstance = csvClass.getDeclaredConstructor().newInstance();
                    AnnotationInfo annotationInfo = csvClassInfo.getAnnotationInfo(Disabled.class.getCanonicalName());
                    if (annotationInfo == null) {
                        LOGGER.info("Adding instance to Sep class:" + csvClass.getCanonicalName());
                        AnnotationParameterValueList params = csvClassInfo.getAnnotationInfo(SepInstance.class.getCanonicalName()).getParameterValues();
                        boolean init = (params.get("initialise") == null || (Boolean) params.get("initialise"));
                        ClassProcessorDispatcher.DirectoryNames dirNames = ClassProcessorDispatcher.standardParamsHelper(params, rootDir, generatedDir, resourceDir);
                        InProcessSepCompiler.sepInstance((cfg) -> {
                            cfg.addPublicNode(newInstance, "processor");
                            boolean supportDirtyFiltering = true;
                            if (params.get("supportDirtyFiltering") != null) {
                                supportDirtyFiltering = (Boolean) params.get("supportDirtyFiltering");
                            } 
                            cfg.supportDirtyFiltering = supportDirtyFiltering;
                        }, dirNames.pkgName, params.get("name").toString(), dirNames.outDir, dirNames.resDir, init);

                    } else {
                        LOGGER.info("disabled Fluxtion SEP node generation for:" + csvClass.getCanonicalName());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("problem creating static event processor, node should have default constructor", ex);
            }
        } catch (URISyntaxException ex) {
            LOGGER.error("problem generating static event processor", ex);
        }
        LOGGER.debug("AnnotatedClassCompiler completed");
    }
}

