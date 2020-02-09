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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.builder.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.audit.EventLogControlEvent;
import com.fluxtion.api.audit.EventLogControlEvent.LogLevel;
import com.fluxtion.api.audit.EventLogManager;
import com.fluxtion.api.time.Clock;
import com.fluxtion.builder.generation.NodeNameProducer;
import com.fluxtion.builder.time.ClockFactory;

/**
 * Configuration used by Fluxtion event stream compiler at generation time to
 * control the output of the generated static event processor. The properties
 * control the logical configuration of the compilation and not the physical
 * location of input/output resources.
 *
 * @author Greg Higgins
 */
public class SEPConfig {

    /**
     * Add a node to the SEP. The node will have private final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T> The type of the node to add to the SEP
     * @param node the node instance to add
     * @return The de-duplicated added node
     */
    @SuppressWarnings("unchecked")
    public <T> T addNode(T node) {
        if (nodeList == null) {
            nodeList = new ArrayList();
        }
        if (!nodeList.contains(node)) {
            nodeList.add(node);
            return node;
        }
        return (T) nodeList.get(nodeList.indexOf(node));
    }

    /**
     * Add a node to the SEP. The node will have public final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy if the provided name is null.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T> The type of the node to add to the SEP
     * @param node the node instance to add
     * @param name the variable name of the node
     * @return The de-duplicated added node
     */
    @SuppressWarnings("unchecked")
    public <T> T addNode(T node, String name) {
        addNode(node);
        addPublicNode(node, name);
        return (T) nodeList.get(nodeList.indexOf(node));
    }

    /**
     * Add a node to the SEP. The node will have public final scope, the
     * variable name of the node will be generated from {@link NodeNameProducer}
     * strategy if the provided name is null.<p>
     * Fluxtion will check if this node is already in the node set and will
     * return the previously added node.
     *
     * @param <T> The type of the node to add to the SEP
     * @param node the node instance to add
     * @param name the variable name of the node
     * @return The de-duplicated added node
     */
    public <T> T addPublicNode(T node, String name) {
        if (publicNodes == null) {
            publicNodes = new HashMap<>();
        }
        publicNodes.put(node, name);
        return node;
    }

    /**
     * Adds an {@link Auditor} to this SEP. The Auditor will have public final
     * scope and can be accessed via the provided variable name.
     *
     * @param <T> The type of the Auditor
     * @param listener Auditor instance
     * @param name public name of Auditor
     * @return the added Auditor
     */
    public <T extends Auditor> T addAuditor(T listener, String name) {
        if (auditorMap == null) {
            auditorMap = new HashMap<>();
        }
        auditorMap.put(name, listener);
        return listener;
    }

    /**
     * Maps a class name from one String to another in the generated output.
     *
     * @param originalFqn Class name to replace
     * @param mappedFqn Class name replacement
     */
    public void mapClass(String originalFqn, String mappedFqn) {
        class2replace.put(originalFqn, mappedFqn);
    }
    
    /**
     * adds a clock to the generated SEP.
     * @return the clock in generated SEP
     */
    public Clock clock(){
        addNode(clock, "clock");
        addAuditor(clock, "clock");
        return clock;
    }
    
    /**
     * Add an {@link EventLogManager} auditor to the generated SEP. Specify 
     * the level at which method tracing will take place.
     * @param tracingLogLevel 
     */
    public void addEventAudit(LogLevel tracingLogLevel){
        addAuditor(new EventLogManager().tracingOn(tracingLogLevel), "eventLogger");
    }

    /**
     * Users can override this method and add SEP description logic here. The
     * buildConfig method will be called by the Fluxtion generator at build
     * time.
     */
    public void buildConfig() {

    }

    /**
     * the name of the template file to use as an input
     */
    public String templateFile;
    /**
     * the name of the template file to use as an input
     */
    public String debugTemplateFile;
    /**
     * the name of the template file to use as an input
     */
    public String testTemplateFile;
    /**
     * the name of the template file to use as an input
     */
    public String introspectorTemplateFile;

    /**
     * the nodes included in this graph
     */
    public List nodeList;
    /**
     * Variable names overrides for public nodes, these will be well known and
     * addressable from outside the SEP.
     */
    public HashMap<Object, String> publicNodes;

    public HashMap<String, Auditor> auditorMap;

    /**
     * Node Factory configuration
     */
    public DeclarativeNodeConiguration declarativeConfig;

    //MAPPING
    /**
     * overrides the filter integer id's for a set of instances
     */
    public Map<Object, Integer> filterMap;

    /**
     * An extension point to the generator context. This instance will be
     * available in the templating context under the key MODEL_EXTENSION
     */
    public Object templateContextExtension;

    /**
     * configures generated code to inline the event handling methods or not.
     */
    public boolean inlineEventHandling = false;

    /**
     * configures generated code to support dirty filtering
     */
    public boolean supportDirtyFiltering = false;

    /**
     * generate a set of debugging classes that can be used with the SEP and
     * debugging tool sets.
     */
    public boolean generateDebugPrep = false;

    /**
     * Flag controlling generation of meta data description resources.
     *
     * not required, default = true.
     */
    public boolean generateDescription = true;
    /**
     * Generate a test decorator.
     */
    public boolean generateTestDecorator = false;

    /**
     * attempt to assign private member variables, some platforms will support
     * access to non-public scoped members. e.g. reflection utilities in Java.
     */
    public boolean assignPrivateMembers;

    public boolean formatSource = true;

    /**
     * The maximum number of filter branches inside an event handler before an
     * alternate map-dispatch strategy is employed.
     *
     */
    public int maxFiltersInline = 7;

    /**
     * Map an original fully qualified class name into a new value. Can be
     * useful if generated code wants to remove all dependencies to Fluxtion
     * classes and replaced with user classes.
     */
    public final Map<String, String> class2replace = new HashMap<>();
    
    private final Clock clock = ClockFactory.SINGLETON;

    @Override
    public String toString() {
        return "SEPConfig{" + "templateFile=" + templateFile + ", debugTemplateFile=" + debugTemplateFile + ", testTemplateFile=" + testTemplateFile + ", introspectorTemplateFile=" + introspectorTemplateFile + ", nodeList=" + nodeList + ", publicNodes=" + publicNodes + ", auditorMap=" + auditorMap + ", declarativeConfig=" + declarativeConfig + ", filterMap=" + filterMap + ", templateContextExtension=" + templateContextExtension + ", inlineEventHandling=" + inlineEventHandling + ", supportDirtyFiltering=" + supportDirtyFiltering + ", generateDebugPrep=" + generateDebugPrep + ", generateDescription=" + generateDescription + ", generateTestDecorator=" + generateTestDecorator + ", assignPrivateMembers=" + assignPrivateMembers + ", formatSource=" + formatSource + ", maxFiltersInline=" + maxFiltersInline + ", class2replace=" + class2replace + '}';
    }

}
