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
package com.fluxtion.extension.declarative.funclib.api.filter;

import com.fluxtion.extension.declarative.api.Test;


/**
 *
 * @author Greg Higgins
 */
public class BinaryPredicates {

    public static class GreaterThan implements Test {

        public boolean isGreaterThan(double op1, double op2) {
            return op1 > op2;
        }
    }

    public static class GreaterThanOrEqual implements Test {

        public boolean isGreaterThanOrEqual(double op1, double op2) {
            return op1 >= op2;
        }
    }

    public static class LessThan implements Test {

        public boolean isLessThan(double op1, double op2) {
            return op1 < op2;
        }
    }

    public static class LessThanOrEqual implements Test {

        public boolean isLessThanOrEqual(double op1, double op2) {
            return op1 <= op2;
        }
    }

    public static class EqualTo implements Test {

        public boolean isEqualTo(double op1, double op2) {
            return op1 == op2;
        }
    }

    public static class NotEqualTo implements Test {

        public boolean isNotEqualTo(double op1, double op2) {
            return op1 != op2;
        }
    }

//    public static class UpdateCount implements Test{
//        public boolean checkUpdateCount(int op1, int op2 ){
//            
//        }
//    }
}
