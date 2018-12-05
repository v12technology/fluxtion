/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.test.tracking;

import com.fluxtion.api.annotations.EventHandler;

/**
 *
 * @author Greg Higgins
 */
public class Handler_TraceEvent_PrivateMembers {
        
    //scalar types
    private final String private_str;
    private final int private_int;
    private final char private_char;
    private final Handler_TraceEvent_PrivateMembers parent;
    private final Currency currencyEnum;
    //some transients
    private final transient String transient_str;
    private final transient int transient_int;
    //arrays
    private final int[] private_int_array;
    private final Handler_TraceEvent_PrivateMembers[] arrayRef;
    
    public Handler_TraceEvent_PrivateMembers(
            String private_str, String transient_str, 
            int private_int, int transient_int,
            char private_char,
            Handler_TraceEvent_PrivateMembers parent,
            Currency currencyEnum,
            int[] private_int_array,
            Handler_TraceEvent_PrivateMembers[] arrayRef) {
        this.private_str = private_str;
        this.transient_str = transient_str;
        this.private_int = private_int;
        this.transient_int = transient_int;
        this.private_char = private_char;
        this.parent = parent;
        this.currencyEnum = currencyEnum;
        this.private_int_array = private_int_array;
        this.arrayRef = arrayRef;
    }

    @EventHandler
    public void handleEvent(TraceEvent_0 event){
    }

    public String getPrivate_str() {
        return private_str;
    }

    public String getTransient_str() {
        return transient_str;
    }

    public int getPrivate_int() {
        return private_int;
    }

    public int getTransient_int() {
        return transient_int;
    }

    public char getPrivate_char() {
        return private_char;
    }

    public Handler_TraceEvent_PrivateMembers getParent() {
        return parent;
    }

    public Currency getCurrencyEnum() {
        return currencyEnum;
    }

    public int[] getPrivate_int_array() {
        return private_int_array;
    }

    public Handler_TraceEvent_PrivateMembers[] getArrayRef() {
        return arrayRef;
    }
    
    public enum Currency {EUR, USD, JPY, GBP};
}
