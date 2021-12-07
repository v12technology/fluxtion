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
/**
 * Contains classes and functions that are used to construct a Fluxtion Static
 * Event Processor (SEP). Builder functions are used to:
 * <ul>
 * <li>Describe the graph
 * <li>Add nodes
 * <li>Control access scope of nodes
 * <li>Name elements in the graph
 * <li>Factory management
 * <li>Injection points for extending build functions
 * </ul>
 *
 * <h2>Class space</h2>
 * The builder classes depends upon the api module, both modules are loaded by
 * Fluxtion generator at generation time. Building is a <b>compile time only</b>
 * operation. The builder module is not referenced by the generated SEP and
 * should not be distributed with the generated SEP.<p>
 *
 * The goal is to reduce the dependencies of the final application to the
 * minimum possible. This will simplify integration, testing and deployment
 * reducing development costs. The generator and builder may have complex
 * dependencies but they are not required at runtime or in the application class
 * space.
 */
package com.fluxtion.compiler.builder;
