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
package com.fluxtion.generator.model;

import static org.junit.Assert.assertEquals;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;
import static org.reflections.ReflectionUtils.withType;

import com.fluxtion.api.FilteredEventHandler;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.event.TimeHandlerExtends;
import com.fluxtion.test.event.TimeHandlerImpl;
import com.fluxtion.test.event.TimerHandler2Removed;
import com.google.common.base.Predicates;
import com.googlecode.gentyref.GenericTypeReflector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import org.junit.Test;
import org.reflections.ReflectionUtils;
import com.fluxtion.api.StaticEventProcessor;

/**
 *
 * @author Greg Higgins
 */
public class GentyRefUsageTest {


    @Test
    public void testFindEventType() {
        assertEquals(TimeEvent.class , getEventType(new TimeHandlerImpl(2)));
        assertEquals(TimeEvent.class , getEventType(new TimeHandlerExtends(2)));
        assertEquals(TimeEvent.class , getEventType(new TimerHandler2Removed(2)));
    }

    private Class getEventType(FilteredEventHandler eh) {
        final ParameterizedType name = (ParameterizedType) GenericTypeReflector.getExactSuperType(eh.getClass(), FilteredEventHandler.class);
        return (Class) name.getActualTypeArguments()[0];
    }
    
    public interface GenIntfMethod {
        <T> T buildInstance();
    }
    
    public static class GenImplMethod implements GenIntfMethod{

        @Override
        public <T> T buildInstance() {
            Object obj = null;
            T f = (T)obj;
//            GenericTypeReflector.
            return null;
        }
        
    }
    
    @Test
    public void testFindId(){
        Set<Field> allFields = ReflectionUtils.getFields(CharEvent.class, Predicates.and(
                withName("ID"),
                withType(int.class),
                withModifier(Modifier.PUBLIC),
                withModifier(Modifier.STATIC),
                withModifier(Modifier.FINAL)
        ));
        //System.out.println("fields:" + allFields);
    }
}
