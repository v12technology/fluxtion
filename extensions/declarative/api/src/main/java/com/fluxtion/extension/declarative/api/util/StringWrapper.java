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
package com.fluxtion.extension.declarative.api.util;

import com.fluxtion.extension.declarative.api.Wrapper;
import java.util.Objects;

/**
 * A wrapper for a CharSequence.
 *
 * @author gregp
 */
public class StringWrapper implements Wrapper<String> {

    private final String stringVal;

    public StringWrapper(String sequence) {
        this.stringVal = sequence;
    }

    @Override
    public String event() {
        return stringVal;
    }

    @Override
    public Class<String> eventClass() {
        return String.class;
    }

    @Override
    public int hashCode() {
        return stringVal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringWrapper other = (StringWrapper) obj;
        if (!Objects.equals(this.stringVal, other.stringVal)) {
            return false;
        }
        return true;
    }
    
}
