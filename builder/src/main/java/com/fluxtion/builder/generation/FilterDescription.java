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
package com.fluxtion.builder.generation;

import com.fluxtion.api.event.Event;
import java.util.Objects;

/**
 * Customises the generated source files to provide user controlled
 * {@link FilterDescription}'s. A user can provide logic to control comment and
 * variable names for filters in the generated code. The intention is to make
 * the generated SEP easier to understand reducing cost to rectify errors.<p>
 * 
 * {@link FilterDescriptionProducer} are registered as producers of descriptions.
 *
 * @author Greg Higgins
 */
public class FilterDescription {

    public static final FilterDescription NO_FILTER = new FilterDescription("NO_FILTER");
    public static final FilterDescription INVERSE_FILTER = new FilterDescription("INVERSE_FILTER");
    public static final FilterDescription DEFAULT_FILTER = new FilterDescription("DEFAULT");

    /**
     * Value used by the SEP to determine which decision branch to navigate. If
     * integer filtering is used.
     */
    public final int value;

    /**
     * Value used by the SEP to determine which decision branch to navigate. If
     * String filtering is used
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
    
    public FilterDescription changeClass(Class<? extends Event> newClass){
        FilterDescription fd = new FilterDescription(newClass, stringValue);
        if(!isFiltered){
            fd = new FilterDescription(newClass);
        }else if(isIntFilter){
            fd = new FilterDescription(newClass, value);
        }else if(this==NO_FILTER){
            return NO_FILTER;
        }else if(this==INVERSE_FILTER){
            return INVERSE_FILTER;
        }else if(this==DEFAULT_FILTER){
            return DEFAULT_FILTER;
        }
        return fd;
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
        if (this.nullId != other.nullId) {
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
