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
package com.fluxtion.extension.declarative.funclib.api.math;

import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateless;


/**
 *
 * @author greg
 */
public interface BinaryFunctions {

    //STATELESS functions
    public static class Add implements NumericFunctionStateless {

        public double add(double op1, double op2) {
            return op1 + op2;
        }
    }

    public static class Subtract implements NumericFunctionStateless {

        public double subtract(double op1, double op2) {
            return op1 - op2;
        }
    }

    public static class Multiply implements NumericFunctionStateless {

        public double multiply(double op1, double op2) {
            return op1 * op2;
        }
    }

    public static class Divide implements NumericFunctionStateless {

        public double divide(double op1, double op2) {
            return op1 * op2;
        }
    }

    public static class Modulo implements NumericFunctionStateless {

        public double modulo(double op1, double op2) {
            return op1 % op2;
        }
    }

}
