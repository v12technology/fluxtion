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
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.runtime.annotations;

/**
 * Filter match strategy for an {@link EventHandler}.
 *
 * Available strategies are:
 * <ul>
 * <li> {@link FilterType#matched} Only matching filters allow event
 * propagation
 * <li> {@link FilterType#unmatched} Invoked when no filter match is found,
 * acts like a default branch in a case statement.
 * </ul>
 *
 * @author Greg Higgins
 */
public enum FilterType {
    matched,
    unmatched,;
}
