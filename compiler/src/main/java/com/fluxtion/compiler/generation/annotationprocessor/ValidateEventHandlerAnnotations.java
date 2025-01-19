/*
 * Copyright (c) 2025 gregory higgins.
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

package com.fluxtion.compiler.generation.annotationprocessor;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class ValidateEventHandlerAnnotations extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<? extends Element> typeElements = annotatedElements.stream()
                    .filter(element -> {
                        OnEventHandler ehAnnotation = element.getAnnotation(OnEventHandler.class);
                        boolean missingReturn = ehAnnotation.propagate();
                        missingReturn &= ehAnnotation.failBuildIfMissingBooleanReturn();
                        missingReturn &= ((ExecutableType) element.asType()).getReturnType().getKind() != TypeKind.BOOLEAN;
                        boolean nonPublic = !element.getModifiers().contains(Modifier.PUBLIC);
                        boolean oneParams = ((ExecutableType) element.asType()).getParameterTypes().size() == 1;
                        return missingReturn | nonPublic | !oneParams;
                    })
                    .collect(Collectors.toSet());

            typeElements.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "event handler method should be public method and a boolean return type"
                                    + "with a single argument failing method:" + element.getSimpleName(), element));

        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(OnEventHandler.class.getCanonicalName());
        return supportedAnnotations;
    }
}
