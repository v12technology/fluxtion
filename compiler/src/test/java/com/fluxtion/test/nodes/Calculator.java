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
import com.fluxtion.runtime.annotations.OnTrigger;

/**
 * @author Greg Higgins
 */
public class Calculator {

    public Accumulator accumulator;
    public KeyProcessor calculateKey;
    private int currentSum = 0;
    String operation = "+";

    @OnTrigger
    public boolean calcComplete() {
        //System.out.println("Calculation stack");
        for (String val : accumulator.getInputQueue()) {
            //System.out.println("\t'" + val + "'");
            int register = 0;
            try {
                register = Integer.decode(val);
                switch (operation) {
                    case "+":
                        currentSum += register;
                        register = 0;
                        break;
                    case "-":
                        currentSum -= register;
                        register = 0;
                        break;
                    case "*":
                        currentSum *= register;
                        register = 0;
                        break;
                    case "/":
                        currentSum /= register;
                        register = 0;
                        break;

                }
            } catch (Exception e) {
                operation = val;
            }
        }
        return true;

        //System.out.println("=====================");
        //System.out.println("RESULT:" + currentSum);
        //System.out.println("=====================");
    }

    @OnBatchEnd
    public void clearResults() {
        //System.out.println("Calculator cleared");
        currentSum = 0;
        operation = "+";
    }

    @Initialise
    public void init() {
        currentSum = 0;
        operation = "+";
    }

}
