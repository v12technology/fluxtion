/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.complexgraph;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Greg Higgins
 */
public class MultipleGraphConstructions extends MultipleSepTargetInProcessTest {

    public MultipleGraphConstructions(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void test_wc() {
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
    public void test_wc_generic() {
        sep(c ->{
            WordCounterGeneric root = c.addPublicNode(new WordCounterGeneric(), "result");
            root.anyCharHandler = c.addNode(new CharHandler());
            root.eolHandler = c.addNode(new CharHandler.FilteredCharEventHandler('\n'));
            root.wordChardHandler = c.addNode(new CharHandler.UnMatchedCharEventHandler());
            root.spaceHandler = c.addNode(new CharHandler.FilteredCharEventHandler(' '));
            root.tabHandler = c.addNode(new CharHandler.FilteredCharEventHandler('\t'));
        });

        String testString = "fred goes\nhome\today\n";
        WordCounterGeneric result = getField("result");
        StringDriver.streamChars(testString, sep);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);

    }

    @Test
    public void test_wc_generic_arrays() throws Exception {
        sep(c ->{
            WordCounterGenericArrays root = c.addPublicNode(new WordCounterGenericArrays(), "result");
            root.anyCharHandler = c.addNode(new CharHandler());
            root.eolHandler = c.addNode(new CharHandler.FilteredCharEventHandler('\n'));
            root.wordChardHandler = c.addNode(new CharHandler.UnMatchedCharEventHandler());
            root.delimiterHandlers = new CharHandler.FilteredCharEventHandler[]{
                    c.addNode(new CharHandler.FilteredCharEventHandler(' ')),
                    c.addNode(new CharHandler.FilteredCharEventHandler('\t'))};
        });

        String testString = "fred goes\nhome\today\n";
        WordCounterGenericArrays result = getField("result");
        StringDriver.streamChars(testString, sep);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);

    }

    @Test
    public void test_wc_inline_event_handling() throws Exception {
        sep(c ->{
            c.addPublicNode(new WordCounterInlineEventHandler(), "result");
        });

        String testString = "fred goes\nhome\today\n";
        WordCounterInlineEventHandler result = getField("result");
        StringDriver.streamChars(testString, sep);
        //System.out.println(result.toString());
        assertEquals(20, result.charCount);
        assertEquals(4, result.wordCount);
        assertEquals(2, result.lineCount);
    }

}
