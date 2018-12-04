/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
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
