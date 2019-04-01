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
package com.fluxtion.ext.text.builder.csv;

/**
 *
 * @author gregp
 */

/**
 * Configuration parameters for line endings, separators, ignore characters
 * 
 * @author gregp
 */
public class CharTokenConfig {
    private char lineEnding;
    private char fieldSeparator;
    private char ignoredChars;
    
    public static final CharTokenConfig WINDOWS = new CharTokenConfig('\n',',','\r');
    public static final CharTokenConfig UNIX = new CharTokenConfig('\n',',');

    public CharTokenConfig(char lineEnding, char fieldSeparator, char ignoredChars) {
        this.lineEnding = lineEnding;
        this.fieldSeparator = fieldSeparator;
        this.ignoredChars = ignoredChars;
    }

    public CharTokenConfig(char lineEnding, char fieldSeparator) {
        this(lineEnding, fieldSeparator, '\u0000');
    }

    public CharTokenConfig() {
    }

    public char getLineEnding() {
        return lineEnding;
    }

    public void setLineEnding(char lineEnding) {
        this.lineEnding = lineEnding;
    }

    public char getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public char getIgnoredChars() {
        return ignoredChars;
    }

    public void setIgnoredChars(char ignoredChars) {
        this.ignoredChars = ignoredChars;
    }
    
    
    
}
