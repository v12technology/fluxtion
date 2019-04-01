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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.futext.api.ascii;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.ext.futext.api.event.CharEvent;

/**
 *
 * @author greg
 */
public class EolNotifier {
    
    /**
     * The number of header lines to ignore before sending notifications.
     */
    public int headerLines;
    private int lineProcessCount;
    private boolean ignoreHeaderLines;
    
    @EventHandler(filterId = '\n')
    public boolean onEol(CharEvent event){
        boolean ret = false;
        lineProcessCount++;
        if(ignoreHeaderLines){
            ret = lineProcessCount>headerLines;
        }else{
            ret = true;
        }
        return ret;
    }
    
    @Initialise
    public void init(){
        ignoreHeaderLines = headerLines > 0;
        lineProcessCount = 0;
    }
    
}
