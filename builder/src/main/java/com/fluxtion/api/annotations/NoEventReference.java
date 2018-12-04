/* 
 * Copyright (C) 2016-2017 V12 Technology Limited. All rights reserved. 
 *
 * This software is subject to the terms and conditions of its EULA, defined in the
 * file "LICENCE.txt" and distributed with this software. All information contained
 * herein is, and remains the property of V12 Technology Limited and its licensors, 
 * if any. This source code may be protected by patents and patents pending and is 
 * also protected by trade secret and copyright law. Dissemination or reproduction 
 * of this material is strictly forbidden unless prior written permission is 
 * obtained from V12 Technology Limited.  
 */
package com.fluxtion.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member that is part of the node set in the SEP as a reference only.
 * The child node containing this reference will not be in the call chain
 * when it has no antecedents apart from the parent reference. This allows a
 * child to refer to a parent solely as a data store.
 * 
 * @author Greg Higgins
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoEventReference {
    
}
