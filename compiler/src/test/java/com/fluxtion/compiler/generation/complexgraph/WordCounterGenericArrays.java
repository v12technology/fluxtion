/*
 * Copyright (c) 2019, 2024 gregory higgins.
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

import com.fluxtion.compiler.generation.complexgraph.CharHandler.FilteredCharEventHandler;
import com.fluxtion.compiler.generation.complexgraph.CharHandler.UnMatchedCharEventHandler;
import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * @author Greg Higgins
 */
public class WordCounterGenericArrays {

    public CharHandler anyCharHandler;
    public FilteredCharEventHandler[] delimiterHandlers;
    public FilteredCharEventHandler eolHandler;
    public UnMatchedCharEventHandler wordChardHandler;

    public transient int wordCount;
    public transient int charCount;
    public transient int lineCount;
    private int increment = 1;

    @OnParentUpdate
    public void onAnyChar(CharHandler anyCharHandler) {
        charCount++;
    }

    @OnParentUpdate("delimiterHandlers")
    public void onDelimiter(FilteredCharEventHandler delimiterHandler) {
        increment = 1;
    }

    @OnParentUpdate("eolHandler")
    public void onEol(FilteredCharEventHandler eolHandler) {
        lineCount++;
        increment = 1;
    }

    @OnParentUpdate
    public void onUnmatchedChar(UnMatchedCharEventHandler wordChardHandler) {
        wordCount += increment;
        increment = 0;
    }

    @Override
    public String toString() {
        return "wc\n" + "charCount:" + charCount + "\nwordCount:" + wordCount
                + "\nlineCount:" + lineCount;
    }
}
