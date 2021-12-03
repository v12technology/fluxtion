/*
 * Copyright (C) 2020 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.function;

import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import com.fluxtion.ext.streaming.api.numeric.NumericSignal;
import lombok.Value;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.factory.DefaultNumberBuilder.defaultVal;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.subtract;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author V12 Technology Ltd.
 */
public class DefaultNumberTest extends StreamInProcessTest {

    @Test
    public void defaultNumber() {
        sep((c) -> {
            subtract(defaultVal(55, "key_b"), defaultVal(12, Sale::getAmountSold)).id("result");
        });
//        sep(com.fluxtion.ext.declarative.builder.function.defaultValbertest_defaultValber_1605386238496.TestSep_defaultValber.class);
        
        Number result = getWrappedField("result");
        assertThat(result.intValue(), is(0));
        onEvent(new NumericSignal(20, "key_b"));
        assertThat(result.intValue(), is(8));
        onEvent(new Sale(50));
        assertThat(result.intValue(), is(-30));
        
        init();
        result = getWrappedField("result");
        assertThat(result.intValue(), is(0));
        onEvent(new Sale(50));
        assertThat(result.intValue(), is(5));
    }
    
    @Test
    public void defaultNumberX() {
        sep((c) -> {
            subtract(defaultVal(55, "key_b"), select(Sale::getAmountSold).defaultVal(12)).id("result");
        });
//        sep(com.fluxtion.ext.declarative.builder.function.defaultValbertest_defaultValber_1605386238496.TestSep_defaultValber.class);
        
        Number result = getWrappedField("result");
        assertThat(result.intValue(), is(0));
        onEvent(new NumericSignal(20, "key_b"));
        assertThat(result.intValue(), is(8));
        onEvent(new Sale(50));
        assertThat(result.intValue(), is(-30));
        
        init();
        result = getWrappedField("result");
        assertThat(result.intValue(), is(0));
        onEvent(new Sale(50));
        assertThat(result.intValue(), is(5));
    }

    @Value
    public static class Sale {

        int amountSold;
    }

}
