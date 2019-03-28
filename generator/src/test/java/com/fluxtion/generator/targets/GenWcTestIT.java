/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.targets;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.generator.model.parentlistener.wc.StringDriver;
import com.fluxtion.generator.model.parentlistener.wc.WordCounter;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterGeneric;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterGenericArrays;
import com.fluxtion.generator.model.parentlistener.wc.WordCounterInlineEventHandler;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_generic;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_generic_arrays;
import static com.fluxtion.generator.targets.JavaGeneratorNames.test_wc_inline_event_handling;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class GenWcTestIT {

    @Test
    public void test_wc() throws Exception {
        //System.out.println("test_wc");
        EventHandler handler = JavaTestGeneratorHelper.sepInstance(test_wc);

        String testString = "fred goes\nhome\today\n";
        WordCounter result = (WordCounter) handler.getClass().getField("result").get(handler);
        StringDriver.streamChars(testString, handler);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);
    }

    @Test
    public void test_wc_generic() throws Exception {
        //System.out.println("test_wc_generic");
        EventHandler handler = JavaTestGeneratorHelper.sepInstance(test_wc_generic);

        String testString = "fred goes\nhome\today\n";
        WordCounterGeneric result = (WordCounterGeneric) handler.getClass().getField("result").get(handler);
        StringDriver.streamChars(testString, handler);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);
    }

    @Test
    public void test_wc_generic_arrays() throws Exception {
        //System.out.println("test_wc_generic_arrays");
        EventHandler handler = JavaTestGeneratorHelper.sepInstance(test_wc_generic_arrays);

        String testString = "fred goes\nhome\today\n";
        WordCounterGenericArrays result = (WordCounterGenericArrays) handler.getClass().getField("result").get(handler);
        StringDriver.streamChars(testString, handler);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);
    }

    @Test
    public void test_wc_inline_event_handling() throws Exception {
        //System.out.println("test_wc_inline_event_handling");
        EventHandler handler = JavaTestGeneratorHelper.sepInstance(test_wc_inline_event_handling);

        String testString = "fred goes\nhome\today\n";
        WordCounterInlineEventHandler result = (WordCounterInlineEventHandler) handler.getClass().getField("result").get(handler);
        StringDriver.streamChars(testString, handler);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);
    }

}
