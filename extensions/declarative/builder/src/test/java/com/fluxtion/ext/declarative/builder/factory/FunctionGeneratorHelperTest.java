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
package com.fluxtion.ext.declarative.builder.factory;

import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import com.fluxtion.ext.declarative.api.numeric.MutableNumericValue;
import com.fluxtion.ext.declarative.api.numeric.NumericConstant;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author greg
 */
public class FunctionGeneratorHelperTest {
    
    public FunctionGeneratorHelperTest() {
    }

    /**
     * Test of intFromMap method, of class FunctionGeneratorHelper.
     */
    @Ignore
    @Test
    public void testIntFromMap() {
        System.out.println("intFromMap");
        Map configMap = null;
        String key = "";
        int defualtValue = 0;
        int expResult = 0;
        int result = FunctionGeneratorHelper.intFromMap(configMap, key, defualtValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of numericGetMethod method, of class FunctionGeneratorHelper.
     */
    @Test
    public void testNumericGetMethod() throws NoSuchMethodException {
        System.out.println("numericGetMethod");
        NumericValue val = new NumericConstant(10);
        Method expResult = NumericConstant.class.getMethod("doubleValue");
        Method result = FunctionGeneratorHelper.numericGetMethod(val, NumericValue::doubleValue );
        assertEquals(expResult, result);
    }
    
    /**
     * Test of numericSetMethod method, of class FunctionGeneratorHelper.
     */
    @Test
    public void testNumericSetMethod() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("numericSetMethod");
        MutableNumericValue val = new MutableNumericValue();
        Method expResult = MutableNumericValue.class.getMethod("setLongValue", long.class);
        Method result;
        result = FunctionGeneratorHelper.numericSetMethod(val, MutableNumericValue::setLongValue );
        assertEquals(expResult, result);
        result.invoke(val, 1000l);
        assertEquals(1000, val.longValue);
    }

}
