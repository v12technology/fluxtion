/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.ext.text.api.csv;

import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.streaming.api.util.CharArrayCharSequence;
import static com.fluxtion.ext.text.api.ascii.Conversion.atoi;
import static java.beans.Introspector.decapitalize;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Converters {

    public static SerializableFunction<CharSequence, Number> defaultInt(int val) {
        return new IntConverter(val)::defaultVal;
    }

    public static SerializableFunction<CharSequence, CharSequence> defaultCharSequence(String val) {
        return new DefaultValue(val)::defaultVal;
    }

    /**
     * Converts a String to a valid java identifier, removing all invalid characters
     * @param str
     * @return 
     */
    public static String getIdentifier(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if ((i == 0 && Character.isJavaIdentifierStart(str.charAt(i))) || (i > 0 && Character.isJavaIdentifierPart(str.charAt(i)))) {
                sb.append(str.charAt(i));
            }
        }
        return decapitalize(sb.toString()).trim();
    }

    
    
    public static class DefaultValue {

        private final String val;

        public DefaultValue(String val) {
            this.val = val;
        }

        public CharSequence defaultVal(CharSequence seq) {
            if (seq.length() < 1) {
                return val;
            }
            return seq;
        }

    }
    
    public static class IntConverter {

        private final int val;

        public IntConverter(int val) {
            this.val = val;
        }

        public int defaultVal(CharSequence seq) {
            if (seq.length() < 1) {
                return val;
            }
            return atoi(seq, val);
        }

    }

    public static String intern(CharSequence seq) {
        return ((CharArrayCharSequence.CharSequenceView) seq).intern();
    }
}
