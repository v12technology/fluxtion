package com.fluxtion.compiler.generation.annotationprocessor;

import com.fluxtion.runtime.annotations.OnTrigger;
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
public class ValidateOnTriggerAnnotations extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            OnTrigger triggerAnnotation = annotation.getAnnotation(OnTrigger.class);
            boolean requireGuard = triggerAnnotation != null && triggerAnnotation.failBuildIfNotGuarded();
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<? extends Element> typeElements = annotatedElements.stream()
                    .filter(element ->
                                    ((ExecutableType) element.asType()).getReturnType().getKind() != TypeKind.BOOLEAN
//                            (((ExecutableType) element.asType()).getReturnType().getKind() != TypeKind.BOOLEAN && requireGuard)
                                            || ((ExecutableType) element.asType()).getParameterTypes().size() != 0
                                            || !element.getModifiers().contains(Modifier.PUBLIC)
                    )
                    .collect(Collectors.toSet());

            typeElements.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "trigger method should be public method and a boolean return type"
                                    + "with no arguments failing method:" + element.getSimpleName(), element
                    )
            );
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
        supportedAnnotations.add(OnTrigger.class.getCanonicalName());
        return supportedAnnotations;
    }
}
