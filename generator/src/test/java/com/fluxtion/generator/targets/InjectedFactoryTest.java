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

import com.fluxtion.builder.node.DeclarativeNodeConiguration;
import com.fluxtion.builder.node.NodeFactory;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_injected_factory;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_injected_factory_variable_config;
import com.fluxtion.test.nodes.KeyProcessorFactory;
import com.fluxtion.test.nodes.KeyTracker;
import com.fluxtion.test.nodes.KeyTrackerWithVariableConfig;
import com.thoughtworks.qdox.model.JavaClass;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class InjectedFactoryTest {
    
    @Test
    public void test_injected_factory() throws Exception {
        //System.out.println(test_injected_factory);
        SEPConfig cfg = new SEPConfig();
        cfg.generateDescription = false;
        //add nodes
        cfg.addPublicNode(new KeyTracker(), "keyTracker1");
        //Factories
        Set<Class<? extends NodeFactory>> factoryList = new HashSet<>();
        factoryList.add(KeyProcessorFactory.class);
        cfg.declarativeConfig = new DeclarativeNodeConiguration(null, factoryList, null);
        //generate
        JavaClass generatedClass = JavaTestGeneratorHelper.generateClass(cfg, test_injected_factory);
        assertEquals(3, generatedClass.getFields().length);
    }
    
    @Test
    public void test_injected_factory_variable_config() throws Exception {
        //System.out.println(test_injected_factory_variable_config);
        SEPConfig cfg = new SEPConfig();
        cfg.generateDescription = false;
        //add nodes
        cfg.addPublicNode(new KeyTrackerWithVariableConfig(), "keyTracker1");
        //Factories
        Set<Class<? extends NodeFactory>> factoryList = new HashSet<>();
        factoryList.add(KeyProcessorFactory.class);
        cfg.maxFiltersInline = 10;
        cfg.declarativeConfig = new DeclarativeNodeConiguration(null, factoryList, null);
        //generate
        JavaClass generatedClass = JavaTestGeneratorHelper.generateClass(cfg, test_injected_factory_variable_config);
        assertEquals(4, generatedClass.getFields().length);
    }
    
}
