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

import com.fluxtion.runtime.annotations.OnParentUpdate;

/**
 * @author Greg Higgins
 */
public class WordCounterGeneric {

    public CharHandler anyCharHandler;
    public CharHandler.FilteredCharEventHandler spaceHandler;
    public CharHandler.FilteredCharEventHandler tabHandler;
    public CharHandler.FilteredCharEventHandler eolHandler;
    public CharHandler.UnMatchedCharEventHandler wordChardHandler;

    public int wordCount;
    public int charCount;
    public int lineCount;
    public int increment = 1;

    @OnParentUpdate
    public void onAnyChar(CharHandler anyCharHandler) {
        charCount++;
    }

    @OnParentUpdate("tabHandler")
    public void onTabDelimiter(
            CharHandler.FilteredCharEventHandler delimiterHandler) {
        increment = 1;
    }

    @OnParentUpdate("spaceHandler")
    public void onSpaceDelimiter(
            CharHandler.FilteredCharEventHandler delimiterHandler) {
        increment = 1;
    }

    @OnParentUpdate("eolHandler")
    public void onEol(CharHandler.FilteredCharEventHandler eolHandler) {
        lineCount++;
        increment = 1;
    }

    @OnParentUpdate
    public void onUnmatchedChar(
            CharHandler.UnMatchedCharEventHandler wordChardHandler) {
        wordCount += increment;
        increment = 0;
    }

    @Override
    public String toString() {
        return "wc\n" + "charCount:" + charCount + "\nwordCount:" + wordCount
                + "\nlineCount:" + lineCount;
    }

}
