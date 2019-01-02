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
package com.fluxtion.example.core.events.collections;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEventHandler;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Builder extends SEPConfig {

    @Override
    public void buildConfig() {
        MyEventHandler handler1 = addNode(new MyEventHandler());
        MyEventHandler handler2 = addNode(new MyEventHandler());
        DataEventHandler handler3 = addNode(new DataEventHandler());
        ConfigHandler cfg0 = addNode(new ConfigHandler());
        ConfigHandler cfg1 = addNode(new ConfigHandler());
        ConfigHandler cfg2 = addNode(new ConfigHandler());
        List<ConfigHandler> cfgHandlers = Arrays.asList(cfg1, cfg2);
        Aggregator agg = addNode(new Aggregator(new Object[]{handler1, handler2, handler3, cfg0}, cfgHandlers));
    }

}
