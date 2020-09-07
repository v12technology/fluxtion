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

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
@Service
public class Main {

    public static void main(String[] args) throws IOException {
        new Main().start();
    }

    private PipelineController controller;
    private CsvEtlBuilder etlBuilder;
    private FileConfig fileConfig;
    private PipelineFileStore pipelineFileStore;
    private PipelineRegistry pipelineRegistry;

    public Main start() {
        //create instances
        fileConfig = new FileConfig("src/test/fluxtion-etl/test1");
        pipelineFileStore = new PipelineFileStore();
        pipelineRegistry = new PipelineRegistry();
        etlBuilder = new CsvEtlBuilder();
        controller = new PipelineController();
        //set config
        //set references
        pipelineFileStore.setFileConfig(fileConfig);
        pipelineRegistry.setPipelineStore(pipelineFileStore);
        controller.setPipelineRegistry(pipelineRegistry);
        controller.setBuilder(etlBuilder);
        //init
        fileConfig.init();
        pipelineFileStore.init();
        pipelineRegistry.init();
        etlBuilder.init();
        return this;
    }

    public CsvEtlPipeline buildModel(String yaml) {
        return controller.buildModel(yaml);
    }

    public void executePipeline(String id, Reader reader) {
        controller.executePipeline(id, reader);
    }
    
    public CsvEtlPipeline getModel(String id){
        return pipelineRegistry.getPipelines().get(id);
    }
    
    public Map<String, CsvEtlPipeline> listModels(){
        return pipelineRegistry.getPipelines();
    }
    
    public Main stop() {
        log.info("stopping etl");
        controller.tearDown();
        etlBuilder.tearDown();
        pipelineRegistry.tearDown();
        pipelineFileStore.tearDown();
        fileConfig.tearDown();
        log.info("stopped etl");
        return this;
    }

}
