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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.lifecycle;

/**
 * An invoker inteface used in the generated sep to invoke a call tree. Can be 
 * useful when switch statements become too big and a map dispatcher is used to 
 * invoke the specific call tree.
 * 
 * RELOCATE - not required here
 * 
 * @author Greg Higgins
 */
public interface FilteredHandlerInvoker {
		public void invoke( Object event );
}
