/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.node;

import com.fluxtion.api.generation.FilterDescriptionProducer;
import com.fluxtion.api.generation.NodeNameProducer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fluxtion.runtime.audit.Auditor;

/**
 *
 * @author Greg Higgins
 */
public class SEPConfig {

    //methods to implement, to make this more fluent api
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

    public <T> T addPublicNode(T node, String name) {
        if (publicNodes == null) {
            publicNodes = new HashMap<>();
        }
        publicNodes.put(node, name);
        return node;
    }
    
    public <T extends Auditor> T addAuditor(T listener, String name){
        if(auditorMap == null){
            auditorMap = new HashMap<>();
        }
        auditorMap.put(name, listener);
        return listener;
    }
    
    public void mapClass(String originalFqn, String mappedFqn){
        class2replace.put(originalFqn, mappedFqn);
    }
    
    /**
     * 
     */
    public void buildConfig(){
        
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
    /**
     * pluggable strategy to customise names of nodes
     */
    public NodeNameProducer nodeNameStrategy;

    //MAPPING
    /**
     * overrides the filter integer id's for a set of instances
     */
    public Map<Object, Integer> filterMap;

    public FilterDescriptionProducer filterDescriptionProducer;

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
    public int maxFiltersInline = 2;

    /**
     * Map an original fully qualified class name into a new value. Can be useful
     * if generated code wants to remove all dependencies to Fluxtion classes
     * and replaced with user classes.
     */
    public final Map<String, String> class2replace = new HashMap<>();
}
