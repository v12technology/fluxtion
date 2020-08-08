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
package com.fluxtion.ext.text.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a bean field to use a converter method that will convert from a field
 * to a CharSequence that will be written by the marshaller
 *
 * <pre>
 * //SPECIFY MARSHALL METHOD
 * @ConvertToCharSeq("com.fluxtion.ext.futext.builder.csv.AnnotatedBeanCsvTest#marshall")
 * protected MyType stringValue;
 *
 * //Actual marshal method
 *     public static void marshall(MyType field, Appendable msgSink){
 *      try {
 *          msgSink.append("OVERWRITTEN_" + field);
 *      } catch (IOException ex) {
 *          Logger.getLogger(AnnotatedBeanCsvTest.class.getName()).log(Level.SEVERE, null, ex);
 *      }
 *     }
 *
 *
 *
 * <pre>
 *
 * @author gregp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConvertToCharSeq {

    /**
     * The static method that will convert this field into a {@link CharSequence}
     *
     * The syntax is: [fully qualified class name]#[method name]
     *
     * @return the fqn of the converter method
     */
    String value();
}
