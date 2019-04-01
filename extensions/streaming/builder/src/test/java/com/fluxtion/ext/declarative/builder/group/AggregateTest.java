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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import org.junit.Test;
import static org.junit.Assert.*;
import com.fluxtion.ext.streaming.api.numeric.NumericFunctionStateful;
import com.fluxtion.generator.util.BaseSepTest;

/**
 *
 * @author Greg Higgins
 */
public class AggregateTest extends BaseSepTest{

    public static class CumSum implements NumericFunctionStateful {

        public double calc(double prevVal, double newVal) {
            return prevVal + newVal;
        }
    }

    @Test
    public void testPrimitvePush() {
        Class<?> m = push(AllPrimitives.class, AllPrimitives::setBbyteVal);
        assertEquals(m, byte.class);
        m = push(AllPrimitives.class, AllPrimitives::setShortVal);
        assertEquals(m, short.class);
        m = push(AllPrimitives.class, AllPrimitives::setIntVal);
        assertEquals(m, int.class);
        m = push(AllPrimitives.class, AllPrimitives::setLongVal);
        assertEquals(m, long.class);
        m = push(AllPrimitives.class, AllPrimitives::setFloatVal);
        assertEquals(m, float.class);
        m = push(AllPrimitives.class, AllPrimitives::setDoubleVal);
        assertEquals(m, double.class);
    }


    public <T> Class<?> push(Class<T> clazz, SerializableBiConsumer<T, ? super Byte> target) {
        return target.method().getParameterTypes()[0];
    }

    public static class AllPrimitives {
        char charVal;
        byte bbyteVal;
        short shortVal;
        int intVal;
        long longVal;
        float floatVal;
        double doubleVal;

        public char getCharVal() {
            return charVal;
        }

        public void setCharVal(char charVal) {
            this.charVal = charVal;
        }

        public byte getBbyteVal() {
            return bbyteVal;
        }

        public void setBbyteVal(byte bbyteVal) {
            this.bbyteVal = bbyteVal;
        }

        public int getIntVal() {
            return intVal;
        }

        public short getShortVal() {
            return shortVal;
        }

        public void setShortVal(short shortVal) {
            this.shortVal = shortVal;
        }

        public float getFloatVal() {
            return floatVal;
        }

        public void setFloatVal(float floatVal) {
            this.floatVal = floatVal;
        }

        public void setIntVal(int intVal) {
            this.intVal = intVal;
        }

        public long getLongVal() {
            return longVal;
        }

        public void setLongVal(long longVal) {
            this.longVal = longVal;
        }

        public double getDoubleVal() {
            return doubleVal;
        }

        public void setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
        }
        
        
        
    }

}
