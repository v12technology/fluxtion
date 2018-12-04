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
package com.fluxtion.api.generation;

/**
 * Allowing users to extend the generation of the SEP with customisable variable
 * names for nodes.
 * 
 * Users implement this interface and register with the SEP generator before
 * generation time. 
 * 
 * A default naming strategy will be used if the registered NodeNameProducer 
 * returns null.
 * 
 * @author Greg Higgins 
 */
public interface NodeNameProducer {
    String mappedNodeName(Object nodeToMap);
}
