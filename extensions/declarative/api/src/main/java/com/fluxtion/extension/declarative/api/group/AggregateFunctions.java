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
package com.fluxtion.extension.declarative.api.group;

import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateless;

/**
 * Math functions for use with GroupBy.
 * 
 * @author Greg Higgins
 */
public class AggregateFunctions {

    public static Class<AggregateAverage> Avg = AggregateAverage.class;
    public static Class<AggregateCount> Count = AggregateCount.class;
    public static Class<AggregateMax> Max = AggregateMax.class;
    public static Class<AggregateMin> Min = AggregateMin.class;
    public static Class<AggregateSum> Sum = AggregateSum.class;
    public static Class<AggregatePassThrough> Set = AggregatePassThrough.class;

    public static class AggregateSum implements NumericFunctionStateless {

        public static double calcSum(double newValue, double oldSum) {
            return newValue + oldSum;
        }
    }
    
    public static class AggregatePassThrough implements NumericFunctionStateless {
        
        public static double set(double newValue, double oldSum) {
            return newValue;
        }
    }

    public static class AggregateCount implements NumericFunctionStateless {

        public static int increment(int newValue, int oldValue) {
            oldValue++;
            return oldValue;
        }
    }
    
    public static class AggregateMin implements NumericFunctionStateless {

        public static int minimum(int newValue, int oldValue) {
            return Math.min(newValue, oldValue);
        }
    }
    
    public static class AggregateMax implements NumericFunctionStateless {

        public static int maximum(int newValue, int oldValue) {
            return Math.max(newValue, oldValue);
        }
    }

    public static class AggregateAverage implements NumericFunctionStateful {

        private int count;
        private double sum;

        public double calcAverage(double newValue, double oldAverage) {
            count++;
            sum += newValue;
            return sum / count;
        }

        @Override
        public double reset() {
            count = 0;
            sum = 0;
            return Double.NaN;
        }
    }
}
