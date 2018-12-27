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
package com.fluxtion.extension.functional.push;

import com.fluxtion.extension.declarative.builder.factory.NumericValuePushFactory;
import com.fluxtion.extension.declarative.api.numeric.NumericValuePush;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class PushTest {

    @Test
    public void pushTest_1() throws Exception {
        JavaTestGeneratorHelper.setupDefaultTestContext(
                "com.fluxtion.extension.fucntional.test.generated.push", "Push_1");
        DealHandler constant_5 = new DealHandler(5);
        SaleItem item = new SaleItem();
        NumericValuePush<SaleItem> pusher = NumericValuePushFactory.setNumeric(constant_5, item, SaleItem::setQuantity);
        
        Assert.assertThat(item.getQuantity(), is(0));
        pusher.pushNumericValue();
        Assert.assertThat(item.getQuantity(), is(0));
        pusher.onNumericUpdated(constant_5);
        pusher.pushNumericValue();
        Assert.assertThat(item.getQuantity(), is(5));
                
        
    }
}
