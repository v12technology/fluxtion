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
package com.fluxtion.compiler.builder.generation;

import com.fluxtion.runtim.event.Event;
import java.util.ServiceLoader;

/**
 * Produces {@link FilterDescription} instances that act as a extension points
 * for control of filter comments and variable names in the generated SEP.
 *
 * <h2>Registering factories</h2>
 * Fluxtion employs the {@link ServiceLoader} pattern to register user
 * implemented FilterDescriptionProducer's. Please read the java documentation
 * describing the meta-data a factory implementor must provide to register a
 * factory using the {@link ServiceLoader} pattern.
 *
 * @author Greg Higgins
 */
public interface FilterDescriptionProducer {

    default FilterDescription getFilterDescription(Class<? extends Event> event, int filterId) {
        FilterDescription filter = new FilterDescription(event, filterId);
        filter.comment = null;
        filter.variableName = null;
        return filter;
    }

    default FilterDescription getFilterDescription(Class<? extends Event> event, String filterString) {
        FilterDescription filter = new FilterDescription(event, filterString);
        filter.comment = null;
        filter.variableName = null;
        return filter;
    }

}
