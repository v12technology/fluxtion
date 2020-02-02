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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;

/**
 *
 * @author gregp
 */
public class StringPredicates {

    public static SerializableFunction<String, Boolean> is(String refString) {
        return new StringPredicates(refString)::equal;
    }

    public static SerializableFunction<String, Boolean> startsWith(String refString) {
        return new StringPredicates(refString)::startsWithString;
    }

    public static SerializableFunction<String, Boolean> contains(String refString) {
        return new StringPredicates(refString)::containsString;
    }

    public static SerializableFunction<String, Boolean> matches(String refString) {
        return new StringPredicates(refString)::matchesString;
    }
    
    public static SerializableFunction<CharSequence, Boolean> isEqual(String refString) {
        return new StringPredicates(refString)::equalCharSeq;
    }

    public String refString;

    public StringPredicates() {
    }

    public StringPredicates(String refString) {
        this.refString = refString;
    }

    public boolean equalCharSeq(CharSequence testString) {
        return refString.contentEquals(testString);
    }

    public boolean equal(String testString) {
        return refString.equals(testString);
    }

    public boolean startsWithString(String testString) {
        return refString.startsWith(testString);
    }

    public boolean containsString(String testString) {
        return refString.contains(testString);
    }

    public boolean matchesString(String regex) {
        return refString.matches(regex);
    }
}
