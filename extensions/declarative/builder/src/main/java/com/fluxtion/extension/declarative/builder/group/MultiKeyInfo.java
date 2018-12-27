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
package com.fluxtion.extension.declarative.builder.group;

import com.fluxtion.extension.declarative.api.group.MultiKey;
import com.fluxtion.extension.declarative.api.numeric.MutableNumericValue;
import com.fluxtion.extension.declarative.builder.util.ImportMap;
import java.lang.reflect.Method;

/**
 * Meta data required to build a multi valued key, used in group by.
 *
 * @author Greg Higgins
 */
public class MultiKeyInfo {

    private String id;
    private Method sourceMethod;
    private String sourceMethodName;
    private String sourceType;
    private String mutNum = MutableNumericValue.class.getSimpleName();
    public final ImportMap importMap;

    public MultiKeyInfo(ImportMap importMap) {
        this.importMap = importMap;
        importMap.addImport(MutableNumericValue.class);
        importMap.addImport(MultiKey.class);
    }

    public void setSource(Method sourceMethod, String id) {
        this.id = id;
        this.sourceMethod = sourceMethod;
        this.sourceMethodName = sourceMethod.getName();
        this.sourceType = importMap.addImport(this.sourceMethod.getReturnType());//.getCanonicalName();
    }

    public String updateKeyMethod(String targetId) {
        String set = id + " = " + targetId + "." + sourceMethodName + "()";
        if (isNumeric()) {
            set = id + ".set(" + targetId + "." + sourceMethodName + "())";
        }
        return set;
    }

    public String initMethod(String targetId) {
        String set = sourceType + " " + id;
        if (isNumeric()) {
            set = mutNum + " " + id + " = new " + mutNum + "()";
        }
        return set;
    }

    public String resetMethod() {
        String set = id + " = null";
        if (isNumeric()) {
            set = id + ".set(0)";
        }
        return set;
    }

    public String copyMethod(String copyId) {
        String set = copyId + "." + id + " = " + id;
        if (isNumeric()) {
            set = copyId +"." + id + ".copyFrom(" + id + ")" ;
        }
        return set;
    }

    private boolean isNumeric() {
        return this.sourceMethod.getReturnType().isPrimitive();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "MultiKeyInfo{" + "id=" + id + ", sourceMethod=" + sourceMethod + ", sourceMethodName=" + sourceMethodName + ", sourceType=" + sourceType +'}';
    }

}
