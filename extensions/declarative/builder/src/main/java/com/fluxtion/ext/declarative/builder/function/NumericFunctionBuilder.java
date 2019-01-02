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
package com.fluxtion.ext.declarative.builder.function;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateless;
import com.fluxtion.ext.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.ext.declarative.builder.util.FunctionInfo;
import com.fluxtion.ext.declarative.builder.util.SourceInfo;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.input;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.outputClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceMappingList;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetMethod;
import com.fluxtion.ext.declarative.builder.factory.NumericValuePushFactory;
import com.fluxtion.ext.declarative.api.numeric.MutableNumericValue;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.api.event.Event;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import javax.lang.model.type.TypeKind;
import org.apache.velocity.VelocityContext;
import com.fluxtion.ext.declarative.builder.factory.FunctionKeys;
import java.util.Map;
import java.util.Set;
import static com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper.numericGetMethod;
import static com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper.numericSetMethod;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.stateful;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.numeric.NumericValuePush;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection.SerializableSupplier;
import com.fluxtion.ext.declarative.api.window.CountSlidingBuffer;
import com.fluxtion.ext.declarative.builder.window.CountSlidingBufferFactory;
import com.fluxtion.ext.declarative.api.window.UpdateCountTest;
import com.fluxtion.ext.declarative.builder.Templates;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.imports;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import com.fluxtion.ext.declarative.builder.window.CountNotifierBuilder;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Test comment
 *
 * @author greg
 */
public class NumericFunctionBuilder {

    private final Class<? extends NumericFunctionStateless> wrappedfunctionClass;
    private static final String TEMPLATE = Templates.PACKAGE + "/NumericFunctionWrapperTemplate.vsl";

    private final HashMap<Object, SourceInfo> inst2SourceInfo = new HashMap<>();
    private final ArrayList<ResultTarget> resultTargets;
    FunctionInfo functionInfo;
    private Object resetNotifier;
    private boolean statefulFunction = false;
    private WindowType windowType;
    private Object windowedInput;
    private Method windowedMethod;
    private final ImportMap importMap = ImportMap.newMap(Initialise.class, OnEvent.class,
            OnEventComplete.class, OnParentUpdate.class,
            NumericValuePush.class, NumericValue.class);
    //class cache
    private FunctionClassCacheKey key;
    private static HashMap<FunctionClassCacheKey, Class> classCache = new HashMap<>();
    /**
     * size of the window in seconds or batch
     */
    private int windowSize;

    /**
     * size of the sliding window, item counts between publishes for a sliding
     * window.
     */
    private int slideSize;
    private Test windowCache;
    private CountSlidingBuffer slidingWindow;
    private String windowDataAccessString;
    private SourceInfo slidingSrcInfo;
    private String windowInputDatatType;

    private enum WindowType {
        none, countTumble, countSliding, timeTumble, timeSliding;
    }

    private NumericFunctionBuilder(Class<? extends NumericFunctionStateless> function) {
        this.resultTargets = new ArrayList<>();
        this.wrappedfunctionClass = function;
        statefulFunction = NumericFunctionStateful.class.isAssignableFrom(function);
        key = new FunctionClassCacheKey(wrappedfunctionClass);
    }

    public static NumericFunctionBuilder function(Class<? extends NumericFunctionStateless> function) {
        NumericFunctionBuilder builder = new NumericFunctionBuilder(function);
        builder.checkFunction();
        if (NumericFunctionStateful.class.isAssignableFrom(function)) {
            builder.functionInfo.appendPreviousResult();
        }
        return builder;
    }

    public NumericFunctionBuilder input(NumericValue input) {
        SourceInfo sourceInfo = addSource(input);
        functionInfo.appendParamNumeric(input, sourceInfo);
        Method m = FunctionGeneratorHelper.methodFromLambda(NumericValue.class, (Function<NumericValue, ?>) NumericValue::byteValue);
        key.addSourceMethod(m);
        return this;
    }

    public <K> NumericFunctionBuilder input(K input, Function<K, ? super Number> sourceFunction) {
        return input(input, sourceFunction, false);
    }

    public <K> NumericFunctionBuilder input(K input,
            Function<K, ? super Number> sourceFunction,
            boolean cast) {
        Method sourceMethod = numericGetMethod(input, sourceFunction);
        SourceInfo sourceInfo = addSource(input);
        functionInfo.appendParamSource(sourceMethod, sourceInfo, cast);
        key.addSourceMethod(sourceMethod);
        return this;
    }

    public <T, V extends Number> NumericFunctionBuilder input(T input,
            SerializableSupplier<T, V> sourceFunction) {
        return input(input, sourceFunction, false);
    }

    public <T, V extends Number> NumericFunctionBuilder input(T input,
            SerializableSupplier<T, V> sourceFunction, boolean cast) {
        Method sourceMethod = sourceFunction.method();
        SourceInfo sourceInfo = addSource(input);
        functionInfo.appendParamSource(sourceMethod, sourceInfo, cast);
        key.addSourceMethod(sourceMethod);
        return this;
    }

    public NumericFunctionBuilder input(Number number) {
        windowType = WindowType.none;
        functionInfo.appendParamLocal(number.toString(), true);
        return this;
    }

    public NumericFunctionBuilder resetNotifier(Object resetNotifier) {
        this.resetNotifier = resetNotifier;
        return this;
    }

    public NumericFunctionBuilder countWin(int count) {
        if (windowType != WindowType.none) {
            windowType = WindowType.countTumble;
        }
        windowSize = count;
        return this;
    }

    public NumericFunctionBuilder countSlideWin(int count, int publishFrequency) {
        if (windowType != WindowType.none) {
            windowType = WindowType.countSliding;
        }
        windowSize = count;
        slideSize = Math.min(count, publishFrequency);
        return this;
    }

    public < S extends Event> NumericFunctionBuilder input(Class<S> eventClass, Function<S, ? super Number> sourceFunction) {
        return input(EventSelect.select(eventClass), sourceFunction);
    }

    public < S extends Event> NumericFunctionBuilder input(Class<S> eventClass, Function<S, ? super Number> sourceFunction, boolean isCast) {
        return input(EventSelect.select(eventClass), sourceFunction, isCast);
    }

    public <S extends Event> NumericFunctionBuilder input(EventWrapper<S> handler, Function<S, ? super Number> sourceFunction) {
        return input(handler, sourceFunction, false);
    }

    public <S extends Event> NumericFunctionBuilder input(EventWrapper<S> handler, Function<S, ? super Number> sourceFunction, boolean isCast) {
        Method sourceMethod = numericGetMethod((Class<S>) handler.eventClass(), sourceFunction);
        SourceInfo sourceInfo = addSource(handler);
        functionInfo.appendParamSource(sourceMethod, sourceInfo, handler, isCast);
        setWindowMethod(sourceMethod);
        key.addSourceMethod(sourceMethod);
        return this;
    }

    private void setWindowMethod(Method sourceMethod) {
        if (windowedMethod == null) {
            windowedMethod = sourceMethod;
            String s = functionInfo.paramString;
            String[] splits = s.split(",");
            windowDataAccessString = splits[splits.length - 1];

            int paramIndex = splits.length > 1 ? 1 : 0;
            windowInputDatatType = functionInfo.paramTypeByIndex(paramIndex);
        }
    }

    public <S> NumericFunctionBuilder input(Wrapper<S> handler, Function<S, ? super Number> sourceFunction) {
        return input(handler, sourceFunction, false);
    }

    public <S> NumericFunctionBuilder input(Wrapper<S> handler, Function<S, ? super Number> sourceFunction, boolean isCast) {
        Method sourceMethod = numericGetMethod((Class<S>) handler.eventClass(), sourceFunction);
        SourceInfo sourceInfo = addSource(handler);
        functionInfo.appendParamSource(sourceMethod, sourceInfo, handler, isCast);
        setWindowMethod(sourceMethod);
        key.addSourceMethod(sourceMethod);
        return this;
    }

    public NumericFunctionBuilder push(MutableNumericValue target) {
        resultTargets.add(new ResultTarget(target));
        return this;
    }

    public <T> NumericFunctionBuilder push(T target, BiConsumer<T, ? super Byte> targetFunction) {
        Method methodFromLambda = numericSetMethod(target, targetFunction);
        resultTargets.add(new ResultTarget(target, methodFromLambda));
//        key.addSourceMethod(sourceMethod);
        return this;
    }

    public <T> NumericFunctionBuilder pushChar(T target, BiConsumer<T, ? super Character> targetFunction) {
        Method methodFromLambda = FunctionGeneratorHelper.setCharMethod(target, targetFunction);
        //methodFromLambda(target.getClass(), (ObjCharConsumer) targetFunction);
        resultTargets.add(new ResultTarget(target, methodFromLambda));
        return this;
    }

    public NumericValue build() {

        try {
            Class<NumericValue> aggClass = classCache.get(key);

            if (aggClass == null) {
                VelocityContext ctx = new VelocityContext();
                String genClassName = wrappedfunctionClass.getSimpleName() + "Invoker_" + GenerationContext.nextId();
                ctx.put(functionClass.name(), genClassName);
                ctx.put(outputClass.name(), functionInfo.returnType);
                ctx.put(targetClass.name(), importMap.addImport(functionInfo.calculateClazz));
                ctx.put(targetMethod.name(), functionInfo.calculateMethod);
                ctx.put(input.name(), functionInfo.paramString);
                ctx.put(imports.name(), importMap.asString());
                if (resetNotifier != null) {
                    ctx.put(FunctionKeys.resetNotifier.name(), resetNotifier);
                }
                ctx.put(stateful.name(), statefulFunction);
                ctx.put(sourceMappingList.name(), new ArrayList(inst2SourceInfo.values()));
                buildWindowSupport(ctx);
                aggClass = FunctionGeneratorHelper.generateAndCompile(null, TEMPLATE, GenerationContext.SINGLETON, ctx);
                classCache.put(key, aggClass);
            }
            NumericValue result = aggClass.newInstance();
            //set sources via reflection
            Set<Map.Entry<Object, SourceInfo>> entrySet = inst2SourceInfo.entrySet();
            for (Map.Entry<Object, SourceInfo> entry : entrySet) {
                Object source = entry.getKey();
                String fieldName = entry.getValue().id;
                aggClass.getField(fieldName).set(result, source);
            }
            for (ResultTarget resultTarget : resultTargets) {
                resultTarget.buildPush(result);
            }
            if (resetNotifier != null) {
                aggClass.getField("resetNotifier").set(result, resetNotifier);
            }
            if (windowType == WindowType.countTumble || windowType == WindowType.timeTumble) {
                aggClass.getField("tumbleWinNotifier").set(result, windowCache);
            } else if (windowType == WindowType.countSliding || windowType == WindowType.timeSliding) {
                aggClass.getField("tumbleWinNotifier").set(result, slidingWindow);
                aggClass.getField(slidingSrcInfo.id).set(result, null);
            }

            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not buuld function " + e.getMessage(), e);
        }
    }

    private void buildWindowSupport(VelocityContext ctx) {
        windowType = windowType == null ? WindowType.none : windowType;
        ctx.put("windowed", true);
        switch (windowType) {
            case countTumble:
                ctx.put("tumbleCountWindow", true);
                windowCache = CountNotifierBuilder.updateCount(windowedInput, windowSize);
                ctx.put(sourceClass.name(), windowCache.getClass().getCanonicalName());
                break;
            case countSliding:
                ctx.put("slidingCountWindow", true);
//                ctx.put("tumbleCountWindow", true);
                slidingWindow = CountSlidingBufferFactory.build(windowedInput, windowedMethod, windowSize, slideSize, windowDataAccessString, slidingSrcInfo);
                ctx.put(sourceClass.name(), slidingWindow.getClass().getCanonicalName());
                String inputString = functionInfo.paramString;
                int indexOf = inputString.indexOf(windowDataAccessString);
                String newInput = inputString.substring(0, indexOf) + " (" + windowInputDatatType + ")value"
                        + inputString.substring(indexOf + windowDataAccessString.length());
                ctx.put(input.name(), newInput);
                ctx.put("windowedDataClass", windowedMethod.getReturnType().getName());
                //the array cache
                //UpdateCountTest.updateCount(windowedInput, windowSize);
                break;
            case timeTumble:
                ctx.put("tumbleTimeWindow", true);
                windowCache = CountNotifierBuilder.updateCount(windowedInput, windowSize);
                ctx.put(sourceClass.name(), windowCache.getClass().getCanonicalName());
                break;
            case timeSliding:
                ctx.put("slidingTimeWindow", true);
                //add the time counter
                CountNotifierBuilder.updateCount(windowedInput, windowSize);
//                break;
            case none:
            default:
                ctx.put("windowed", false);
            //do nothing
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

    private SourceInfo addSource(Object input) {
        SourceInfo srcInfo = inst2SourceInfo.computeIfAbsent(input, (in) -> new SourceInfo(
                importMap.addImport(input.getClass()),
                "source_" + input.getClass().getSimpleName() + "_" + GenerationContext.nextId()));
        if (windowedInput == null) {
            windowedInput = input;
            this.slidingSrcInfo = srcInfo;
        }
        return srcInfo;
    }

    private class ResultTarget<T> {

        private TypeKind type;
        private T targetInstance;
//        private BiConsumer<T, ? super Number> targetFunction;
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
            return "ResultTarget{" + "type=" + type + ", targetInstance=" + targetInstance + ", targetFunction=" + '}';
        }
    }

    @Override
    public String toString() {
        return "NumericFunctionBuilder{" + "functionClass=" + wrappedfunctionClass + ", functionInfo=" + functionInfo + ", inst2SourceInfo=" + inst2SourceInfo + '}';
    }

    private static class FunctionClassCacheKey {

        private final ArrayList<Method> sourceMethods = new ArrayList<>();
        private final Class<? extends NumericFunctionStateless> wrappedfunctionClass;

        public FunctionClassCacheKey(Class<? extends NumericFunctionStateless> wrappedfunctionClass) {
            this.wrappedfunctionClass = wrappedfunctionClass;
        }

        public void addSourceMethod(Method m) {
            sourceMethods.add(m);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.sourceMethods);
            hash = 67 * hash + Objects.hashCode(this.wrappedfunctionClass);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FunctionClassCacheKey other = (FunctionClassCacheKey) obj;
            if (!Objects.equals(this.sourceMethods, other.sourceMethods)) {
                return false;
            }
            if (!Objects.equals(this.wrappedfunctionClass, other.wrappedfunctionClass)) {
                return false;
            }
            return false;
        }

    }
}
