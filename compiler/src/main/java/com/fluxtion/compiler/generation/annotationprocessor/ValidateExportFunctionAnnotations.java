package com.fluxtion.compiler.generation.annotationprocessor;

import com.fluxtion.runtime.annotations.ExportFunction;
import com.fluxtion.runtime.callback.ExportFunctionNode;
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
public class ValidateExportFunctionAnnotations extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<? extends Element> typeElements = annotatedElements.stream()
                    .filter(element ->
                            {
                                TypeKind returnType = ((ExecutableType) element.asType()).getReturnType().getKind();
                                boolean validReturn = returnType == TypeKind.BOOLEAN || returnType == TypeKind.VOID;
                                return !validReturn || !element.getModifiers().contains(Modifier.PUBLIC);
                            }
                    )
                    .collect(Collectors.toSet());

            TypeElement exportNodeType = processingEnv.getElementUtils().getTypeElement(ExportFunctionNode.class.getCanonicalName());
            Set<Element> failingSubClass = annotatedElements.stream()
                    .map(Element::getEnclosingElement)
                    .filter(element -> {
                        return !processingEnv.getTypeUtils().isSubtype(element.asType(), exportNodeType.asType());
                    })
                    .collect(Collectors.toSet());

            typeElements.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "exported method should be public method and a boolean return type ", element));

            failingSubClass.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "class with exported method must extend " + ExportFunctionNode.class.getCanonicalName(), element));

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
        supportedAnnotations.add(ExportFunction.class.getCanonicalName());
        return supportedAnnotations;
    }
}
