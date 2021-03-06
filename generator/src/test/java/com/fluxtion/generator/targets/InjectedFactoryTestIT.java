/*
 * Copyright (C) 2016 Greg Higgins (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.api.StaticEventProcessor;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_injected_factory;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_injected_factory_variable_config;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.nodes.KeyTracker;
import com.fluxtion.test.nodes.KeyTrackerWithVariableConfig;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class InjectedFactoryTestIT {
    
    
    @Test
    public void validateReferences() throws Exception{
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_injected_factory);
        KeyTracker tracker = (KeyTracker) handler.getClass().getField("keyTracker1").get(handler);
        Assert.assertEquals('1', tracker.keyProcessor_1.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_1.notifyAccumulator);
        Assert.assertEquals('a', tracker.keyProcessor_a.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_a.notifyAccumulator);
    }
    
    @Test
    public void validateReferencesOverride() throws Exception{
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_injected_factory_variable_config);
        KeyTrackerWithVariableConfig tracker = (KeyTrackerWithVariableConfig) handler.getClass().getField("keyTracker1").get(handler);
        Assert.assertEquals('1', tracker.keyProcessor_1.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_1.notifyAccumulator);
        Assert.assertEquals('a', tracker.keyProcessor_a.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_a.notifyAccumulator);
        Assert.assertEquals('x', tracker.keyProcessor_x.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_x.notifyAccumulator);
    }
    
    @Test
    public void validateEventHandling() throws Exception{
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_injected_factory);
        KeyTracker tracker = (KeyTracker) handler.getClass().getField("keyTracker1").get(handler);
        CharEvent event_a = new CharEvent('a');
        CharEvent event_1 = new CharEvent('1');
        CharEvent event_x = new CharEvent('x');
        
        handler.onEvent(event_x);
        assertEquals(false, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(false, tracker.key_a);
        
        handler.onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(false, tracker.key_a);
        
        tracker.resetTestFlags();
        
        handler.onEvent(event_a);
        assertEquals(true, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(true, tracker.key_a);
        
        handler.onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
        
    }
    
    @Test
    public void validateEventHandlingOverride() throws Exception{
        StaticEventProcessor handler = JavaTestGeneratorHelper.sepInstance(test_injected_factory_variable_config);
        KeyTrackerWithVariableConfig tracker = (KeyTrackerWithVariableConfig) handler.getClass().getField("keyTracker1").get(handler);
        CharEvent event_a = new CharEvent('a');
        CharEvent event_1 = new CharEvent('1');
        CharEvent event_x = new CharEvent('x');
        CharEvent event_y = new CharEvent('y');
        
        handler.onEvent(event_y);
        assertEquals(false, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(false, tracker.key_a);
        assertEquals(false, tracker.key_x);
        
        handler.onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(false, tracker.key_a);
        assertEquals(false, tracker.key_x);
        
        tracker.resetTestFlags();
        
        handler.onEvent(event_a);
        assertEquals(true, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(false, tracker.key_x);
        
        handler.onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(false, tracker.key_x);
        
        handler.onEvent(event_x);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(true, tracker.key_x);
        
    }
}
