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

import com.fluxtion.ext.declarative.api.Wrapper;

/**
 * Interface implemented by nodes processing delimited or fixed length records
 * into structured data.
 *
 * @author gregp
 * @param <T> The target type of the processor
 */
public interface RowProcessor<T> extends Wrapper<T> {

    /**
     * Indicates whether the row passed the validator attached to the target
     * type. A target type can annotate a method
     * <pre>@OnEvent</pre> a boolean return type indicates whether this is a
     * validating method.
     *
     * @return validation was successful, true indicates success. This value is
     * transient only returns true for the event cycle that a successful
     * validation has occurred.
     */
    boolean passedValidation();

}
