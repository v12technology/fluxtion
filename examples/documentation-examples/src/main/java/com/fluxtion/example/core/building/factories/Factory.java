/*
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.example.core.building.factories;

import com.fluxtion.api.node.NodeFactory;
import com.fluxtion.api.node.NodeRegistry;
import com.fluxtion.api.node.SEPConfig;
import java.util.Map;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Factory extends SEPConfig implements NodeFactory<FactoryNode>  {
    
    @Override
    public FactoryNode createNode(Map config, NodeRegistry registry) {
//        int limit = Integer.parseInt((String) config.get("limit"));
        int limit = (Integer) config.get("limit");
        String filter = (String) config.get("filter");
        FilteredDataHandler handler = new FilteredDataHandler(filter);
        handler.setLimit(limit);
        registry.registerNode(handler, "handler");
        return new FactoryNode(handler);
    }
    
}
