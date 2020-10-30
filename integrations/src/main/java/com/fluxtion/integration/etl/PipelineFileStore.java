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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@Data
@Log4j2
public class PipelineFileStore implements PipelineStore, Lifecycle {

    private FileConfig fileConfig;
    private final String pipelineFile = "pipelines.yml";

    @Override
    public List<CsvEtlPipeline> getAllPipelines() {
        List<CsvEtlPipeline> list = new ArrayList<>();
        try {
            log.info("getAllPipelines");
            File pipeFile = new File(fileConfig.getResDirFile(), pipelineFile);
            if(pipeFile.createNewFile()){
                log.info("created new pipeline file:{}", pipeFile);
            }
            try (Reader reader = new FileReader(pipeFile)) {
                Yaml yaml = new Yaml();
                Iterable<Object> loadAll = yaml.loadAll(reader);
                for (Iterator<Object> iterator = yaml.loadAll(reader).iterator(); iterator.hasNext();) {
                    CsvEtlPipeline next = (CsvEtlPipeline) iterator.next();
                    list.add(next);
                }
            } catch (FileNotFoundException ex) {
                log.info("no pipeline file found", ex);
            } catch (IOException ex) {
                log.warn("problem while reading pipeline file", ex);
            }
            log.info("loaded:{}", list);
        } catch (IOException ex) {
            log.warn("could not create pipeline file", ex);
        }
        return list;
    }

    @Override
    public boolean writePipelines(List<CsvEtlPipeline> pipelines) {
        File pipeFile = new File(fileConfig.getResDirFile(), pipelineFile);
        log.info("write pipeline models to file:'{}'", pipeFile.toPath());
        try (Writer writer = new FileWriter(pipeFile)) {
            Representer representer = new Representer() {
                @Override
                protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                    // if value of property is null, ignore it.
                    if (propertyValue == null) {
                        return null;
                    } else {
                        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                    }
                }
            };

            Yaml yaml = new Yaml(representer);
            yaml.dumpAll(pipelines.iterator(), writer);
        } catch (IOException ex) {
        }
        return false;
    }

    @Override
    public void init() {
        File pipeFile = new File(fileConfig.getResDirFile(), pipelineFile);
        log.info("reading pipeline models from file:'{}'", pipeFile.toPath());
    }

    @Override
    public void tearDown() {
        log.info("stopping");
    }

}
