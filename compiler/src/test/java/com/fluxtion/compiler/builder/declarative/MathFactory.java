/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.compiler.builder.declarative;

/**
 *
 * @author gregp
 */
public class MathFactory{}

//implements SepConfigGenerator<FunctionCfg> {
//
//    public static class Value{
//        private final MyPredefinedNode node;
//
//        public Value(MyPredefinedNode node) {
//            this.node = node;
//        }
//
//        @OnTrigger
//        public void evauateMax(){
//
//        }
//
//    }
//
//
//    public static Value max(MyPredefinedNode node){
//        return new Value(node);
//    }
//
//
//    @Override
//    public String sepConfigStatement(FunctionCfg cfg, String node, List<String> imports) {
//        String code = node + " = com.fluxtion.creator.MathFactory.max("
//                + cfg.getMethodRef() + ");\n" ;
//        return code;
//    }
//
//
//    public static class FunctionCfg{
//        String functionName;
//        String methodRef;
//
//        public FunctionCfg() {
//        }
//
//        public FunctionCfg(String functionName, String methodRef) {
//            this.functionName = functionName;
//            this.methodRef = methodRef;
//        }
//
//        public String getFunctionName() {
//            return functionName;
//        }
//
//        public void setFunctionName(String functionName) {
//            this.functionName = functionName;
//        }
//
//        public String getMethodRef() {
//            return methodRef;
//        }
//
//        public void setMethodRef(String methodRef) {
//            this.methodRef = methodRef;
//        }
//
//        @Override
//        public String toString() {
//            return "FunctionCfg{" + "functionName=" + functionName + ", methodRef=" + methodRef + '}';
//        }
//
//    }
//
//
//}
