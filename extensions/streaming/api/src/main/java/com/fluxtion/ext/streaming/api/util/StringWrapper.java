/* 
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.util;

import com.fluxtion.ext.streaming.api.Wrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * A wrapper for a CharSequence.
 *
 * @author gregp
 */
@EqualsAndHashCode
@AllArgsConstructor
public class StringWrapper implements Wrapper<String> {

    private final String stringVal;

    @Override
    public String event() {
        return stringVal;
    }

    @Override
    public Class<String> eventClass() {
        return String.class;
    }
}
