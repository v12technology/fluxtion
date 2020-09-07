/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.integration.etl;

import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.generator.compiler.OutputRegistry;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
@Data
public class PipelineRegistry implements Lifecycle {
    
    private PipelineStore pipelineStore;
    private Map<String, CsvEtlPipeline> pipelines;

    @Override
    public void init() {
        log.info("init loading pipelines");
        pipelines = new HashMap<>();
        pipelineStore.getAllPipelines().forEach(c ->{
            try {
                String csvProcessorClassName = c.getCsvProcessorClassName();
                Class<RowProcessor> forName = (Class<RowProcessor>) Class.forName(csvProcessorClassName, true, OutputRegistry.INSTANCE.getClassLoader());
                RowProcessor newInstance = (RowProcessor) forName.getDeclaredConstructors()[0].newInstance();
                log.info("created rowprocessor id:{}, instance:{}", c.getId(), newInstance);
                c.setCsvProcessor(newInstance);
                pipelines.put(c.getId(), c);
            } catch (ClassNotFoundException ex) {
                log.warn("could not create csv processor", ex);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(PipelineRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        });
    }
    
    void registerModel(CsvEtlPipeline pipeline) {
        log.info("registering model id:'{}'", pipeline.getId());
//        pipelineStore.writePipelines(List.of(pipeline));
        pipelines.put(pipeline.getId(), pipeline);
        pipelineStore.writePipelines(new ArrayList<>(pipelines.values()));
    }


    @Override
    public void tearDown() {
        log.info("stopping");
    }

//    public void loadModels() {
//        //what is the classpath?!!
//        try (ScanResult scanResult = new ClassGraph()
//                .enableClassInfo()
//                .overrideClassLoaders(OutputRegistry.INSTANCE.getClassLoader())
//                .scan()) {
//            ClassInfoList rowProcessorInfoList = scanResult.getClassesImplementing(RowProcessor.class.getCanonicalName());
//            rowProcessorInfoList.forEach((ClassInfo t) -> {
//                System.out.println("loaded rowprocessor:" + t);
//            });
//        }
//    }
//
//    private void addMarshaller(ClassInfo info) {
//        try {
//            Class<RowProcessor> marshallerClass = info.loadClass(RowProcessor.class);
//            RowProcessor rowProcessor = marshallerClass.getDeclaredConstructor().newInstance();
//            log.info("registering marshaller:{} for type:{}", marshallerClass.getName(), rowProcessor.eventClass());
////            marshallerMap.put(rowProcessor.eventClass(), rowProcessor);
//        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            log.warn("unable to load RowProcessor", ex);
//        }
//
//    }
}
