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
package com.fluxtion.ext.text.builder;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Templates {

    private static final String PACKAGE = "template/dev/funclib";
//    private static final String PACKAGE = "template/";

    public static final String ANYCHAR_MATCH_FILTER = PACKAGE + "/filter/AnyCharMatchFilterTemplate.vsl";
    public static final String CHAR_MATCH_FILTER = PACKAGE + "/filter/AsciiMatchFIlterTemplate.vsl";
    public static final String CSV_MARSHALLER = PACKAGE + "/csv/CsvMarshallerTemplate.vsl";
//HELPERS
    public static final String UNARY_TEMPLATE = PACKAGE + "/helper/UnaryNumericFunctionHelperTemplate.vsl";
    public static final String BINARY_TEMPLATE = PACKAGE + "/helper/BinaryNumericFunctionHelperTemplate.vsl";
    public static final String BINARY_FILTER_TEMPLATE = PACKAGE + "/helper/BinaryNumericFilterHelperTemplate.vsl";

}
