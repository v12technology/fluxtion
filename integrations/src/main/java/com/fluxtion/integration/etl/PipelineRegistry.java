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
    private Map<String, CsvEtlPipeline> pipelineFailures;
    private CsvEtlBuilder builder;

    void deletePipeline(String pipelineId) {
        log.info("deleting model id:'{}'", pipelineId);
        pipelines.remove(pipelineId);
        pipelineFailures.remove(pipelineId);
        writePipelines();
    }

    @Override
    public void init() {
        log.info("init loading pipelines");
        pipelines = new HashMap<>();
        pipelineFailures = new HashMap<>();
        pipelineStore.getAllPipelines().forEach(c -> {
            String csvProcessorClassName = c.getCsvProcessorClassName();
            if (csvProcessorClassName == null) {
                log.info("no csvProcessorClassName registered");
                tryRebuild(c);
            } else {
                try {
                    Class forName = Class.forName(csvProcessorClassName, true, OutputRegistry.INSTANCE.getClassLoader());
                    Object newInstance = forName.getDeclaredConstructors()[0].newInstance();
                    log.info("created rowprocessor id:{}, instance:{} rowProcessor:{}", c.getId(), newInstance, newInstance instanceof RowProcessor);
                    c.setCsvProcessor((RowProcessor) newInstance);
                    pipelines.put(c.getId(), c);
                } catch (ClassNotFoundException ex) {
                    log.warn("ClassNotFoundException could not create csv processor cannot find decoder:'{}'", csvProcessorClassName);
                    tryRebuild(c);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    log.error("Cannot instantiate pipeline", ex);
                    registerFailedModel(c);
                }
            }
        });
    }

    private void tryRebuild(CsvEtlPipeline c) {
        try {
            log.info("trying to rebuild CsvEtlPipeline ");
            final CsvEtlPipeline pipeline = builder.buildWorkFlow(c.getDefintion());
            registerModel(pipeline);
        } catch (Throwable ex1) {
            log.error("Cannot generate pipeline", ex1);
            registerFailedModel(c);
        }
    }

    void registerModel(CsvEtlPipeline pipeline) {
        log.info("registering model id:'{}'", pipeline.getId());
//        pipelineStore.writePipelines(List.of(pipeline));
        pipelines.put(pipeline.getId(), pipeline);
        pipelineFailures.remove(pipeline.getId());
//        pipelineStore.writePipelines(new ArrayList<>(pipelines.values()));
        writePipelines();
    }

    void registerFailedModel(CsvEtlPipeline pipeline) {
        log.info("registering failed model id:'{}'", pipeline.getId());
        pipelines.remove(pipeline.getId());
        pipelineFailures.put(pipeline.getId(), pipeline);
        writePipelines();
    }

    private void writePipelines() {
        final List<CsvEtlPipeline> pipelineList = new ArrayList<>(pipelines.values());
        pipelineList.addAll(pipelineFailures.values());
        pipelineStore.writePipelines(pipelineList);
    }

    @Override
    public void tearDown() {
        log.info("stopping");
    }

}
