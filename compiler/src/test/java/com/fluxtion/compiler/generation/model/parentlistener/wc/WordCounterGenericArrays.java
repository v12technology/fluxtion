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
package com.fluxtion.compiler.generation.model.parentlistener.wc;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.model.parentlistener.wc.CharHandler.FilteredCharEventHandler;
import com.fluxtion.compiler.generation.model.parentlistener.wc.CharHandler.UnMatchedCharEventHandler;

/**
 *
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

    public static class Builder extends SEPConfig {

        {
            WordCounterGenericArrays root = addPublicNode(new WordCounterGenericArrays(), "result");
            root.anyCharHandler = addNode(new CharHandler());
            root.eolHandler = addNode(new FilteredCharEventHandler('\n'));
            root.wordChardHandler = addNode(new UnMatchedCharEventHandler());
            root.delimiterHandlers = new FilteredCharEventHandler[]{
                addNode(new FilteredCharEventHandler(' ')),
                addNode(new FilteredCharEventHandler('\t'))};

        }
    }

}
