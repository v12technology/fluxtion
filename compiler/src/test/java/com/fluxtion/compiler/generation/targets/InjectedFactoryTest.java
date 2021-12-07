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
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.builder.node.SEPConfig;

import com.fluxtion.compiler.generation.util.BaseSepInProcessTest;
import com.fluxtion.test.event.CharEvent;
import com.fluxtion.test.nodes.KeyProcessorFactory;
import com.fluxtion.test.nodes.KeyTracker;
import com.fluxtion.test.nodes.KeyTrackerWithVariableConfig;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class InjectedFactoryTest extends BaseSepInProcessTest {

    private static void buildFactory(SEPConfig cfg) {
        cfg.addPublicNode(new KeyTracker(), "keyTracker1");
        Set<Class<? extends NodeFactory<?>>> factoryList = new HashSet<>();
        factoryList.add(KeyProcessorFactory.class);
        cfg.declarativeConfig = new DeclarativeNodeConiguration(null, factoryList, null);
    }

    private static void buildFactoryWithConfig(SEPConfig cfg) {
        cfg.addPublicNode(new KeyTrackerWithVariableConfig(), "keyTracker1");
        //Factories
        Set<Class<? extends NodeFactory<?>>> factoryList = new HashSet<>();
        factoryList.add(KeyProcessorFactory.class);
        cfg.declarativeConfig = new DeclarativeNodeConiguration(null, factoryList, null);
    }

    @Test
    public void test_injected_factory() throws Exception {
        sep(InjectedFactoryTest::buildFactory);
        KeyTracker tracker = getField("keyTracker1");
        Assert.assertEquals('1', tracker.keyProcessor_1.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_1.notifyAccumulator);
        Assert.assertEquals('a', tracker.keyProcessor_a.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_a.notifyAccumulator);
    }

    @Test
    public void test_injected_factory_variable_config() throws Exception {
        sep(InjectedFactoryTest::buildFactoryWithConfig);
        KeyTrackerWithVariableConfig tracker = getField("keyTracker1");
        Assert.assertEquals('1', tracker.keyProcessor_1.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_1.notifyAccumulator);
        Assert.assertEquals('a', tracker.keyProcessor_a.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_a.notifyAccumulator);
        Assert.assertEquals('x', tracker.keyProcessor_x.myChar);
        Assert.assertEquals(false, tracker.keyProcessor_x.notifyAccumulator);
    }

    @Test
    public void validateEventHandling() throws Exception{
        sep(InjectedFactoryTest::buildFactory);
        KeyTracker tracker = getField("keyTracker1");
        CharEvent event_a = new CharEvent('a');
        CharEvent event_1 = new CharEvent('1');
        CharEvent event_x = new CharEvent('x');

        onEvent(event_x);
        assertEquals(false, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(false, tracker.key_a);

        onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(false, tracker.key_a);

        tracker.resetTestFlags();

        onEvent(event_a);
        assertEquals(true, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(true, tracker.key_a);

        onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
    }

    @Test
    public void validateEventHandlingOverride() throws Exception{
        sep(InjectedFactoryTest::buildFactoryWithConfig);
        KeyTrackerWithVariableConfig tracker = getField("keyTracker1");

        CharEvent event_a = new CharEvent('a');
        CharEvent event_1 = new CharEvent('1');
        CharEvent event_x = new CharEvent('x');
        CharEvent event_y = new CharEvent('y');

        onEvent(event_y);
        assertEquals(false, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(false, tracker.key_a);
        assertEquals(false, tracker.key_x);

        onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(false, tracker.key_a);
        assertEquals(false, tracker.key_x);

        tracker.resetTestFlags();

        onEvent(event_a);
        assertEquals(true, tracker.onEvent);
        assertEquals(false, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(false, tracker.key_x);

        onEvent(event_1);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(false, tracker.key_x);

        onEvent(event_x);
        assertEquals(true, tracker.onEvent);
        assertEquals(true, tracker.key_1);
        assertEquals(true, tracker.key_a);
        assertEquals(true, tracker.key_x);
    }
}
