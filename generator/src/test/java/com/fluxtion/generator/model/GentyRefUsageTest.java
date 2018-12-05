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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.model;

import com.google.common.base.Predicates;
import com.googlecode.gentyref.GenericTypeReflector;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.event.TimeEvent;
import com.fluxtion.test.event.TimeHandlerExtends;
import com.fluxtion.test.event.TimeHandlerImpl;
import com.fluxtion.test.event.TimerHandler2Removed;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.*;

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

    private Class getEventType(EventHandler eh) {
        final ParameterizedType name = (ParameterizedType) GenericTypeReflector.getExactSuperType(eh.getClass(), EventHandler.class);
        return (Class) name.getActualTypeArguments()[0];
    }
    
    public static interface GenIntfMethod {
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
