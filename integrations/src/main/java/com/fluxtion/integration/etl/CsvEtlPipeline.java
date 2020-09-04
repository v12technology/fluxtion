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

import com.fluxtion.ext.text.api.csv.RowProcessor;
import com.fluxtion.generator.compiler.OutputRegistry;
import lombok.Data;
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
public class CsvEtlPipeline {

    private String id;
    private transient RowProcessor csvProcessor;
    private CsvLoadDefinition defintion;

    public String toYaml() {
        Representer representer = new Representer() {
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
        return yaml.dumpAs(this, Tag.MAP, null);
    }

    public static CsvEtlPipeline loadPipeline(String yaml) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(OutputRegistry.INSTANCE.getClassLoader());
        CsvEtlPipeline pipeline = null;
        try {
            Yaml yamlParser = new Yaml();
            pipeline = yamlParser.loadAs(yaml, CsvEtlPipeline.class);
        } catch (Exception e) {
        }
        Thread.currentThread().setContextClassLoader(originalClassLoader);
        return pipeline;
    }

}
