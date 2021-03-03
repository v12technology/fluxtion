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
package com.fluxtion.ext.streaming.builder.util;

import com.fluxtion.builder.generation.GenerationContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A holder of imports for a java class. Classes are added with the addImport 
 * method, this returns the String classname to be used in code. If the class name
 * is already in use then the returned value will be the fqn.
 * @author Greg Higgins
 */
public class ImportMap {

    private final Set<Class> importedClassSet;
    private final Set<Class> staticImportedClassSet;

    private ImportMap() {
        importedClassSet = new HashSet<>();
        staticImportedClassSet = new HashSet<>();
    }

    public static ImportMap newMap() {
        return new ImportMap();
    }

    public static ImportMap newMap(Class... clazzes) {
        final ImportMap importMap = new ImportMap();
        for (Class clazz : clazzes) {
            importMap.addImport(clazz);
        }
        return importMap;
    }

    public String addImport(Class clazz) {
        String className = clazz.getEnclosingClass()==null?
                clazz.getCanonicalName():clazz.getEnclosingClass().getCanonicalName();
        String simpleName = clazz.getSimpleName();
        String pkgName = "";
        try{
            pkgName = GenerationContext.SINGLETON.getPackageName();
        }catch (Exception e){ }
        pkgName = pkgName==null?"":pkgName;
        if(clazz.isPrimitive() || className.startsWith("java.lang") 
                || pkgName.startsWith(className)){
            return simpleName;
        }
        if (importedClassSet.contains(clazz)) {
            className = clazz.getSimpleName();
        }else if(importedClassSet.stream().map(c -> c.getSimpleName()).noneMatch(name -> name.equals(simpleName))){
            importedClassSet.add(clazz);
            className = clazz.getSimpleName();
        }
        return className;
    }
    
    public void addStaticImport(Class clazz){
        staticImportedClassSet.add(clazz);
    }

    /**
     * the imports as String list.
     * @return 
     */
    public List<String> asString() {
        final List<String> list = importedClassSet.stream().map(c -> c.getCanonicalName()).collect(Collectors.toList());
        list.addAll(staticImportedClassSet.stream().map(c -> "static " + c.getCanonicalName() + ".*").collect(Collectors.toList()));
        Collections.sort(list);
        return list;
    }

    /**
     * The imports as a set of classes.
     * @return 
     */
    public Set<Class> getImportedClassSet() {
        return Collections.unmodifiableSet(importedClassSet);
    }
    
    
}
