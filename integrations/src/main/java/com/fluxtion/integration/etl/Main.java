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
import java.io.Writer;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private MarshallerRegistry marshallerRegistry;

    public void deletePipeline(String pipelineId) {
        pipelineRegistry.deletePipeline(pipelineId);
    }

    @PostConstruct
    public Main start() {
        log.info("starting etl");
        //create instances
        fileConfig = new FileConfig("src/test/fluxtion-etl/test1");
        pipelineFileStore = new PipelineFileStore();
        pipelineRegistry = new PipelineRegistry();
        etlBuilder = new CsvEtlBuilder();
        controller = new PipelineController();
        marshallerRegistry = new MarshallerRegistry();
        //set config
        //set references
        pipelineFileStore.setFileConfig(fileConfig);
        pipelineRegistry.setPipelineStore(pipelineFileStore);
        pipelineRegistry.setBuilder(etlBuilder);
        controller.setPipelineRegistry(pipelineRegistry);
        controller.setBuilder(etlBuilder);
        controller.setMarshallerRegistry(marshallerRegistry);
        //init
        fileConfig.init();
        pipelineFileStore.init();
        pipelineRegistry.init();
        marshallerRegistry.init();
        etlBuilder.init();
        log.info("started etl");
        return this;
    }

    public CsvEtlPipeline buildModel(String yaml) {
        return controller.buildModel(yaml);
    }

    public void executePipeline(String id, Reader reader) {
        controller.executePipeline(id, reader);
    }

//    public void executePipeline(String id, Reader reader, Writer out) {
//        controller.executePipeline(id, reader, out);
//    }

    public void executePipeline(String id, Reader reader, Writer out, Writer outCsv, Writer errorLog) {
        controller.executePipeline(id, reader, out, outCsv, errorLog);
    }
    
    public CsvEtlPipeline getModel(String id){
        CsvEtlPipeline pipeline = pipelineRegistry.getPipelines().get(id);
        pipeline = pipeline==null?pipelineRegistry.getPipelineFailures().get(id):pipeline;
        return pipeline;
    }
    
    public Map<String, CsvEtlPipeline> listModels(){
        return pipelineRegistry.getPipelines();
    }
    
    public Map<String, CsvEtlPipeline> listFailedModels(){
        return pipelineRegistry.getPipelineFailures();
    }
    
    @PreDestroy
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
