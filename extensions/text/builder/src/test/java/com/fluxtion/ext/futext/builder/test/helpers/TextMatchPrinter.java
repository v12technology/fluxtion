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
package com.fluxtion.ext.futext.builder.test.helpers;

import com.fluxtion.api.annotations.ConfigVariable;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.text.api.filter.AsciiMatchFilter;

/**
 * Test class that prints to standard out when matched text occurs, and sets 
 * matched_flag to true.
 * 
 * @author Greg Higgins
 */
public class TextMatchPrinter {
    
    @Inject()
    @ConfigVariable(key = AsciiMatchFilter.KEY_FILTER_STRING, field = "searchFilter")
    public AsciiMatchFilter filter;
    
    private transient String searchFilter;
    
    private boolean match;

    public TextMatchPrinter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public TextMatchPrinter() {
    }
    
    @OnEvent
    public void filterMatched(){
        System.out.println("matched filter:");
        match = true;
    }

    public boolean isMatch() {
        return match;
    }
    
    public void resetMatchFlag(){
        match = false;
    }
    
}
