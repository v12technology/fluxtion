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
package com.fluxtion.generator.model;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.generator.Generator;
import com.fluxtion.test.event.AnnotatedHandlerNoEventId;
import com.fluxtion.test.event.AnnotatedHandlerNoFilter;
import com.fluxtion.test.event.AnnotatedHandlerStringFilter;
import com.fluxtion.test.event.AnnotatedTimeHandler;
import com.fluxtion.test.event.DirtyNotifierNode;
import com.fluxtion.test.event.EventHandlerCb;
import com.fluxtion.test.event.InitCB;
import com.fluxtion.test.event.ParentUpdateListener;
import com.fluxtion.test.event.RootCB;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class JavaVeloctyTest {


    //example generation - tests to be added
    @Test
    public void hello() throws IOException, Exception {

        SEPConfig cfg = new SEPConfig();

        //set up nodes
        EventHandlerCb e1 = new EventHandlerCb("e1", 1);
        EventHandlerCb e2 = new EventHandlerCb("e2", 2);
        EventHandlerCb e3 = new EventHandlerCb("e3", 3);
        AnnotatedHandlerNoEventId an1 = new AnnotatedHandlerNoEventId(25);
        AnnotatedHandlerStringFilter stringFilterEh = new AnnotatedHandlerStringFilter();
        AnnotatedHandlerNoFilter noFilterEh = new AnnotatedHandlerNoFilter();
        stringFilterEh.filterString = "XXX";
        
        RootCB eRoot = new RootCB("eRoot");
        InitCB i1 = new InitCB("i1");
        InitCB i2 = new InitCB("i2");
        InitCB i3 = new InitCB("i3");
        InitCB i4 = new InitCB("i4");
        ParentUpdateListener pl_1 = new ParentUpdateListener("pl_1");
        DirtyNotifierNode dirty_1 = new DirtyNotifierNode("dirty_1");

        i1.parents = new Object[]{i2, stringFilterEh};
        i2.parents = new Object[]{e1, e2, i3};
        i3.parents = new Object[]{e3, an1, noFilterEh};
        pl_1.parents = new Object[]{e3, an1, stringFilterEh, i2, noFilterEh};
        dirty_1.parents = new Object[]{e2, stringFilterEh};
        i4.parents = new Object[]{dirty_1};
        
        eRoot.parents = new Object[]{i1, i3, pl_1, dirty_1};

        List<Object> nodeList = Arrays.asList(eRoot, e1, i1, i2, e2, e3, i3, i4, an1, stringFilterEh, pl_1, dirty_1, noFilterEh);
        HashMap<Object, String> overrides = new HashMap<>();
        overrides.put(eRoot, "root_output");
        overrides.put(e1, "event_handler_1");
        
        
        GenerationContext.setupStaticContext("com.fluxtion.test.template.java", "TestJava", new File("target/generated-test-sources/java/"), new File("target/generated-test-sources/resources/"));
        cfg.templateFile = "javaTemplate.vsl";
        cfg.nodeList = nodeList;
        cfg.publicNodes = overrides;
//        cfg.className = "TestJava";
        cfg.supportDirtyFiltering = true;
        cfg.generateDescription = false;
        
        Generator generator = new Generator();
//        GeneratorHugeFilter generator = new GeneratorHugeFilter();
        generator.templateSep(cfg);

    }



}
