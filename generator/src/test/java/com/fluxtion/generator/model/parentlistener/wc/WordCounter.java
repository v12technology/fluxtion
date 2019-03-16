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
package com.fluxtion.generator.model.parentlistener.wc;

import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.builder.node.SEPConfig;

/**
 * 
 * @author Greg Higgins
 */
public class WordCounter {

	public CharHandler anyCharHandler;
	public CharHandler.DelimiterCharEventHandler[] delimiterHandlers;
	public CharHandler.EolCharEventHandler eolHandler;
	public CharHandler.UnMatchedCharEventHandler wordChardHandler;

	public int wordCount;
	public int charCount;
	public int lineCount;
	public int increment = 1;

	@OnParentUpdate
	public void onAnyChar(CharHandler anyCharHandler) {
		charCount++;
	}

	@OnParentUpdate
	public void onDelimiter(
			CharHandler.DelimiterCharEventHandler delimiterHandler) {
		increment = 1;
	}

	@OnParentUpdate
	public void onEol(CharHandler.EolCharEventHandler eolHandler) {
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

	public static class Builder extends SEPConfig {

		{
			WordCounter root = addPublicNode(new WordCounter(), "result");
			root.anyCharHandler = addNode(new CharHandler());
			root.eolHandler = addNode(new CharHandler.EolCharEventHandler('\n'));
			root.wordChardHandler = addNode(new CharHandler.UnMatchedCharEventHandler());
			root.delimiterHandlers = new CharHandler.DelimiterCharEventHandler[]{
					addNode(new CharHandler.DelimiterCharEventHandler(' ')),
					addNode(new CharHandler.DelimiterCharEventHandler('\t'))};
		}
	}

}
