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
package com.fluxtion.compiler.generation.complexgraph;

import com.fluxtion.runtime.annotations.FilterType;
import com.fluxtion.runtime.annotations.OnEventHandler;

/**
 * @author Greg Higgins
 */
public class WordCounterInlineEventHandler {

    public transient int wordCount;
    public transient int charCount;
    public transient int lineCount;
    private int increment = 1;

    @OnEventHandler
    public boolean onAnyChar(CharEvent event) {
        charCount++;
        return true;
    }

    @OnEventHandler(filterId = '\t')
    public boolean onTabDelimiter(CharEvent event) {
        increment = 1;
        return true;
    }

    @OnEventHandler(filterId = ' ')
    public boolean onSpaceDelimiter(CharEvent event) {
        increment = 1;
        return true;
    }

    @OnEventHandler(filterId = '\n')
    public boolean onEol(CharEvent event) {
        lineCount++;
        increment = 1;
        return true;
    }

    @OnEventHandler(FilterType.defaultCase)
    public boolean onUnmatchedChar(CharEvent event) {
        wordCount += increment;
        increment = 0;
        return true;
    }

    @Override
    public String toString() {
        return "wc\n" + "charCount:" + charCount + "\nwordCount:" + wordCount
                + "\nlineCount:" + lineCount;
    }

}
