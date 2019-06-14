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
package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class CharSeqFunctions {

    public static <T extends CharSequence> SerializableFunction<T, CharSequence> subSequence(int start, int end) {
        return new SubSeq(start, end)::subSequence;
    }
    
    public static <T extends CharSequence> SerializableFunction<T, String> subString(int start, int end) {
        return new SubSeq(start, end)::subString;
    }
    
    public static <T extends CharSequence> SerializableFunction<T, CharSequence> subSeqBefore(char serachChar) {
        return new SubSeqBefore(serachChar)::subSequence;
    }
    
    public static <T extends CharSequence> SerializableFunction<T, String> subStringBefore(char serachChar) {
        return new SubSeqBefore(serachChar)::subString;
    }

    public static class SubSeq {

        final int start;
        final int end;

        public SubSeq(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public CharSequence subSequence(CharSequence cs) {
            return cs.subSequence(start, end);
        }

        public String subString(CharSequence cs) {
            return subSequence(cs).toString();
        }
    }

    public static class SubSeqBefore {

        final char search;

        public SubSeqBefore(char search) {
            this.search = search;
        }

        public CharSequence subSequence(CharSequence cs) {
            int idx = 0;
            for (; idx < cs.length(); idx++) {
                char c = cs.charAt(idx);
                if (c == ',') {
                    break;
                }
            }
            return cs.subSequence(0, idx);
        }
        
        public String subString(CharSequence cs){
            return subSequence(cs).toString();
        }
    }

}
