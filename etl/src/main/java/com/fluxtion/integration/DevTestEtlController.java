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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import static com.fluxtion.builder.generation.GenerationContext.DEFAULT_CLASSLOADER;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class DevTestEtlController {

    @Autowired
    private Main main;

    @GetMapping("/dev-test/loadclass")
    public String classLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class<?> clazz = Class.forName("com.fluxtion.api.annotations.OnEvent");
        Class<?> clazz2 = Class.forName("com.fluxtion.api.annotations.OnEvent", true, DEFAULT_CLASSLOADER);
        final String info = "clazz:" + clazz.getName() + " loader:" + clazz.getClassLoader() + "\nclazz2:" + clazz2.getName() + " loader2:" + clazz2.getClassLoader();
        log.info(info);
        return info;
    }
    

    @GetMapping("/dev-test/buildsample")
    public CsvEtlPipeline buildSample() {
        String id = "org.greg.Data2";
        String yaml = ""
                + "id: org.greg.Data2\n"
                + "columns:\n"
                + "- {name: age, type: int}\n"
                + "- {name: lastName, type: String, function: 'return input.toString().toUpperCase();' }\n"
                + "derived:\n"
                + "- {name: halfAge, type: int, function: '"
                + "//some comments\n\n"
                + "return age / 2;'}\n"
                + "postRecordFunction: '//no-op demo callback\n'"
                + "";

        return  main.buildModel(yaml);
    }

}
