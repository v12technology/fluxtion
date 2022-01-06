/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
/**
 * This package contains a set of annotations providing meta-data to the Fluxtion
 * static event compiler.<p>
 *
 * Compiled user classes are inspected for annotations to generate meta-data.
 * Class
 * information, meta-data and configuration are all extracted by the Fluxtion
 * Static Event
 * Compiler (SEC). The SEC uses the combined information to generate a Static
 * Event
 * Processor (SEP)
 * .<p>
 *
 * The annotations are only used at build time and are not used at runtime by
 * the
 * generated SEP.<p>
 *
 * Annotations can be classified in the following categories:
 * <ul>
 * <li>Construction - used to bind nodes together in the execution graph
 * <li>Event propagation - determine how events are propagted to individual
 * nodes.
 * <li>Lifecycle - binding nodes to lifecycle phases in the SEP
 * <li>Generation config - Providing instructions to guide the Fluxtion Static
 * Event Compiler
 * </ul>
 *<p>
 * A SEP
 * processes event handling methods in two phases:
 * <ul>
 * <li>Event in phase - processes handler methods in topological order
 * <li>After event phase - processes handler methods in reverse topological
 * order
 * </ul>
 * <p>
 * The after event phase gives the node a chance to safely remove state after an event
 * cycle.
 */
package com.fluxtion.runtime.annotations;
