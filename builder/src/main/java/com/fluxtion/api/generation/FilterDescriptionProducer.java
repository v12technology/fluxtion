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
package com.fluxtion.api.generation;

import com.fluxtion.runtime.event.Event;

/**
 * Produces @see com.fluxtion.api.generation.FilterDescription instances,
 * Allowing users to extend the generation of the SEP with human readable comments
 * and variables.
 * 
 * Users implement this interface and register with the SEP generator before
 * generation time. 
 * 
 * @author Greg Higgins 
 */
public interface FilterDescriptionProducer {
    
    default FilterDescription getFilterDescription(Class<? extends Event> event, int filterId){
        FilterDescription filter = new FilterDescription(event, filterId);
        filter.comment = null;
        filter.variableName = null;
        return filter;
    }
    
    default FilterDescription getFilterDescription(Class<? extends Event> event, String filterString){
        FilterDescription filter = new FilterDescription(event, filterString);
        filter.comment = null;
        filter.variableName = null;
        return filter;
    }
    
}
