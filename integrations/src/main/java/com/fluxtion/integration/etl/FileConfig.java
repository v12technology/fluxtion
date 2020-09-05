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
import com.fluxtion.generator.compiler.OutputRegistry;
import java.io.File;
import java.io.IOException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

/**
 * Config for file system interactions.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Log4j2
@Data
public class FileConfig implements Lifecycle {

    private final String rootDir;
    private final String pipelineDefDir;
    private final String workDir;
    private final String cacheDir;
    private final File cacheDirFile;
    private final File workDirFile;
    private final File pipelineDirFile;

    public FileConfig() {
        this(".");
    }

    public FileConfig(String rootDir) {
        this.rootDir = rootDir;
        cacheDir = rootDir + "/cache";
        workDir = rootDir + "/work";
        pipelineDefDir = rootDir + "/pipelines";
        pipelineDirFile = new File(pipelineDefDir);
        workDirFile = new File(workDir);
        cacheDirFile = new File(cacheDir);
    }

    @Override
    public void init() {
        try {
            FileUtils.forceMkdir(pipelineDirFile);
            FileUtils.forceMkdir(workDirFile);
            FileUtils.forceMkdir(cacheDirFile);
            System.setProperty("fluxtion.cacheDirectory", cacheDir);
            OutputRegistry.INSTANCE.update();
        } catch (IOException ex) {
            log.error("could not start, problem making directories", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void tearDown() {
    }

    public File getGenDirFile() {
        return OutputRegistry.INSTANCE.getGenDirFile();
    }

    public File getResDirFile() {
        return OutputRegistry.INSTANCE.getResDirFile();
    }

    public File getClassesDirFile() {
        return OutputRegistry.INSTANCE.getClassesDirFile();
    }

}
