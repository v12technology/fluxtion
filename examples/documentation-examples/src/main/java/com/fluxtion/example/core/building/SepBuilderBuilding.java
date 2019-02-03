/*
 * Copyright (C) 2019 gregp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.example.core.building;

import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.core.building.imperative.PropertySubNode;
import com.fluxtion.example.core.building.imperative.SubNode;
import com.fluxtion.example.core.building.injection.InjectingDataProcessor;
import com.fluxtion.example.shared.MyEventHandler;

/**
 *
 * @author gregp
 */
public class SepBuilderBuilding {

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.building.injection.generated",
            outputDir = "src/main/java"
    )
    public void buildInjection(SEPConfig cfg) {
        cfg.addNode(new InjectingDataProcessor("myfilter_string"));
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.building.imperative.generated",
            outputDir = "src/main/java"
    )
    public void buildImperative(SEPConfig cfg) {
        MyEventHandler handler = cfg.addNode(new MyEventHandler());
        SubNode subNode = cfg.addPublicNode(new SubNode(handler), "subNode");
        PropertySubNode propNode = new PropertySubNode();
        propNode.setMySubNode(subNode);
        propNode.someParent = handler;
        cfg.addNode(propNode, "propNode");
    }
    
//    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.building.factories.generated",
//            outputDir = "src/main/java"
//    )
//    public void buildFactory(SEPConfig cfg)  {
//        cfg.yamlFactoryConfig = "src/main/resources/cfg/factorytest.yml";
//    }

}
