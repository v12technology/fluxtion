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
import java.io.IOException;
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
    private CsvEtlBuilder builder;

    @Override
    public void init() {
        log.info("init loading pipelines");
        pipelines = new HashMap<>();
        pipelineStore.getAllPipelines().forEach(c -> {
            String csvProcessorClassName = c.getCsvProcessorClassName();
            try {
                Class forName = Class.forName(csvProcessorClassName, true, OutputRegistry.INSTANCE.getClassLoader());
                Object newInstance = forName.getDeclaredConstructors()[0].newInstance();
                log.info("created rowprocessor id:{}, instance:{} rowProcessor:{}", c.getId(), newInstance, newInstance instanceof RowProcessor);
                c.setCsvProcessor((RowProcessor) newInstance);
                pipelines.put(c.getId(), c);
            } catch (ClassNotFoundException ex) {
                log.warn("ClassNotFoundException could not create csv processor cannot find decoder:'{}'", csvProcessorClassName);
                try {
                    log.info("trying to rebuild workfllow, missing etl processing classes");
                    builder.buildWorkFlow(c.getDefintion());
                    pipelines.put(c.getId(), c);
                } catch (IOException | ClassNotFoundException ex1) {
                    Logger.getLogger(PipelineRegistry.class.getName()).log(Level.SEVERE, null, ex1);
                }
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

}
