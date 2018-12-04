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
import java.util.Objects;

/**
 *
 * @author Greg Higgins
 */
public class FilterDescription {

    public static final FilterDescription NO_FILTER = new FilterDescription( "NO_FILTER");
    public static final FilterDescription INVERSE_FILTER = new FilterDescription( "INVERSE_FILTER");
    public static final FilterDescription DEFAULT_FILTER = new FilterDescription( "DEFAULT");
    
    /**
     * Value used by the SEP to determine which decision branch to navigate.
     * If integer filtering is used.
     */
    public final int value;

    /**
     * Value used by the SEP to determine which decision branch to navigate.
     * If String filtering is used
     */
    public final String stringValue;
    
    private final String nullId;

    /**
     * boolean value indicating String or integer based filtering.
     */
    public final boolean isIntFilter;
    
    /**
     * Indicates presence of filtering, false value means match all values.
     */
    public boolean isFiltered;

    /**
     * the event class for this filter.
     */
    public Class<? extends Event> eventClass;

    /**
     * Human readable comment to be associated with this filter in the generated
     * code of the SEP. Depending upon the target language this value may be
     * mutated to suit the target language rules.
     */
    public String comment;

    /**
     * User suggested identifier for this filter in the generated SEP code.
     * Depending upon the target language this value may be mutated to suit the
     * relevant rules.
     */
    public String variableName;

    public FilterDescription(Class<? extends Event> eventClass) {
        this.value = 0;
        this.eventClass = eventClass;
        this.stringValue = "";
        this.isIntFilter = true;
        this.isFiltered = false;  
        nullId = "";
    }
    
    public FilterDescription(Class<? extends Event> eventClass, int value) {
        this.value = value;
        this.eventClass = eventClass;
        this.stringValue = "";
        this.isIntFilter = true;
        this.isFiltered = true;
        nullId = "";
    }

    public FilterDescription(Class<? extends Event> eventClass, String value) {
        this.stringValue = value;
        this.eventClass = eventClass;
        this.isIntFilter = false;
        this.isFiltered = true;
        this.value = 0;
        nullId = "";
    }

    private FilterDescription(String value) {
        this.stringValue = "";
        this.eventClass = null;
        this.isIntFilter = false;
        this.isFiltered = true;
        this.value = 0;
        nullId = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        if (isIntFilter) {
            hash = 89 * hash + this.value;
        } else {
            hash = 89 * hash + Objects.hashCode(this.stringValue);
        }
        hash = 89 * hash + (this.isIntFilter ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.eventClass);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FilterDescription other = (FilterDescription) obj;
        if (isIntFilter && this.value != other.value) {
            return false;
        }
        if (!isIntFilter && !Objects.equals(this.stringValue, other.stringValue)) {
            return false;
        }
        if (this.isIntFilter != other.isIntFilter) {
            return false;
        }
        if(this.nullId != other.nullId){
            return false;
        }
        if (!Objects.equals(this.eventClass, other.eventClass)) {
            return false;
        }
        return true;
    }

    
    
    
    @Override
    public String toString() {
        return "FilterDescription{" + "value=" + value + ", eventClass=" + eventClass + ", comment=" + comment + ", variableName=" + variableName + '}';
    }

}
