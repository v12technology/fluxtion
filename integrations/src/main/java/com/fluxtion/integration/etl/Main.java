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

import java.io.File;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
public class Main {
    public static final String pipelineDefDir = "fluxtion/pipelines/";
    public static final String workDir = "fluxtion/work/";
    public static final String cacheDir = "fluxtion/cache/";
    public static void main(String[] args) throws IOException {
        System.setProperty("fluxtion.cacheDirectory", "fluxtion/cache/");
        FileUtils.forceMkdir(new File(pipelineDefDir));
        FileUtils.forceMkdir(new File(workDir));
        FileUtils.forceMkdir(new File(cacheDir));
    }
    
    private void init(){
        
    }
    
}
