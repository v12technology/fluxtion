package com.fluxtion.ext.text.builder.csv;

import com.fluxtion.builder.annotation.ClassProcessor;
import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.ext.text.api.annotation.ConvertField;
import com.fluxtion.ext.text.api.annotation.CsvMarshaller;
import com.fluxtion.ext.text.api.annotation.OptionalField;
import com.fluxtion.ext.text.api.annotation.TrimField;
import com.fluxtion.generator.Generator;
import com.google.auto.service.AutoService;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.reflect.dsl.MethodReflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gregp
 */
@AutoService(ClassProcessor.class)
public class CsvAnnotationBeanBuilder implements ClassProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(CsvAnnotationBeanBuilder.class.getName());
    private File generatedDir;
    private File resourceDir;
    private File rootDir;
    private boolean warmup = true;

    @Override
    public void outputDirectories(File rootDir, File output, File resourceDir) {
        this.rootDir = rootDir;
        this.generatedDir = output;
        this.resourceDir = resourceDir;
    }

    @Override
    public void process(URL classPath) {
        if (warmup) {
            new Thread(Generator::warmupCompiler).start();
            warmup = false;
        }
        try {
            File fin = new File(classPath.toURI());
            LOGGER.info("CsvAnnotationBeanBuilder scanning url:'{}' for CSVMarshaller annotations", fin);
            try (ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClasspath(fin)
                    .scan()) {
                ClassInfoList csvList = scanResult.getClassesWithAnnotation(CsvMarshaller.class.getCanonicalName());
                for (ClassInfo csvClassInfo : csvList) {
                    final Class<?> csvClass = csvClassInfo.loadClass();

                    CsvMarshaller annotation = csvClass.getAnnotation(CsvMarshaller.class);
                    LOGGER.info("re-use flag:" + annotation.newBeanPerRecord());

                    AnnotationInfo annotationInfo = csvClassInfo.getAnnotationInfo(Disabled.class.getCanonicalName());
                    if (annotationInfo == null) {
                        LOGGER.info("Fluxtion generating CSV marshaller for:" + csvClass.getCanonicalName());
                        AnnotationParameterValueList params = csvClassInfo.getAnnotationInfo(CsvMarshaller.class.getCanonicalName()).getParameterValues();
                        Object pkgNameVal = params.get("packageName");
                        String pkgName = pkgNameVal == null ? csvClassInfo.getPackageName() : pkgNameVal.toString();
                        CsvToBeanBuilder beanBuilder = CsvToBeanBuilder.nameSpace(pkgName);
                        if (generatedDir != null && resourceDir != null) {
                            beanBuilder.setOutputDirs(generatedDir.getCanonicalPath(), resourceDir.getCanonicalPath());
                        }
                        CsvMarshallerBuilder<?> builder = beanBuilder.builder(csvClass, annotation.headerLines());
                        builder.includeEventPublisher(annotation.addEventPublisher());
                        builder.mappingRow(annotation.mappingRow());
                        builder.processEscapeSequences(annotation.processEscapeSequences());
                        builder.skipCommentLines(annotation.skipCommentLines());
                        builder.skipEmptyLines(annotation.skipEmptyLines());
                        builder.reuseTarget(!annotation.newBeanPerRecord());
                        builder.tokenConfig( new CharTokenConfig(annotation.lineEnding(), annotation.fieldSeparator(), annotation.ignoredChars()));
                        Arrays.stream(Introspector.getBeanInfo(csvClass).getPropertyDescriptors()).forEach(p -> fieldMarshallerConfig(p, builder));
                        builder.build();
                    } else {
                        LOGGER.info("disabled Fluxtion CSV generation for:" + csvClass.getCanonicalName());
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("problem generating csv marshallers", ex);
            }

        } catch (URISyntaxException ex) {
            LOGGER.error("problem generating csv marshallers", ex);
        }
        LOGGER.info("CsvAnnotationBeanBuilder completed");
    }

    private void fieldMarshallerConfig(PropertyDescriptor md, CsvMarshallerBuilder builder) {
        if (md.getWriteMethod() != null) {
            Mirror m = new Mirror();
            Field field;// = clazz.getDeclaredField(md.getName());
            final Class targetClazz = builder.getTargetClazz();
            field = m.on(targetClazz).reflect().field(md.getName());
            field.setAccessible(true);
            final String fieldName = md.getName();
            final TrimField trim = field.getAnnotation(TrimField.class);
            if (trim!=null && trim.value()) {
                builder.trim(fieldName);
            }
            final ConvertField converter = field.getAnnotation(ConvertField.class);
            if(converter!=null){
                String[] converterString = converter.value().split("#");
                MethodReflector method = m.on(converterString[0]).reflect().method(converterString[1]);
                builder.converterMethod(fieldName, method.withAnyArgs());
            }
        }
    }

}
