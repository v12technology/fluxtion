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
import com.fluxtion.integration.eventflow.EventFlow;
import com.fluxtion.integration.eventflow.sources.DelimitedSource;
import java.io.IOException;
import java.io.Reader;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
@Log4j2
public class PipelineController implements Lifecycle {

    private CsvEtlBuilder builder;
    private PipelineRegistry pipelineRegistry;

    public CsvEtlPipeline buildModel(String yaml) {
        CsvEtlPipeline pipeline = null;
        try {
            pipeline = builder.buildWorkFlow(yaml);
            pipelineRegistry.registerModel(pipeline);
        } catch (IOException | ClassNotFoundException ex) {
            log.warn("unable to build pipeliine", ex);
        }
        return pipeline;
    }

    public void executePipeline(String id, Reader reader) {
        CsvEtlPipeline pipeline = pipelineRegistry.getPipelines().get(id);
        if (pipeline != null) {
            EventFlow.flow(new DelimitedSource(pipeline.getCsvProcessor(), reader, "limitFromCsv"))
                    .first(System.out::println)
                    .start();
        }
    }

    @Override
    public void init() {
        log.info("starting");
    }

    @Override
    public void tearDown() {
        log.info("stopping");
    }
}
