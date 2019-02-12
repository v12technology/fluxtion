/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.declarative.builder.stream;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFunctions {

    public int String2Int(String s) {
        return (int) Double.parseDouble(s);
    }

    public double String2Double(String s) {
        return Double.parseDouble(s);
    }

    public Number String2Number(String s) {
        return Double.parseDouble(s);
    }

    public String int2String(int i) {
        return i + "";
    }

    public String number2String(Number i) {
        return i.intValue() + "";
    }
    
    public boolean String2Boolean(String b){
        return Boolean.valueOf(b);
    }

}
