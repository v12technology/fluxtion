/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.test.nodes;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnBatchEnd;
import com.fluxtion.runtime.event.AbstractEventHandlerNode;
import com.fluxtion.test.event.CharEvent;

/**
 * @author Greg Higgins
 */
public class KeyProcessor extends AbstractEventHandlerNode<CharEvent> {

    private int count = 0;
    public char myChar;
    public Accumulator accumulator;
    public boolean notifyAccumulator;

    @Override
    public Class<CharEvent> eventClass() {
        return CharEvent.class;
    }

    @Override
    public boolean onEvent(CharEvent e) {
        //System.out.println("received " + e);
        count++;
        if (notifyAccumulator && accumulator != null)
            accumulator.add(myChar);
        return true;
    }

    @OnBatchEnd
    public void batchEnd() {
        count = 0;
    }

    @Initialise
    public void init() {
        count = 0;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.myChar;
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
        final KeyProcessor other = (KeyProcessor) obj;
        if (this.myChar != other.myChar) {
            return false;
        }
        return true;
    }


}
