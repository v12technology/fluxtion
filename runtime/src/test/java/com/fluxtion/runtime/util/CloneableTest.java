/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.runtime.util;

import org.junit.Assert;
import org.junit.Test;

public class CloneableTest {

    @Test
    public void testClone() throws CloneNotSupportedException {
        MyCopy parentCopy = new MySubCopy();
        MySubCopy subCopy = new MySubCopy();

        MyCopy myCopy = subCopy.copyFrom(parentCopy);
        MyCopy myCopy1 = parentCopy.copyFrom(subCopy);

        Assert.assertEquals(myCopy, myCopy1);
        Assert.assertNotSame(myCopy, myCopy1);
    }
}
