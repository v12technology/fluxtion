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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.declarative.builder.factory.FunctionGeneratorHelper;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.functionClass;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.sourceMappingList;
import static com.fluxtion.ext.declarative.builder.factory.FunctionKeys.targetClass;
import com.fluxtion.ext.declarative.builder.util.ImportMap;
import com.fluxtion.ext.declarative.builder.util.LambdaReflection;
import com.fluxtion.ext.futext.api.csv.FailedValidationListener;
import com.fluxtion.ext.futext.api.csv.RowProcessor;
import com.fluxtion.ext.futext.api.csv.ValidationLogger;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import static com.fluxtion.ext.futext.builder.Templates.CSV_MARSHALLER;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.VelocityContext;

/**
 *
 * @author gregp
 * @param <P>
 * @param <T>
 */
public class RecordParserBuilder<P extends RecordParserBuilder<P, T>, T> {

    public static <T> FailedValidationListener<T> failedValidationListener(RowProcessor<T> rowProcessor) {
        FailedValidationListener<T> failureListener = new FailedValidationListener<>(rowProcessor);
        GenerationContext.SINGLETON.getNodeList().add(failureListener);
        return failureListener;
    }

    protected Class<T> targetClazz;
    protected String targetId;
    protected final ImportMap importMap;
    protected int headerLines;
    protected int mappingRow;
    protected String eventMethod;
    protected String eventCompleteMethod;
    protected boolean skipEmptyLines;
    protected boolean skipCommentLines;
    protected boolean processEscapeSequences;
    protected boolean acceptPartials;
    protected final ArrayList<CsvPushFunctionInfo> srcMappingList;
    protected int converterCount;
    protected Map<Object, String> converterMap;
    protected CharTokenConfig tokenCfg;
    protected boolean eventMethodValidates;
    protected boolean reuseTarget;
    protected final boolean fixedLen;

    protected RecordParserBuilder(Class<T> target, int headerLines, boolean fixedLen) {
        importMap = ImportMap.newMap();
        importMap.addImport(com.fluxtion.api.annotations.EventHandler.class);
        importMap.addImport(Initialise.class);
        importMap.addImport(CharEvent.class);
        importMap.addImport(HashMap.class);
        importMap.addImport(RowProcessor.class);
        importMap.addImport(EofEvent.class);
        importMap.addImport(Config.class);
        importMap.addImport(ValidationLogger.class);
        importMap.addImport(Inject.class);
        srcMappingList = new ArrayList<>();
        this.headerLines = headerLines;
        this.converterMap = new HashMap<>();
        this.tokenCfg = CharTokenConfig.UNIX;
        this.eventMethodValidates = false;
        this.reuseTarget = true;
        this.fixedLen = fixedLen;
        //
        targetClazz = target;
        targetId = "target";
        Method[] methods = target.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(OnEvent.class) != null) {
                eventMethod = method.getName();
                if (method.getReturnType() == boolean.class) {
                    eventMethodValidates = true;
                }
            }
            if (method.getAnnotation(OnEventComplete.class) != null) {
                importMap.addImport(OnEventComplete.class);
                eventCompleteMethod = method.getName();
            }
        }
    }

    public <S extends CharSequence, U> P converter(int colIndex, LambdaReflection.SerializableFunction<S, U> converterFunction) {
        importMap.addImport(NoEventReference.class);
        Method method = converterFunction.method();
        Class<?> declaringClass = method.getDeclaringClass();
        if (Modifier.isStatic(method.getModifiers())) {
            importMap.addStaticImport(declaringClass);
            colInfo(colIndex).setConverter(method);
        } else {
            Object captured = converterFunction.captured()[0];
            String localName = converterMap.computeIfAbsent(captured, (t) -> {
                String name = declaringClass.getSimpleName();
                name = name.toLowerCase().charAt(0) + name.substring(1) + "_" + converterCount++;
                GenerationContext.SINGLETON.getNodeList().add(captured);
                return name;
            });
            colInfo(colIndex).setConverter(localName, method, captured);
        }
        return (P) this;
    }

    public <S extends CharSequence, U> P converter(String colName, LambdaReflection.SerializableFunction<S, U> converterFunction) {
        importMap.addImport(NoEventReference.class);
        Method method = converterFunction.method();
        Class<?> declaringClass = method.getDeclaringClass();
        if (Modifier.isStatic(method.getModifiers())) {
            importMap.addStaticImport(declaringClass);
            colInfo(colName).setConverter(method);
        } else {
            Object captured = converterFunction.captured()[0];
            String localName = converterMap.computeIfAbsent(captured, (t) -> {
                String name = declaringClass.getSimpleName();
                name = name.toLowerCase().charAt(0) + name.substring(1) + "_" + converterCount++;
                GenerationContext.SINGLETON.getNodeList().add(captured);
                return name;
            });
            colInfo(colName).setConverter(localName, method, captured);
        }
        return (P) this;
    }

    public P headerLines(int lines) {
        this.headerLines = lines;
        return (P) this;
    }

    public P trim(int colIndex) {
        colInfo(colIndex).setTrim(true);
        return (P) this;
    }

    public P skipEmptyLines(boolean skipLines) {
        this.skipEmptyLines = skipLines;
        return (P) this;
    }

    public P skipCommentLines(boolean skipCommentLines) {
        this.skipCommentLines = skipCommentLines;
        return (P) this;
    }

    public P processEscapeSequences(boolean processEscapeSequences) {
        this.processEscapeSequences = processEscapeSequences;
        return (P) this;
    }

    public P tokenConfig(CharTokenConfig tokenCfg) {
        this.tokenCfg = tokenCfg;
        return (P) this;
    }

    public P acceptPartials(boolean acceptParials) {
        this.acceptPartials = acceptParials;
        return (P) this;
    }

    public P reuseTarget(boolean reuseTarget) {
        this.reuseTarget = reuseTarget;
        return (P) this;
    }

    public RowProcessor<T> build() {
        try {
            VelocityContext ctx = new VelocityContext();
            HashMap<String, String> convereters = new HashMap<>();
            HashMap<String, String> validators = new HashMap<>();
            converterMap.entrySet().stream().forEach((e) -> {
                convereters.put(e.getValue(), importMap.addImport(e.getKey().getClass()));
            });
            if (mappingRow > 0) {
                importMap.addImport(ArrayList.class);
            }
            String genClassName = targetClazz.getSimpleName() + "CsvMarshaller" + GenerationContext.nextId();
            ctx.put(functionClass.name(), genClassName);
            ctx.put("imports", importMap.asString());
            ctx.put(targetClass.name(), targetClazz.getSimpleName());
            ctx.put(sourceMappingList.name(), srcMappingList);
            ctx.put("headerPresent", headerLines > 0);
            ctx.put("headerLines", headerLines);
            ctx.put("mappingRowPresent", mappingRow > 0);
            ctx.put("mappingRow", mappingRow);
            ctx.put("skipEmptyLines", skipEmptyLines);
            ctx.put("skipCommentLines", skipCommentLines);
            ctx.put("processEscapeSequences", processEscapeSequences);
            ctx.put("acceptPartials", acceptPartials);
            ctx.put("fixedLen", fixedLen);
            ctx.put("newTarget", !reuseTarget);
            ctx.put("convereters", convereters);
            ctx.put("validators", validators);
            ctx.put("delimiter", StringEscapeUtils.escapeJava("" + tokenCfg.getFieldSeparator()));
            ctx.put("eol", StringEscapeUtils.escapeJava("" + tokenCfg.getLineEnding()));
            if (tokenCfg.getIgnoredChars() != Character.MIN_VALUE) {
                ctx.put("ignore", StringEscapeUtils.escapeJava("" + tokenCfg.getIgnoredChars()));
            }
            if (eventMethod != null) {
                ctx.put("eventMethod", eventMethod);
                ctx.put("eventMethodValidates", eventMethodValidates);
            }
            if (eventCompleteMethod != null) {
                ctx.put("eventCompleteMethod", eventCompleteMethod);
            }
            final Class<RowProcessor> aggClass = FunctionGeneratorHelper.generateAndCompile(null, CSV_MARSHALLER, GenerationContext.SINGLETON, ctx);
            final RowProcessor result = aggClass.newInstance();

            converterMap.entrySet().stream().forEach((e) -> {
                try {
                    Object source = e.getKey();
                    String fieldName = e.getValue();
                    aggClass.getField(fieldName).set(result, source);
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    throw new RuntimeException("could not build function " + ex.getMessage(), ex);
                }
            });
            GenerationContext.SINGLETON.getNodeList().add(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("could not build function " + e.getMessage(), e);
        }
    }

    //helper methods
    protected CsvPushFunctionInfo colInfo(int index) {
        return srcMappingList.stream().filter(c -> c.getFieldIndex() == index).findFirst().get();
    }

    protected CsvPushFunctionInfo colInfo(String name) {
        return srcMappingList.stream().filter(c -> c.getFieldName() != null && c.getFieldName().equals(name)).findFirst().get();
    }

    protected CsvPushFunctionInfo colInfo(Method accessor) {
        return srcMappingList.stream().filter(c -> c.getTargetMethod().equals(accessor)).findFirst().get();
    }

}
