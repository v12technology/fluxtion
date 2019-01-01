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
package com.fluxtion.extension.declarative.builder.function;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateless;
import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateful;
import com.fluxtion.extension.declarative.builder.util.ArraySourceInfo;
import com.fluxtion.api.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.outputClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.sourceMappingList;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.targetClass;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.targetMethod;
import com.fluxtion.extension.declarative.builder.factory.NumericValuePushFactory;
import com.fluxtion.ext.declarative.api.numeric.MutableNumericValue;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.extension.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.api.numeric.NumericValuePush;
import com.fluxtion.runtime.event.Event;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.lang.model.type.TypeKind;
import org.apache.velocity.VelocityContext;
import com.fluxtion.extension.declarative.builder.factory.FunctionKeys;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.stateful;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.targetClassFqn;
import java.util.Map;
import java.util.Set;
import static com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper.numericGetMethod;
import static com.fluxtion.extension.declarative.builder.factory.FunctionGeneratorHelper.numericSetMethod;
import static com.fluxtion.extension.declarative.builder.factory.FunctionKeys.imports;
import com.fluxtion.extension.declarative.builder.util.ImportMap;

/**
 *
 * @author greg
 */
public class NumericArrayFunctionBuilder {

    private final Class<? extends NumericArrayFunctionStateless> wrappedfunctionClass;
    private static final String TEMPLATE = "template/NumericArrayFunctionWrapperTemplate.vsl";
    private final HashMap<Class, ArraySourceInfo> inst2SourceInfo = new HashMap<>();
    private final ArrayList<ResultTarget> resultTargets;
    private FunctionInfo functionInfo;
    private boolean statefulFunction = false;
    private Object resetNotifier;
    private final ImportMap importMap = ImportMap.newMap(Initialise.class, OnEvent.class,
            OnEventComplete.class, OnParentUpdate.class, NumericValuePush.class,NumericValue.class );

    private NumericArrayFunctionBuilder(Class<? extends NumericArrayFunctionStateless> function) {
        this.resultTargets = new ArrayList<>();
        this.wrappedfunctionClass = function;
        statefulFunction = NumericArrayFunctionStateful.class.isAssignableFrom(function) ;
    }

    public static NumericArrayFunctionBuilder function(Class<? extends NumericArrayFunctionStateless> function) {
        NumericArrayFunctionBuilder builder = new NumericArrayFunctionBuilder(function);
        builder.checkFunction();
        return builder;
    }

    public static <S extends Event> NumericValue buildFilteredFunction(
            Class<? extends NumericArrayFunctionStateless> function,
            Class<S> eventClass,
            Function<S, ? super Number> sourceFunction,
            Object resetNotifier,
            String... filterString) {
        try {
            NumericArrayFunctionBuilder builder = new NumericArrayFunctionBuilder(function);
            builder.checkFunction();
            EventWrapper<S>[] wrappers = EventSelect.select(eventClass, filterString);
            builder.input(sourceFunction, wrappers);
            if(resetNotifier!=null) builder.resetNotifier(resetNotifier);
            return builder.build();
        } catch (Exception exception) {
            throw new RuntimeException("unable to build function:" + function.getName(), exception);
        }
    }

    public static <S extends Event> NumericValue buildFunction(
            Class<? extends NumericArrayFunctionStateless> function,
            Class<S> eventClass,
            Function<S, ? super Number> sourceFunction,
            String... filterString) {
        return buildFilteredFunction(function, eventClass, sourceFunction, null, filterString);
    }

    public static <S extends Event> NumericValue buildFilteredFunction(
            Class<? extends NumericArrayFunctionStateless> function,
            Class<S> eventClass,
            Function<S, ? super Number> sourceFunction,
            Object resetNotifier,
            int... filterString) {
        try {
            NumericArrayFunctionBuilder builder = new NumericArrayFunctionBuilder(function);
            builder.checkFunction();
            EventWrapper<S>[] wrappers = EventSelect.select(eventClass, filterString);
            builder.input(sourceFunction, wrappers);
            if(resetNotifier!=null) builder.resetNotifier(resetNotifier);
            return builder.build();
        } catch (Exception exception) {
            throw new RuntimeException("unable to build function:" + function.getName(), exception);
        }
    }

    public static <S extends Event> NumericValue buildFunction(
            Class<? extends NumericArrayFunctionStateless> function,
            Class<S> eventClass,
            Function<S, ? super Number> sourceFunction,
            int... filterString) {
        return buildFilteredFunction(function, eventClass, sourceFunction, null, filterString);
    }

    public NumericArrayFunctionBuilder input(NumericValue... input) {
        ArraySourceInfo sourceInfo = addSource(input);
        return this;
    }

    public <K> NumericArrayFunctionBuilder input(Function<K, ? super Number> sourceFunction, K... input) {
        return input(sourceFunction, false, input);
    }

    public <K> NumericArrayFunctionBuilder input(
            Function<K, ? super Number> sourceFunction,
            boolean cast,
            K... input
    ) {
        Method sourceMethod = numericGetMethod(input[0], sourceFunction);
        ArraySourceInfo sourceInfo = addSource(sourceMethod, cast, input);
        return this;
    }

    public <S extends Event> NumericArrayFunctionBuilder input(
            Function<S, ? super Number> sourceFunction,
            EventWrapper<S>... handler) {
        return input(sourceFunction, false, handler);
    }

    public <S extends Event> NumericArrayFunctionBuilder input(
            Function<S, ? super Number> sourceFunction,
            boolean cast,
            EventWrapper<S>... handler) {
        Method sourceMethod = numericGetMethod((Class<S>) handler[0].eventClass(), sourceFunction);
        ArraySourceInfo sourceInfo = addSource(sourceMethod, cast, handler);
        return this;
    }
    
    public <S> NumericArrayFunctionBuilder input(
            Function<S, ? super Number> sourceFunction,
            Wrapper<S>... handler) {
        return input(sourceFunction, false, handler);
    }

    public <S> NumericArrayFunctionBuilder input(
            Function<S, ? super Number> sourceFunction,
            boolean cast,
            Wrapper<S>... handler) {
        Method sourceMethod = numericGetMethod((Class<S>) handler[0].eventClass(), sourceFunction);
        ArraySourceInfo sourceInfo = addSource(sourceMethod, cast, handler);
        return this;
    }
    
    public NumericArrayFunctionBuilder resetNotifier(Object resetNotifier) {
        this.resetNotifier = resetNotifier;
        return this;
    }

    public <T> NumericArrayFunctionBuilder push(MutableNumericValue target) {
        resultTargets.add(new ResultTarget(target));
        return this;
    }
    
    public <T> NumericArrayFunctionBuilder push(T target, BiConsumer<T, ? super Byte> targetFunction) {
        Method methodFromLambda = numericSetMethod(target, targetFunction);
        resultTargets.add(new NumericArrayFunctionBuilder.ResultTarget(target, methodFromLambda));
        return this;
    }
    
    public <T> NumericArrayFunctionBuilder pushChar(T target, BiConsumer<T, ? super Character> targetFunction) {
        Method methodFromLambda = FunctionGeneratorHelper.setCharMethod(target, targetFunction);
        resultTargets.add(new NumericArrayFunctionBuilder.ResultTarget(target, methodFromLambda));
        return this;
    }

    public NumericValue build() {
        try{
            VelocityContext ctx = new VelocityContext();
            String genClassName = wrappedfunctionClass.getSimpleName() + "Invoker_" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put(outputClass.name(), functionInfo.returnType);
            ctx.put(targetClass.name(), functionInfo.calculateClass);
//            ctx.put(targetClassFqn.name(), functionInfo.calculateClassFqn);
            importMap.addImport(functionInfo.functionMethod.getDeclaringClass());
            ctx.put(targetMethod.name(), functionInfo.calculateMethod);
            ctx.put(stateful.name(), statefulFunction);
            if (resetNotifier != null) {
                ctx.put(FunctionKeys.resetNotifier.name(), resetNotifier);
            }
            ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
            ctx.put(imports.name(), importMap.asString());
            
            Class<NumericValue> aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
            NumericValue result = aggClass.newInstance();
            //set sources via reflection
            Set<Map.Entry<Class, ArraySourceInfo>> entrySet = inst2SourceInfo.entrySet();
            for (Map.Entry<Class, ArraySourceInfo> entry : entrySet) {
                ArrayList instances = entry.getValue().sourceInstances;
                String fieldName = entry.getValue().id;
                Object[] arrayField = (Object[]) aggClass.getField(fieldName).get(result);
                for (int j = 0; j < instances.size(); j++) {
                    Object source = instances.get(j);
                    arrayField[j] = source;
                }
            }
            for (ResultTarget resultTarget : resultTargets) {
                resultTarget.buildPush(result);
            }
            if (resetNotifier != null) {
                aggClass.getField("resetNotifier").set(result, resetNotifier);
            }
            GenerationContext.SINGLETON.getNodeList().add(result);
        return result;
                } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + toString(), e);
        }
    }

    /**
     * Checks the wrapped numeric function to ensure it has only one public
     * method.
     *
     * Update also has the getTempate method for dynamically generated
     * functions.
     *
     */
    private void checkFunction() {
        Method[] methods = wrappedfunctionClass.getDeclaredMethods();
        Method calcMethod = null;
        if (statefulFunction) {
            if (methods.length > 2 || methods.length < 1) {
                throw new RuntimeException("Cannot generate numeric function from "
                        + "supplied function class must have minimum 1 and maximum 2"
                        + " public methods, where reset() is the second method.");
            }
            if (methods[0].getName().equalsIgnoreCase("reset")) {
                calcMethod = methods[1];
            } else {
                calcMethod = methods[0];
            }
        } else {
            if (methods.length != 1) {
                throw new RuntimeException("Cannot generate numeric function from "
                        + "supplied function class must have 1 public method.");

            }
            calcMethod = methods[0];

        }
        functionInfo = new FunctionInfo(calcMethod);
    }    

    private ArraySourceInfo addSource(NumericValue... input) {
        final String methodSourceName = functionInfo.returnType + "Value";
        try {
            Method getMethod = NumericValue.class.getMethod(methodSourceName);
            Class componentClass = input.getClass().getComponentType();
            ArraySourceInfo srcInfo = inst2SourceInfo.computeIfAbsent(componentClass, (in) -> new ArraySourceInfo(
                    input[0].getClass(),
                    getMethod,
                    false
            ));
            srcInfo.addInstances((Object[]) input);
            return srcInfo;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot find source method " + methodSourceName + " " + toString());
        }
    }

    private <T extends EventWrapper> ArraySourceInfo addSource(Method getMethod, boolean cast, T... input) {
        Class componentClass = input[0].eventClass();
        ArraySourceInfo srcInfo = inst2SourceInfo.computeIfAbsent(componentClass, (in) -> new ArraySourceInfo(
                input[0].getClass(),
                getMethod,
                cast
        ));
        srcInfo.addInstances((Object[]) input);
        return srcInfo;
    }

    private <T extends Wrapper> ArraySourceInfo addSource(Method getMethod, boolean cast, T... input) {
        Class componentClass = input[0].eventClass();
        ArraySourceInfo srcInfo = inst2SourceInfo.computeIfAbsent(componentClass, (in) -> new ArraySourceInfo(
                input[0].getClass(),
                getMethod,
                cast
        ));
        srcInfo.addInstances((Object[]) input);
        return srcInfo;
    }

    private <T> ArraySourceInfo addSource(Method getMethod, boolean cast, T... input) {

        Class componentClass = input.getClass().getComponentType();
        ArraySourceInfo srcInfo = inst2SourceInfo.computeIfAbsent(componentClass, (in) -> new ArraySourceInfo(
                componentClass,
                getMethod,
                cast
        ));
        srcInfo.addInstances(input);
        return srcInfo;

    }


    public static class FunctionInfo {

        public String returnType;
        public Class returnTypeClass;
        public String calculateMethod;
        public String calculateClass;
        public String calculateClassFqn;
        private Method functionMethod;

        public FunctionInfo(Method method) {
            functionMethod = method;
            calculateMethod = method.getName();
            calculateClass = method.getDeclaringClass().getSimpleName();
            calculateClassFqn = method.getDeclaringClass().getCanonicalName();
            returnType = method.getReturnType().getName();
            returnTypeClass = method.getReturnType();
        }

        private TypeKind getReturnTypeKind() {
            return TypeKind.valueOf(returnType.toUpperCase());
        }

        @Override
        public String toString() {
            return "FunctionInfo{" + ", returnType=" + returnType + ", calculateMethod=" + calculateMethod + '}';
        }

    }

    private class ResultTarget<T> {

        private final TypeKind type;
        private final T targetInstance;
        private BiConsumer<T, ? super Number> targetFunction;
        private Method targetMethod;

        public ResultTarget(MutableNumericValue targetInstance) {
            //use type inference for which set method to call
            this.targetInstance = (T) targetInstance;
            type = functionInfo.getReturnTypeKind();
            final String className = type.name().toLowerCase();
            String methodName = "set" + className.toUpperCase().charAt(0) + className.substring(1) + "Value";
            try {
                targetMethod = MutableNumericValue.class.getMethod(methodName, functionInfo.returnTypeClass);
            } catch (Exception exception) {
                throw new RuntimeException("cannot build target set method name:" + methodName, exception);
            }
        }

        public ResultTarget(T targetInstance, Method targetMethod) {
            this.targetInstance = targetInstance;
            this.targetMethod = targetMethod;
            type = functionInfo.getReturnTypeKind();
            //infer type from the target type?
        }

        void buildPush(NumericValue result) throws Exception {
            String getName = type.name().toLowerCase() + "Value";
            Method getter = NumericValue.class.getMethod(getName);
            NumericValuePushFactory.generatePush(result, getter, targetInstance, targetMethod);
        }

        @Override
        public String toString() {
            return "ResultTarget{" + "type=" + type + ", targetInstance=" + targetInstance + ", targetFunction=" + targetFunction + '}';
        }
    }

    @Override
    public String toString() {
        return "NumericFunctionBuilder{" + "functionClass=" + wrappedfunctionClass + ", functionInfo=" + functionInfo + ", inst2SourceInfo=" + inst2SourceInfo + '}';
    }

}
