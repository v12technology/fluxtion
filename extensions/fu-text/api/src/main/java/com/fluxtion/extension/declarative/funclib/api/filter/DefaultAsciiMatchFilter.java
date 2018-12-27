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
package com.fluxtion.extension.declarative.funclib.api.filter;

import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;

/**
 * 
 * @author Greg Higgins
 */
public class DefaultAsciiMatchFilter implements AsciiMatchFilter{
    
    public Object resetNotifier;
    private boolean reset;
    private boolean match;
    
    @Inject()
    @ConfigVariable(key = AsciiMatchFilter.KEY_FILTER_STRING, field = "searchFilter")
    public AsciiMatchFilter filter;
    
    private transient String searchFilter;
    

    public DefaultAsciiMatchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public DefaultAsciiMatchFilter() {
    }
    
    @OnParentUpdate("resetNotifier")
    public void resetNotification(Object resetNotifier){
        reset = true;
    }
    
    @OnParentUpdate("filter")
    public void onFilterMatch(AsciiMatchFilter filter){
        match = reset;
        reset = resetNotifier==null;
    }
    
    @OnEvent
    public boolean filterMatched(){
        return match;
    }

    @Initialise
    public void init(){
        reset = true;
        match = false;
    }
    
}
