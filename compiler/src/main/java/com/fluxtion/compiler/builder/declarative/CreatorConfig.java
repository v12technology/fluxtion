/*
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.compiler.builder.declarative;

import com.fluxtion.compiler.builder.generation.GenerationContext;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author gregp
 */
@Data
@NoArgsConstructor
public class CreatorConfig {

    private String outputSepConfigClass;
    private String outputPackage;
    private String auditorClass;
    private List<Node> nodes;
    private List<EventDefinition> events;
    private String processorId;

    public String getSepCfgShortClassName() {
        return ClassUtils.getShortCanonicalName(outputSepConfigClass);
    }

    public String getSepCfgPackageName() {
        return ClassUtils.getPackageCanonicalName(outputSepConfigClass);
    }

    public void addNode(Node node) {
        getNodes().add(cleanNode(node));
    }

    private Node cleanNode(Node node) {
        try {
            GenerationContext.SINGLETON.getClassLoader().loadClass(node.getType());
        } catch (ClassNotFoundException ex) {
            node.setType(outputPackage + "." + node.getClassName());
        }
        return node;
    }

    private EventDefinition cleanEvent(EventDefinition node) {
        try {
            GenerationContext.SINGLETON.getClassLoader().loadClass(node.getType());
        } catch (ClassNotFoundException ex) {
            node.setType(outputPackage + "." + node.getClassName());
        }
        return node;
    }

    public void validateConfig() {
        nodes.stream().forEach(this::cleanNode);
        events.stream().forEach(this::cleanEvent);
    }
}
