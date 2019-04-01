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
package com.fluxtion.ext.text.builder.ascii;

//import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Escapes a char conformant with java standards
 *
 * @author Greg Higgins
 */
public class EscapedChar {

    public final char rawChar;

    public EscapedChar(char rawChar) {
        this.rawChar = rawChar;
    }

    public String getValidJava() {
        switch (rawChar) {
            case ' ':
                return "space";
            case '\t':
                return "tab";
            case '"':
                return "doubleQuote";
            case '\'':
                return "singlequote";
            case '\n':
                return "newLine";
            case '*':
                return "multiply";
            case '/':
                return "divide";
            case '-':
                return "subtract";
            case '+':
                return "add";
        }
        if (Character.isJavaIdentifierPart(rawChar)) {
            return "" + rawChar;
        }
        return "" + (int) rawChar;
    }

    public String getEscapedChar() {
        switch (rawChar) {
            case '\'':
                return "\\'";
        }
        return StringEscapeUtils.escapeJava("" + rawChar);
    }

    public char getRawChar() {
        return rawChar;
    }

    public static List<EscapedChar> asDedupedList(String input) {
        char[] charArray = input.toCharArray();
        HashSet<Character> hashSet = new HashSet();
        for (char c : charArray) {
            hashSet.add(c);
        }
        List<EscapedChar> collect = (List<EscapedChar>) hashSet.stream()
                .map(c -> new EscapedChar((char) c))
                .collect(Collectors.toList());
        return collect;
    }

}
