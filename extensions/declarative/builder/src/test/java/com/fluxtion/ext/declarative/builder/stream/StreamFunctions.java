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

import javafx.util.Pair;


/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class StreamFunctions {

    //from Strng to primitive
    public int String2Int(String s) {
        try {
            return (int) Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public double String2Double(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public Number String2Number(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean String2Boolean(String b) {
        try {
            return Boolean.valueOf(b);
        } catch (Exception e) {
            return false;
        }
    }

    //From primitve to String
    public String number2String(Number i) {
        try {
            return i.doubleValue()+ "";
        } catch (Exception e) {
            return "";
        }
    }

    public String int2String(int i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }

    public String double2String(double i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }

    public String boolean2String(boolean i) {
        try {
            return i + "";
        } catch (Exception e) {
            return "";
        }
    }
    
    //Ref to Ref
    public Pair<String, Integer> int2Pair(int val){
        return new Pair<>(int2String(val), val);
    }
    
}
