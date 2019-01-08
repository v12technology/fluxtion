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
package com.fluxtion.ext.declarative.api.test;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Test;
import java.util.Objects;

/**
 * Boolean not operator.
 * 
 * @author gregp
 */
public class NotOperator implements Test {

    private final Object tracked;

    public NotOperator(Object tracked) {
        this.tracked = tracked;
    }

    @OnEvent(dirty = false)
    public boolean testNot() {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.tracked);
        return hash;
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
        final NotOperator other = (NotOperator) obj;
        if (!Objects.equals(this.tracked, other.tracked)) {
            return false;
        }
        return true;
    }

}
