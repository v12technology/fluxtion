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
package com.fluxtion.generator.it;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.event.RootCB;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static junit.framework.Assert.*;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class JavaCodeGenerationTestIT {


    @Test
    public void testJavaGeneration() {
        //System.out.println("testJavaGeneration");

        JavaDocBuilder builder = new JavaDocBuilder();
        File f = new File("target/generated-test-sources/java/");
        builder.addSourceTree(f);
        
        JavaClass genClass = builder.getClassByName("com.fluxtion.test.template.java.TestJava");
        JavaField rootField = genClass.getFieldByName("root_output");
        
        assertNotNull(rootField);
        
        Type actualType = rootField.getType();
        Type expecteType = new Type(RootCB.class.getName());
        
        assertEquals(expecteType, actualType);
    }
    
    @Test
    public void testDeclarativeFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        //System.out.println("testDeclarativeFactory");
        Class clazz = Class.forName("com.fluxtion.test.template.java.SimpleCalculator");
        Object obj = clazz.newInstance();
        Method initMethod = clazz.getMethod("init");
        initMethod.invoke(obj);
        ((EventHandler)obj).onEvent(new CharEvent('4'));
        ((EventHandler)obj).onEvent(new CharEvent('0'));
        ((EventHandler)obj).onEvent(new CharEvent('0'));
        ((EventHandler)obj).onEvent(new CharEvent('+'));
        ((EventHandler)obj).onEvent(new CharEvent('2'));
        ((EventHandler)obj).onEvent(new CharEvent('2'));
        ((EventHandler)obj).onEvent(new CharEvent('='));
        ((BatchHandler)obj).batchEnd();
    }
}
