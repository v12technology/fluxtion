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
package com.fluxtion.ext.declarative.builder.helpers;

import com.fluxtion.ext.declarative.api.Test;

/**
 *
 * @author Greg Higgins
 */
public class Tests {
    
    public static Class<Greater> GreaterThan = Greater.class;
    
    public static class Positive implements Test {

        public boolean isPositive(double op1) {
            return op1 > 0;
        }
    }

    public static class Negative implements Test {

        public boolean isPositive(double op1) {
            return op1 < 0;
        }
    }

    public static class Greater implements Test {

        public boolean isGreater(double op1, double op2) {
            return op1 > op2;
        }
    }

    public static class Smaller implements Test {

        public boolean isSmaller(double op1, double op2) {
            return op1 < op2;
        }
    }
    
    public static class StringsEqual implements Test{
        public boolean equal(CharSequence seq1, CharSequence seq2 ){
            return seq1.equals(seq2);
        }
    }
}
