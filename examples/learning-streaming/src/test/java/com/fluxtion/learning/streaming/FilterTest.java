/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.learning.streaming;

import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import com.fluxtion.learning.streaming.SelectTest.MyDataType;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class FilterTest extends BaseSepInprocessTest {

    @Test
    public void filterEvent() {
        sep(c -> {
            select(String.class)
                .filter("warning"::equalsIgnoreCase)
                .log("warning received");
        });
        onEvent("world");
        onEvent("warning");
    }

    @Test
    public void filterLambda() {
        sep(c -> {
            select(Double.class)
                .filter(d -> d > 10)
                .log("input {} > 10");
        });
        onEvent(2.0);
        onEvent(42.0);
        onEvent(92.0);
        onEvent("warning");
    }
    
    @Test
    public void filterMethodRef(){
        sep(c -> {
            select(MyDataType.class)
                .filter(FilterTest::isValid)
                .log("warning received");
        });
        onEvent("world");
        onEvent("warning");
    }

    public static boolean isValid(MyDataType myDataType){
        return myDataType.getKey().equals("hello") && myDataType.getValue().equals("world");
    }
    
    @Test
    public void filterChain() {
        sep(c -> {
            select(Double.class)
                .filter(d -> d > 10)
                .log("input {} > 10")
                .filter(d -> d > 60)
                .log("input {} > 60")
                ;
        });
        onEvent("world");
        onEvent(2.0);
        onEvent(42.0);
        onEvent(92.0);
        onEvent("warning");
        onEvent(42);
    }
    
    @Test
    public void filterElse() {
        sep(c -> {
            select(Double.class)
                .filter(d -> d > 10)
                .filter(d -> d > 60)
                .log("input {} > 60")
                .elseStream().log("input {} between 10 -> 60 ");
        });
        onEvent("world");
        onEvent(2.0);
        onEvent(42.0);
        onEvent(92.0);
        onEvent("warning");
        onEvent(42);
    }

}
