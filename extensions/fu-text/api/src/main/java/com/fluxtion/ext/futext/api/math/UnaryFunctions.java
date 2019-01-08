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
package com.fluxtion.ext.futext.api.math;

import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateful;
import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateless;



/**
 *
 * @author greg
 */
public class UnaryFunctions {

    //STATEFUL functions
    public static class Avg implements NumericFunctionStateful {
        private int count;
        private double sum;
        
        public double avg(double prevVal, double newVal){
            count++;
            sum += newVal;
            return sum/count;
        }

        @Override
        public double reset() {
            count = 0;
            sum = 0;
            return Double.NaN;
        }
        
    }
        
    
    public static class Min implements NumericFunctionStateful, NumericArrayFunctionStateful {

        public double min(double op1, double op2) {
            return Math.min(op1, op2);
        }
        
        @Override
        public double reset(){
            return Double.MAX_VALUE;
        }
    }

    public static class Max implements NumericFunctionStateful, NumericArrayFunctionStateful {

        public double max(double op1, double op2) {
            return Math.max(op1, op2);
        }
        
        @Override
        public double reset(){
            return Double.MIN_VALUE;
        }
    }

    public static class CumSum implements NumericFunctionStateful, NumericArrayFunctionStateful {

        public double incSum(double op1, double op2) {
            return op1 + op2;
        }
        
        @Override
        public double reset(){
            return 0;
        }
    }

    //STATELESS functions
    public static class Abs implements NumericFunctionStateless {

        public double abs(double op1) {
            return Math.abs(op1);
        }
    }

}
