/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.generator.model.parentlistener.wc.WordCounter;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterGeneric;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterGenericArrays;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterInlineEventHandler;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_generic;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_generic_arrays;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_inline_event_handling;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class GenWcTest {

    public GenWcTest() {
    }

    @Test
    public void test_wc() throws Exception {
        //System.out.println("test_wc");
        SEPConfig cfg = new WordCounter.Builder();
        cfg.generateDescription = false;
        JavaTestGeneratorHelper.generateClass(cfg, test_wc);
    }

    @Test
    public void test_wc_generic() throws Exception {
        //System.out.println("test_wc_generic");
        SEPConfig cfg = new WordCounterGeneric.Builder();
        cfg.generateDescription = false;
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_generic);
    }

    @Test
    public void test_wc_generic_arrays() throws Exception {
        //System.out.println("test_wc_generic_arrays");
        SEPConfig cfg = new WordCounterGenericArrays.Builder();
        cfg.generateDescription = false;
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_generic_arrays);
    }

    @Test
    public void test_wc_inline_event_handling() throws Exception {
        //System.out.println("test_wc_inline_event_handling");
        SEPConfig cfg = new WordCounterInlineEventHandler.Builder();
        cfg.generateDescription = false;
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_inline_event_handling);
    }

}
