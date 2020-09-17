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
import java.beans.Transient;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    private String csvProcessorClassName;

    @Transient
    public RowProcessor getCsvProcessor() {
        try {
            return (RowProcessor) csvProcessor.getClass().getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(CsvEtlPipeline.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("cnnot buuld row processor", ex);
        }
    }

    public String toYaml() {
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
        return yaml.dumpAs(this, Tag.MAP, null);
    }

    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        defintion.getColumns().forEach(c -> headers.add("\"" + c.getName() + "\""));
        defintion.getColumns().stream().map(Column::getName).collect(Collectors.joining(","));
        return headers;
    }

    public String getHeadersAsCsv() {
        return defintion.getColumns().stream().map(Column::getName).collect(Collectors.joining(","));
    }

}
