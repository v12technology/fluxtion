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
 *
 * @author gregp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvMarshaller {

    /**
     * the name of the package the generated artifacts will be written to.
     *
     * @return package name
     */
    String packageName() default "";

    /**
     * Re-use the same bean instance for each record or create a new instance on
     * every record. Default is to re-use the same instance
     *
     * @return flag new instance per record
     */
    boolean newBeanPerRecord() default false;

    /**
     * Add an event publihser to the generated parser so bean records can be
     * chained into another static event processor
     *
     * @return flag to add a chaining event publisher
     */
    boolean addEventPublisher() default true;

    /**
     * number of header lines in the file
     *
     * @return
     */
    int headerLines() default 1;

    /**
     * ignore comment lines, comment char = '#'
     *
     * @return skip comment lines
     */
    boolean skipCommentLines() default true;

    /**
     * process escape sequences, slows the parser down
     *
     * @return
     */
    boolean processEscapeSequences() default false;

    /**
     * Ignore empty lines in supplied file, otherwise report a validation error
     * on empty lines.
     *
     * @return ignore empty lines
     */
    boolean skipEmptyLines() default true;

    /**
     * The row containing the column identifiers for this bean marshaller
     *
     * @return the column to property mapping row
     */
    int mappingRow() default 1;

    /**
     * The line ending character for the input file. default is '\n'
     *
     * @return
     */
    char lineEnding() default '\n';

    /**
     * The field separator character, default is ','
     *
     * @return
     */
    char fieldSeparator() default ',';

    /**
     * Ignore character, useful for processing windows style line endings
     * default is '\r'
     *
     * @return
     */
    char ignoredChars() default '\r';
    
    /**
     * Process and validate records that are missing fields, default is false
     * 
     * @return validate a record with missing field
     */
    boolean acceptPartials() default false;

}
