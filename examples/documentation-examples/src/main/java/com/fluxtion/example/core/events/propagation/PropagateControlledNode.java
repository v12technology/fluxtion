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
package com.fluxtion.example.core.events.propagation;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.example.shared.DataEventHandler;

/**
 *
 * @author V12 Technology Ltd.
 */
public class PropagateControlledNode {

    private final PropagateControlledhandler handler;

    @NoEventReference
    private final DataEventHandler datahandler;

    public PropagateControlledNode(PropagateControlledhandler handler, DataEventHandler datahandler) {
        this.handler = handler;
        this.datahandler = datahandler;
    }
    
    @OnEvent
    public void update(){
        
    }

}
