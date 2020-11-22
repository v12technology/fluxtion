/*
 * Copyright (C) 2020 V12 Technology Ltd.
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
package com.fluxtion.ext.streaming.api.numeric;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;

/**
 *
 * @author V12 Technology Ltd.
 */
public abstract class DefaultNumberWrapper extends AbstractFilterWrapper<Number> {
//public class DefaultNumberWrapper implements Wrapper<Number> {

    private final Wrapper<Number> parent;
    protected transient final MutableNumber result = new MutableNumber();

    public DefaultNumberWrapper(Wrapper<Number> parent) {
        this.parent = parent;
    }

    public static class DefaultIntWrapper extends DefaultNumberWrapper {

        private final int defaultVal;

        public DefaultIntWrapper(Wrapper<Number> parent, int defaultVal) {
            super(parent);
            this.defaultVal = defaultVal;
        }

        public void reset() {
            recalculate = true;
            result.set(defaultVal);
        }

    }

    public static class DefaultDoubleWrapper extends DefaultNumberWrapper {

        private final double defaultVal;

        public DefaultDoubleWrapper(Wrapper<Number> parent, double defaultVal) {
            super(parent);
            this.defaultVal = defaultVal;
        }

        public void reset() {
            recalculate = true;
            result.set(defaultVal);
        }

    }

    public static class DefaultLongWrapper extends DefaultNumberWrapper {

        private final long defaultVal;

        public DefaultLongWrapper(Wrapper<Number> parent, long defaultVal) {
            super(parent);
            this.defaultVal = defaultVal;
        }

        public void reset() {
            recalculate = true;
            result.set(defaultVal);
        }

    }

    @OnEvent
    public boolean onEvent() {
        boolean updated = true;
        if (recalculate) {
            Number parentNumber = parent.event();
            updated = !parentNumber.equals(result);
            result.set(parentNumber);
        }
        recalculate = true;
        return updated;
    }
    
    @AfterEvent
    public void resetAfterEvent() {
      if (reset) {
        reset();
      }
      reset = false;
    } 

    @Override
    public Number event() {
        return result;
    }

    @Override
    public Class<Number> eventClass() {
        return Number.class;
    }

    @Override
    public boolean isValidOnStart() {
        return true;
    }

    @Override
    public FilterWrapper<Number> publishAndReset(Object notifier) {
        parent.publishAndReset(notifier);
        return super.publishAndReset(notifier);
    }

    @Override
    public FilterWrapper<Number> resetNoPublish(Object notifier) {
        parent.resetNoPublish(notifier);
        return super.resetNoPublish(notifier); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FilterWrapper<Number> resetAndPublish(Object notifier) {
        parent.resetAndPublish(notifier);
        return super.resetAndPublish(notifier); //To change body of generated methods, choose Tools | Templates.
    }

}
