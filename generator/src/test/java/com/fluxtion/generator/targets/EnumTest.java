/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.targets.JavaGeneratorNames.*;
import com.fluxtion.test.enums.DayProcessor;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class EnumTest {

    @Test
    public void test_enumField() throws Exception {
        //System.out.println("test_enumField");
        SEPConfig cfg = new DayProcessor.Builder();
        JavaTestGeneratorHelper.generateClass(cfg, test_enumField);
    }
}
