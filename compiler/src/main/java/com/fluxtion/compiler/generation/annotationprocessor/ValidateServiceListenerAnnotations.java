package com.fluxtion.compiler.generation.annotationprocessor;

import com.fluxtion.runtime.annotations.runtime.ServiceDeregistered;
import com.fluxtion.runtime.annotations.runtime.ServiceRegistered;
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
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class ValidateServiceListenerAnnotations extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            Set<? extends Element> typeElements = annotatedElements.stream()
                    .filter(this::inValidMethod)
                    .collect(Collectors.toSet());

            typeElements.forEach(element ->
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "service listener method should be public method with void return type "
                            + "accepting a single argument or an optional second String argument that is the service name " +
                            "failing method:" + element.getSimpleName(), element));

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
        supportedAnnotations.add(ServiceRegistered.class.getCanonicalName());
        supportedAnnotations.add(ServiceDeregistered.class.getCanonicalName());
        return supportedAnnotations;
    }

    private boolean inValidMethod(Element element) {
        ExecutableType method = (ExecutableType) element.asType();
        boolean invalid = method.getReturnType().getKind() != TypeKind.VOID;
        invalid |= !element.getModifiers().contains(Modifier.PUBLIC);

        final int parameterCount = method.getParameterTypes().size();

        if (parameterCount == 2) {
            TypeMirror arg2Type = method.getParameterTypes().get(1);
            TypeMirror charSeqType = processingEnv.getElementUtils()
                    .getTypeElement(CharSequence.class.getCanonicalName())
                    .asType();
            invalid |= !processingEnv.getTypeUtils().isAssignable(arg2Type, charSeqType);
        } else if (parameterCount != 1) {
            invalid = true;
        }

        return invalid;
    }
}
