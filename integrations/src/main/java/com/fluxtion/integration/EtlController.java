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
package com.fluxtion.integration;

import com.fluxtion.integration.etl.CsvEtlPipeline;
import com.fluxtion.integration.etl.Main;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import static com.fluxtion.builder.generation.GenerationContext.DEFAULT_CLASSLOADER;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class EtlController {

    @Autowired
    private Main main;
    
    @GetMapping("/pipeline/list")
    public Collection<CsvEtlPipeline> getAllPipelines() {
        return main.listModels().values();
    }

    @PostConstruct
    public void init() {
        log.info("starting");
//        main.start();
    }

    @PreDestroy
    public void teardown() {
        log.info("stopping");
//        main.stop();
    }
}
