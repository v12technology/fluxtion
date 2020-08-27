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

import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FileDispatchTest extends BaseSepInprocessTest {
    
    @Test
    @Ignore
    public void geneerateMarshaller(){
        CsvToBeanBuilder.nameSpace("com.fluxtion.integrations.dispatch")
                .dirOption(InprocessSepCompiler.DirOptions.TEST_DIR_OUTPUT)
                .builder(DataEvent.class, 1)
                .reuseTarget(false)
                .build();
    }

    @Data
    public static class DataEvent {

        private int id;
        private String name;
    }

}
