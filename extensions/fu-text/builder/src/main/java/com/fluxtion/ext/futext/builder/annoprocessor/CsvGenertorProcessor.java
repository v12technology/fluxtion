package com.fluxtion.ext.futext.builder.annoprocessor;

import com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder;
import com.fluxtion.ext.futext.builder.csv.CsvToBeanBuilder;
import com.google.auto.service.AutoService;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 *
 * @author gregp
 */
@SupportedAnnotationTypes(
        "com.fluxtion.ext.futext.api.annotation.CsvMarshaller")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CsvGenertorProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("TESTING!!!!");
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {

                System.out.println("asType:" + annotatedElement.asType());
                System.out.println("package:" + annotatedElement.getEnclosingElement().toString());
                System.out.println("class:" + annotatedElement.getSimpleName());
                try {
                    Class t = Class.forName(annotatedElement.asType().toString());

//                    processingEnv.getFiler().createSourceFile(name, originatingElements)
                    CsvToBeanBuilder.nameSpace(t.getPackage().getName()).mapBean("NewBean", t);
                    System.out.println("built mapper");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CsvGenertorProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

//        throw new RuntimeException("annotation processor - CsvGenertorProcessor working");
        return false;
    }
}
