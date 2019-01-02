/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import static com.fluxtion.generator.targets.JavaGeneratorNames.*;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.test.enums.DayOfWeek;
import com.fluxtion.test.enums.DayProcessor;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class EnumTestIT {
    @Test
    public void test_enumField() throws Exception {
        //System.out.println("test_enumField");
        EventHandler handler = JavaTestGeneratorHelper.sepInstance(test_enumField);
		DayProcessor result = (DayProcessor) handler.getClass().getField("dayProcessor").get(handler);
        Assert.assertEquals(DayOfWeek.MONDAY, result.firsDayOfWeek);
    }  
}
