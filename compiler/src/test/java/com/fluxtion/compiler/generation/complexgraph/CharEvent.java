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

import com.fluxtion.runtime.event.DefaultEvent;

/**
 * 
 * @author Greg Higgins
 */
public class CharEvent extends DefaultEvent {

	/** mutable char easy to re-use memory for demo **/
	private char character;

	public CharEvent(char id) {
		super(id);
		this.character = id;
		filterId = id;
	}

	public char getCharacter() {
		return character;
	}

	/**
	 * Setting the character will also make the filterId update as well
	 * 
	 * @param character
	 */
	public void setCharacter(char character) {
		this.character = character;
		filterId = character;
	}

}
