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
package fluxtion.extension.functional.test.helpers;

import com.fluxtion.api.annotations.OnEvent;

/**
 * records a count of onEvent calls
 * 
 * @author Greg Higgins
 */
public class UpdateCount {
   
    public Object parent;
    public int count;

    public UpdateCount(Object parent) {
        this.parent = parent;
    }

    public UpdateCount() {
    }
    
    @OnEvent
    public void increment(){
        this.count++;
    }
    
}
