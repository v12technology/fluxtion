/* 
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.creator;

import com.fluxtion.builder.generation.GenerationContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author gregp
 */
public class CreatorConfig {

    private String outputSepConfigClass;
    private String outputPackage;
    private String auditorClass;
    private List<Node> nodes;
    private List<EventDefinition> events;
    private String processorId;

    public CreatorConfig(String processorId, String sepConfigClass, String packageName) {
        this.processorId = processorId;
        this.outputSepConfigClass = sepConfigClass;
        this.outputPackage = packageName;
        nodes = new ArrayList<>();
        events = new ArrayList<>();
    }

    public CreatorConfig() {
    }

    public String getOutputSepConfigClass() {
        return outputSepConfigClass;
    }

    public void setOutputSepConfigClass(String outputSepConfigClass) {
        this.outputSepConfigClass = outputSepConfigClass;
    }

    public String getSepCfgShortClassName() {
        return ClassUtils.getShortCanonicalName(outputSepConfigClass);
    }

    public String getSepCfgPackageName() {
        return ClassUtils.getPackageCanonicalName(outputSepConfigClass);
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    public void setOutputPackage(String outputPackage) {
        this.outputPackage = outputPackage;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public String getAuditorClass() {
        return auditorClass;
    }

    public void setAuditorClass(String auditorClass) {
        this.auditorClass = auditorClass;
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

    public List<EventDefinition> getEvents() {
        return events;
    }

    public void setEvents(List<EventDefinition> events) {
        this.events = events;
    }

    public void validateConfig() {
        nodes.stream().forEach(this::cleanNode);
        events.stream().forEach(this::cleanEvent);
    }

}
