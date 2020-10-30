/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */
package com.fluxtion.generator;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Templates {

    private static final String PACKAGE = "template/base";
    public static final String JAVA_TEMPLATE = PACKAGE + "/javaTemplate.vsl";
    public static final String JAVA_DEBUG_TEMPLATE = PACKAGE + "/javaTemplateDebug.vsl";
    public static final String JAVA_INTROSPECTOR_TEMPLATE = PACKAGE + "/javaIntrospectorTemplate.vsl";
    public static final String JAVA_TEST_DECORATOR_TEMPLATE = PACKAGE + "/javaTestDecoratorTemplate.vsl";
}
