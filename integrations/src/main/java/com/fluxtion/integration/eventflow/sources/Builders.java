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
package com.fluxtion.integration.eventflow.sources;

import com.fluxtion.ext.text.api.csv.RowProcessor;
import static com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder.buildRowProcessor;
import com.fluxtion.generator.compiler.DirOptions;
import com.fluxtion.integration.eventflow.EventFlow;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class Builders {
    
    public static EventFlow csvSource(Class inputClass, File file) throws IOException{
        return csvSource(inputClass, Files.newBufferedReader(file.toPath()));
    }
    
    public static EventFlow csvSource(Class inputClass, Reader reader){
        RowProcessor rowProcessor = buildRowProcessor(inputClass, "com.fluxtion.csvmarshaller.autogen");
        return EventFlow.flow(new DelimitedSource(rowProcessor, reader, ""));
    }
    
    public static EventFlow csvSource(Class inputClass, Reader reader, String pckgName, DirOptions dirOptions){        
        RowProcessor rowProcessor = buildRowProcessor(inputClass, pckgName, dirOptions);
        return EventFlow.flow(new DelimitedSource(rowProcessor, reader, ""));
    }
    
}
