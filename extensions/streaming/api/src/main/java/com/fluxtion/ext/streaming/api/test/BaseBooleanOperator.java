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
package com.fluxtion.ext.streaming.api.test;

import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.Test;
import java.util.Arrays;

/**
 * Boolean and operator.
 *
 * @author gregp
 */
public abstract class BaseBooleanOperator implements Test {

    protected final Object[] tracked;
    protected final transient Test[] trackedTest;
    protected int updateCount;

    public BaseBooleanOperator(Object[] tracked) {
        this.tracked = tracked;
        Object[] temp = Arrays.stream(tracked).filter((Object t) -> t instanceof Test).map(t -> (Test)t).toArray();
        trackedTest = new Test[temp.length]; 
        for (int i = 0; i < temp.length; i++) {
            trackedTest[i] = (Test) temp[i];
        }
    }

    @OnParentUpdate("tracked")
    public void parentUpdated(Object tracked) {
        updateCount++;
        if (tracked instanceof Test) {
            updateCount--;
        }
    }

    protected int testCount() {
        int testCount = 0;
        for (Test test : trackedTest) {
            if (test.passed()) {
                testCount++;
            }
        }
        return testCount;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.deepHashCode(this.tracked);
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
        final BaseBooleanOperator other = (BaseBooleanOperator) obj;
        if (!Arrays.deepEquals(this.tracked, other.tracked)) {
            return false;
        }
        return true;
    }

}
