/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.learning.streaming;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.event.Signal;
import com.fluxtion.ext.streaming.api.stream.NumericPredicates;
import com.fluxtion.ext.streaming.api.stream.NumericPredicates.FilterConfig;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import com.fluxtion.learning.streaming.SelectTest.MyDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    
    @Test
    public void dynamicFiltering(){
        sep(c -> {
            select(Double.class)
                .filter(gt(10, "configKey"))
                .log("dynamic filter exceeded");
        });   
        onEvent("world");
        onEvent(8.0);
        onEvent(20.0);
        onEvent(50.0);
        onEvent(new FilterConfig("configKey", 25));
        onEvent(20.0);
        onEvent(50.0);
    }
    
    @Test
    public void dynamicUserFiltering(){
        sep(c -> {
            select(Double.class)
                .filter(Double::isFinite)
                .filter(new FilterGT(10)::gt)
                .log("dynamic filter exceeded val:{}", Double::intValue);
        });   
        onEvent("world");
        onEvent(8.0);
        onEvent(20.0);
        onEvent(Double.NaN);
        onEvent(50.0);
        onEvent(new Signal("myConfigKey", 25));
        onEvent(20.0);
        onEvent(50.0);
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FilterGT{
        private int minValue;
        
        public boolean gt(Number n){
            return n.longValue() > minValue;
        }
        
        @EventHandler(filterString = "myConfigKey", propagate = false)
        public void updateFilter(Signal<Number> filterSignal){
            minValue = filterSignal.getValue().intValue();
        } 
    }

}
