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
package com.fluxtion.ext.futext.api.csv;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.ext.declarative.api.util.Named;

/**
 *
 * @author V12 Technology Ltd.
 */
public class ValidationLogger extends Named{
    
    private final String id;
    private StringBuilder sb;
    
    public ValidationLogger(String id) {
        super(id);
        this.id = id;
        sb = new StringBuilder();
    }
    
    public ValidationLogger logError(CharSequence error){
        sb.append(error);
        return this;
    }

    public StringBuilder getSb() {
        return sb;
    }
    
    @AfterEvent
    public void cleanBuffer(){
        sb.setLength(0);
    }

}
