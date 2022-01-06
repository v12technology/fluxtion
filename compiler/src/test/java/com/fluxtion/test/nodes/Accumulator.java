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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Greg Higgins
 */
public class Accumulator {

    private List<String> inputQueue;
    private String currentVal;

    public void add(char c) {
        //logic
        if (Character.isDigit(c)) {
            currentVal += c;
            int ptr = inputQueue.size() - 1;
            if (ptr < 0) {
                inputQueue.add(currentVal);
            } else {
                inputQueue.set(ptr, currentVal);
            }
        } else {
            inputQueue.add("" + c);
            currentVal = "";
            inputQueue.add(currentVal);
        }
        //System.out.println("Accumulator received:" + c);
    }

    @Initialise
    public void init() {
        inputQueue = new ArrayList<>();
        currentVal = "";
    }

    @OnBatchEnd
    public void batchEnd() {
        inputQueue.clear();
        currentVal = "";
    }

    public List<String> getInputQueue() {
        return Collections.unmodifiableList(inputQueue);
    }

}
