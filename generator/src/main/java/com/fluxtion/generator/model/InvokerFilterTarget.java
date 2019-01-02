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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.model;

import com.fluxtion.builder.generation.FilterDescription;

/**
 * A target for an invoker holding a call tree for a filtered event. 
 * @author Greg Higgins
 */
public class InvokerFilterTarget {
    public FilterDescription filterDescription;
    /**
     * The body of the method thst is the call tree for the filtered dispatch
     */
    public String methodBody;
    /**
     * Name of the method to invoke that holds the call tree for the filtered
     * processing.
     */
    public String methodName;
    /**
     * the name of the map holding the invokers for this Event class 
     */
    public String intMapName;
    /**
     * the name of the map holding the invokers for this Event class
     */
    public String stringMapName;
}
