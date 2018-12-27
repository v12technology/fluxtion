/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.extension.functional.group;

import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateless;

/**
 *
 * @author Greg Higgins
 */
public class AggregateFunctions {

    static Class<AggregateSum> Sum = AggregateSum.class;
    static Class<AggregateAverage> Avg = AggregateAverage.class;
    static Class<AggregateCount> Count = AggregateCount.class;

    public static class AggregateSum implements NumericFunctionStateless {

        public static double calcSum(double newValue, double oldSum) {
            return newValue + oldSum;
        }
    }

    public static class AggregateCount implements NumericFunctionStateless {

        public static int increment(int newValue, int oldValue) {
            oldValue++;
            return oldValue;
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
