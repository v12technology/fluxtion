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

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.ClassUtils;
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
public class CsvLoadDefinition {

    private List<Column> columns = new ArrayList<>();
    private List<DerivedColumn> derived = new ArrayList<>();
    /**
     * unique identifier
     */
    private String id;
    private String postRecordFunction;

    public CsvLoadDefinition addColumn(Column column) {
        columns.add(column);
        return this;
    }

    public CsvLoadDefinition addDerived(DerivedColumn column) {
        derived.add(column);
        return this;
    }

    public String className() {
        return ClassUtils.getShortCanonicalName(id);
    }

    public String packageName() {
        return ClassUtils.getPackageCanonicalName(id);
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
//        return yaml.dumpAsMap(this);
    }
    
    public static CsvLoadDefinition fromYaml(String yamlString){
        Yaml yamlParser = new Yaml();
        return yamlParser.loadAs(yamlString, CsvLoadDefinition.class);
        
    }
}
