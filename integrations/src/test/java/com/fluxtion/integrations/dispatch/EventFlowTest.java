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
package com.fluxtion.integrations.dispatch;

import com.fluxtion.ext.text.api.annotation.ColumnName;
import com.fluxtion.generator.compiler.InprocessSepCompiler.DirOptions;
import static com.fluxtion.integration.eventflow.sources.Builders.csvSource;
import java.io.IOException;
import static java.nio.file.Files.newBufferedReader;
import java.nio.file.Paths;
import lombok.Data;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class EventFlowTest extends TextInprocessTest {

    @Test
    public void simpleFlow() throws IOException {
        
        csvSource(Iris.class, newBufferedReader(Paths.get("src/test/data/iris.csv")), 
                pckName(), DirOptions.TEST_DIR_OUTPUT)
                .filter(i -> ((Iris)i).getName().startsWith("g"))
                .sink(System.out::println)
                .start()
                .stop();
    }
    
    @Data
    public static class Iris{
    
        @ColumnName("f_name")
        private String name;
        private int size;
    }
}
