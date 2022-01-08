/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.targets;

import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.model.parentlistener.wc.*;
import com.fluxtion.compiler.generation.util.BaseSepInProcessTest;
import org.junit.Test;

import static com.fluxtion.compiler.generation.targets.JavaGeneratorNames.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Greg Higgins
 */
public class GenWcTest extends BaseSepInProcessTest {

    @Test
    public void test_wc() throws Exception {
        sep(c ->{
            WordCounter root = c.addPublicNode(new WordCounter(), "result");
            root.anyCharHandler = (new CharHandler());
            root.eolHandler = (new CharHandler.EolCharEventHandler('\n'));
            root.wordChardHandler = (new CharHandler.UnMatchedCharEventHandler());
            root.delimiterHandlers = new CharHandler.DelimiterCharEventHandler[]{
                    (new CharHandler.DelimiterCharEventHandler(' ')),
                    (new CharHandler.DelimiterCharEventHandler('\t'))};
        });

        String testString = "fred goes\nhome\today\n";
        WordCounter result = getField("result");
        StringDriver.streamChars(testString, sep);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);

    }

    @Test
    public void test_wc_generic() throws Exception {
        //System.out.println("test_wc_generic");
        SEPConfig cfg = new WordCounterGeneric.Builder();
        cfg.setGenerateDescription(false);
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_generic);
    }

    @Test
    public void test_wc_generic_arrays() throws Exception {
        //System.out.println("test_wc_generic_arrays");
        SEPConfig cfg = new WordCounterGenericArrays.Builder();
        cfg.setGenerateDescription(false);
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_generic_arrays);
    }

    @Test
    public void test_wc_inline_event_handling() throws Exception {
        //System.out.println("test_wc_inline_event_handling");
        SEPConfig cfg = new WordCounterInlineEventHandler.Builder();
        cfg.setGenerateDescription(false);
        JavaTestGeneratorHelper.generateClass(cfg, test_wc_inline_event_handling);
    }

}
