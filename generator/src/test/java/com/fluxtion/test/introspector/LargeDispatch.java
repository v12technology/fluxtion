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
package com.fluxtion.test.introspector;

import com.fluxtion.api.lifecycle.FilteredHandlerInvoker;
import java.util.HashMap;

/**
 *
 * @author Greg Higgins
 */
public class LargeDispatch {
    
//int filter maps
	private final HashMap<Integer, FilteredHandlerInvoker> dispatchIntMapCharEvent = initdispatchIntMapCharEvent();

//String filter maps
	private final HashMap<String, FilteredHandlerInvoker> dispatchStringMapCharEvent = initdispatchStringMapCharEvent();

	private final HashMap<String, FilteredHandlerInvoker> dispatchStringMapTimeEvent = initdispatchStringMapTimeEvent();

	private HashMap<Integer, FilteredHandlerInvoker> initdispatchIntMapCharEvent(){
		HashMap<Integer, FilteredHandlerInvoker> dispatchMap = new HashMap<>();
		dispatchMap.put(65, new FilteredHandlerInvoker() {

			@Override
			public void invoke(Object event) {
				handle_CharEvent_65((com.fluxtion.test.event.CharEvent)event);
			}
		});		
		return dispatchMap;
	}

	private HashMap<String, FilteredHandlerInvoker> initdispatchStringMapCharEvent(){
		HashMap<String, FilteredHandlerInvoker> dispatchMap = new HashMap<>();
		dispatchMap.put("fred", new FilteredHandlerInvoker() {

			@Override
			public void invoke(Object event) {
				handle_CharEvent_fred((com.fluxtion.test.event.CharEvent)event);
			}
		});		
		return dispatchMap;
	}

	private HashMap<String, FilteredHandlerInvoker> initdispatchStringMapTimeEvent(){
		HashMap<String, FilteredHandlerInvoker> dispatchMap = new HashMap<>();
		dispatchMap.put("time", new FilteredHandlerInvoker() {

			@Override
			public void invoke(Object event) {
				handle_TimeEvent_time((com.fluxtion.test.event.TimeEvent)event);
			}
		});		
		return dispatchMap;
	}

	private void handle_CharEvent_65(com.fluxtion.test.event.CharEvent typedEvent){
		//method body - invoke call tree
	}

	private void handle_CharEvent_fred(com.fluxtion.test.event.CharEvent typedEvent){
		//method body - invoke call tree
	}

	private void handle_TimeEvent_time(com.fluxtion.test.event.TimeEvent typedEvent){
		//method body - invoke call tree
	}
}
