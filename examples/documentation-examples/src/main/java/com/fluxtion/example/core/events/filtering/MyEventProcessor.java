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
package com.fluxtion.example.core.events.filtering;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterType;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.MyEvent;
import java.util.Date;

/**
 *
 * @author V12 Technology Ltd.
 */
public class MyEventProcessor {

    private final String configFilter;

    public MyEventProcessor(String configFilter) {
        this.configFilter = configFilter;
    }
    
    @EventHandler
    public void handleEvent(MyEvent event) {
    }

    @EventHandler
    public void handleConfigEvent(ConfigEvent event) {
    }

    @EventHandler(filterString = "timeout")
    public void handleTimeoutConfig(ConfigEvent event) {
    }

    @EventHandler(filterString = "maxConnection")
    public void handleMaxConnectionsConfig(ConfigEvent event) {
    }
    
    @EventHandler(FilterType.unmatched)
    public void unHandledConfig(ConfigEvent event) {
    }
    
    @EventHandler(filterStringFromClass = Date.class)
    public void dateConfig(ConfigEvent event) {
    }
    
    @EventHandler(filterVariable = "configFilter")
    public void handleMyVariableConfig(ConfigEvent event) {
    }
}
