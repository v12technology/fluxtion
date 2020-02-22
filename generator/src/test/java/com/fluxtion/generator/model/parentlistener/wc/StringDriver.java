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

import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.api.lifecycle.StaticEventProcessor;

/**
 * 
 * @author Greg Higgins
 */
public class StringDriver {

	public static void streamChars(String testString, StaticEventProcessor sep) {
		// init
		if (sep instanceof Lifecycle) {
			((Lifecycle) sep).init();
		}
		// get bytes
		char[] chars = testString.toCharArray();
		CharEvent charEvent = new CharEvent(' ');
		// sep.onEvent(charEvent);
		// post char events
		for (char aByte : chars) {
			charEvent.setCharacter(aByte);
			sep.onEvent(charEvent);
		}
		// teardown
		if (sep instanceof Lifecycle) {
			((Lifecycle) sep).tearDown();
		}
	}
}
