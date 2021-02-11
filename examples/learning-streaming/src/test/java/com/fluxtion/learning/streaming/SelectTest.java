/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.learning.streaming;

import com.fluxtion.ext.streaming.builder.factory.EventSelect;

import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;

import com.fluxtion.generator.util.BaseSepInprocessTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

/**
 * Tests demonstrating the use of {@link EventSelect#select(java.lang.Class) } methods. These examples should be read in
 * conjunction with <a href="https://v12technology.github.io/fluxtion/learning/learning-into.html">introduction to streaming</a>
 *
 * @author gregp
 */
@SuppressWarnings("unchecked")
public class SelectTest extends BaseSepInprocessTest {

    @Test
    public void selectOnly() {
        sep(c -> select(MyDataType.class));
    }

    @Test
    public void selectLogEvent() {
        sep(c -> select(MyDataType.class)
                .log("received:")
        );
        onEvent(new MyDataType("hello", "world"));
    }

    @Test
    public void selectLogValues() {
        sep(c -> select(MyDataType.class)
                .log("received key:{} value:{}", MyDataType::getKey, MyDataType::getValue)
        );
        onEvent(new MyDataType("hello", "world"));
    }

    @Test
    public void selectMultipleStreams() {
        sep(c -> {
            select(MyDataType.class)
                    .log("myDataStream received:");
            select(Double.class)
                    .log("doubleStream received:");
        });
        onEvent(new MyDataType("hello", "world"));
        onEvent(42.0);
        onEvent(42);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyDataType {

        String key;
        String value;
    }
}
