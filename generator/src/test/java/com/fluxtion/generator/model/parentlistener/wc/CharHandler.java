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
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.model.parentlistener.wc;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.FilterId;
import com.fluxtion.api.annotations.FilterType;

/**
 * 
 * @author Greg Higgins
 */
public class CharHandler {

	@EventHandler
	public void onCharEvent(CharEvent event) {
	}

	public static class EolCharEventHandler extends CharHandler {

		@FilterId
		private int charId;

		public EolCharEventHandler(char character) {
			charId = character;
		}

		public EolCharEventHandler() {
		}
	}

	public static class DelimiterCharEventHandler extends CharHandler {

		@FilterId
		private int charId;

		public DelimiterCharEventHandler(char character) {
			charId = character;
		}

		public DelimiterCharEventHandler() {
		}
	}
    
	public static class FilteredCharEventHandler extends CharHandler {

		@FilterId
		private int charId;

		public FilteredCharEventHandler(char character) {
			charId = character;
		}

		public FilteredCharEventHandler() {
		}
	}

	public static class UnMatchedCharEventHandler {

		@EventHandler(FilterType.unmatched)
		public void onCharEvent(CharEvent event) {
		}
	}

}
