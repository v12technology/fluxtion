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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.util;

import com.fluxtion.generator.util.ClassUtils;
import com.fluxtion.generator.model.CbMethodHandle;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Greg Higgins
 */
public class ClassUtilsTest {
    
    
    public static class A{}
    
    public static class B{}
    public static class B1 extends B{}
    public static class B2 extends B1{}
    
    
    public static class Handler1{
        public void handleA(A a){}
        public void handleB(B a){}
        public void handleB1(B1 a){}
        public void handleB2(B2 a){}
        public void handleObject(Object o){}
    }

    @Test
    public void testCbLocate() throws NoSuchMethodException{
        Handler1 h = new Handler1();
        CbMethodHandle cbA = new CbMethodHandle(h.getClass().getMethod("handleA", A.class), h, null);
        CbMethodHandle cbB = new CbMethodHandle(h.getClass().getMethod("handleB", B.class), h, null);
        CbMethodHandle cbB1 = new CbMethodHandle(h.getClass().getMethod("handleB1", B1.class), h, null);
        CbMethodHandle cbB2 = new CbMethodHandle(h.getClass().getMethod("handleB2", B2.class), h, null);
        CbMethodHandle cbObj = new CbMethodHandle(h.getClass().getMethod("handleObject", Object.class), h, null);
        List<CbMethodHandle> cbList = Arrays.asList(cbA, cbB, cbB1, cbB2);
//        findBestParentCB
        CbMethodHandle findBestParentCB = ClassUtils.findBestParentCB(new B1(), cbList);
        assertEquals(cbB1, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B2(), cbList);
        assertEquals(cbB2, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B(), cbList);
        assertEquals(cbB, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new A(), cbList);
        assertEquals(cbA, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB("", cbList);
        assertEquals(null, findBestParentCB);
        //add Object CB
        cbList = Arrays.asList(cbA, cbB, cbB1, cbB2, cbObj);
        findBestParentCB = ClassUtils.findBestParentCB(new B1(), cbList);
        assertEquals(cbB1, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B2(), cbList);
        assertEquals(cbB2, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new B(), cbList);
        assertEquals(cbB, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB(new A(), cbList);
        assertEquals(cbA, findBestParentCB);
        findBestParentCB = ClassUtils.findBestParentCB("", cbList);
        assertEquals(cbObj, findBestParentCB);
    }
    
}
